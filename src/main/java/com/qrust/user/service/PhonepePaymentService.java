package com.qrust.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.qrust.common.domain.order.MembershipOrderDetails;
import com.qrust.common.domain.order.OrderStatus;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;
import com.qrust.common.domain.order.QrStickerOrderDetails;
import com.qrust.common.repository.OrderRepository;
import com.qrust.user.api.dto.PlanType;
import com.qrust.user.api.dto.order.OrderItem;
import com.qrust.user.api.dto.order.OrderItemType;
import com.qrust.user.api.dto.order.MembershipOrderItem;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@ApplicationScoped
public class PhonepePaymentService {

    @ConfigProperty(name = "phonepe.client-id")
    String clientId;

    @ConfigProperty(name = "phonepe.client-secret")
    String clientSecret;

    @ConfigProperty(name = "phonepe-webhook.username")
    String webhookUsername;

    @ConfigProperty(name = "phonepe-webhook.password")
    String webhookPassword;

    Integer clientVersion = 1;
    Env env = Env.SANDBOX;

    @ConfigProperty(name = "app.frontend.uri")
    String frontendUri;

    StandardCheckoutClient client;

    @Inject
    CognitoService cognitoService;

    @Inject
    UserService userService;

    @Inject
    OrderRepository orderRepository;

    @PostConstruct
    void init() {
        client = StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
    }

    public StandardCheckoutPayResponse createOrder(List<OrderItem> orderItems) {
        String merchantOrderId = "qrust-"+ UUID.randomUUID();
        Log.infof("Creating order with ID: %s", merchantOrderId);

        saveOrderToRepository(merchantOrderId, orderItems);

        int amount = computeOrderTotal(orderItems) * 100;

        String redirectUrl = String.format("%s/payment-status?merchantOrderId=%s", frontendUri, merchantOrderId);

        MetaInfo metaInfo = MetaInfo.builder()
                .udf1(merchantOrderId)
                .build();

        StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .metaInfo(metaInfo)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .build();

        return client.pay(standardCheckoutPayRequest);
    }

    public OrderStatusResponse fetchOrderStatus(String merchantOrderId) {
        return client.getOrderStatus(merchantOrderId);
    }

    public void handleWebhook(String receivedAuth, String body) {
        String authString = webhookUsername + ":" + webhookPassword;
        String expectedHash = sha256Hex(authString);
        if (!receivedAuth.equals(expectedHash)) {
            throw new RuntimeException("Unauthorized: Invalid Authorization header");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode payload = root.path("payload");
            String orderStatus = payload.path("state").asText();
            String merchantOrderId = payload.path("metaInfo").path("udf1").asText();

            List<PaymentOrder> paymentOrders = orderRepository.getAllByMerchantOrderId(merchantOrderId);

            if (paymentOrders.isEmpty()) {
                Log.warnf("No orders found for merchantOrderId: %s", merchantOrderId);
                return;
            }

            for (PaymentOrder paymentOrder : paymentOrders) {
                if(Objects.equals(orderStatus, PaymentStatus.COMPLETED.name())) {
                    if(Objects.equals(paymentOrder.getOrderItemType(), OrderItemType.MEMBERSHIP)) {
                        MembershipOrderDetails membershipDetails = (MembershipOrderDetails) paymentOrder.getOrderDetails();
                        cognitoService.upgradeUserGroup(paymentOrder.getUserId(), membershipDetails.getPlanType().name());
                        paymentOrder.setPaymentStatus(PaymentStatus.COMPLETED);
                    }else if(Objects.equals(paymentOrder.getOrderItemType(), OrderItemType.QR_STICKER)){
                        paymentOrder.setOrderStatus(OrderStatus.CREATED);
                        paymentOrder.setPaymentStatus(PaymentStatus.COMPLETED);
                    }
                }else if(Objects.equals(orderStatus, PaymentStatus.FAILED.name())) {
                    paymentOrder.setPaymentStatus(PaymentStatus.FAILED);
                } else {
                    Log.warnf("Unhandled state: %s for order: %s", orderStatus, merchantOrderId);
                }

                orderRepository.save(paymentOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing webhook: " + e.getMessage(), e);
        }
    }

    private int computeOrderTotal(List<OrderItem> orderItems) {
        boolean hasQRSticker = false;
        boolean hasBasicMembership = false;

        // First pass: check if we have both a QR sticker and a basic membership
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getOrderItemType() == OrderItemType.QR_STICKER) {
                hasQRSticker = true;
            } else if (orderItem.getOrderItemType() == OrderItemType.MEMBERSHIP) {
                MembershipOrderItem membershipItem = (MembershipOrderItem) orderItem;
                if (membershipItem.getPlanType() == PlanType.BASIC) {
                    hasBasicMembership = true;
                }
            }
        }

        int total = 0;
        // Second pass: calculate total with special pricing if needed
        for (OrderItem orderItem : orderItems) {
            if (hasQRSticker && hasBasicMembership && 
                orderItem.getOrderItemType() == OrderItemType.MEMBERSHIP) {
                MembershipOrderItem membershipItem = (MembershipOrderItem) orderItem;
                if (membershipItem.getPlanType() == PlanType.BASIC) {
                    // Use 0 as basic membership price when there's also a sticker item
                    // Do not add anything to the total
                } else {
                    total += orderItem.calculatePrice();
                }
            } else {
                total += orderItem.calculatePrice();
            }
        }
        return total;
    }

    private void saveOrderToRepository(String merchantOrderId, List<OrderItem> orderItems) {
        String userId = userService.getCurrentUser().getUserId();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        for (OrderItem orderItem : orderItems) {
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setMerchantOrderId(merchantOrderId);
            paymentOrder.setOrderItemId(UUID.randomUUID().toString());
            paymentOrder.setUserId(userId);
            paymentOrder.setCreatedAt(now);
            paymentOrder.setOrderItemType(orderItem.getOrderItemType());

            if (orderItem.getOrderItemType() == OrderItemType.MEMBERSHIP) {
                com.qrust.user.api.dto.order.MembershipOrderItem membershipItem = (com.qrust.user.api.dto.order.MembershipOrderItem) orderItem;
                MembershipOrderDetails membershipDetails = new MembershipOrderDetails(membershipItem.getPlanType());
                paymentOrder.setOrderDetails(membershipDetails);
            } else if (orderItem.getOrderItemType() == OrderItemType.QR_STICKER) {
                com.qrust.user.api.dto.order.QRStickerOrderItem stickerItem = (com.qrust.user.api.dto.order.QRStickerOrderItem) orderItem;
                QrStickerOrderDetails stickerDetails = new QrStickerOrderDetails(
                    stickerItem.getStickerType(),
                    stickerItem.getQuantity(),
                    stickerItem.getTemplateId(),
                    stickerItem.getAddressId()
                );
                paymentOrder.setOrderDetails(stickerDetails);
            }

            orderRepository.save(paymentOrder);
        }
    }
}
