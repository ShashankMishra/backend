package com.qrust.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.qrust.common.domain.order.*;
import com.qrust.user.api.dto.order.OrderItem;
import com.qrust.user.api.dto.order.OrderItemType;
import com.qrust.user.api.dto.order.QRUpgrade;
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
    QRCodeService qrCodeService;

    @Inject
    UserService userService;

    @Inject
    OrderService orderService;

    @PostConstruct
    void init() {
        client = StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
    }

    public StandardCheckoutPayResponse createOrder(List<OrderItem> orderItems) {
        String merchantOrderId = "qrust-" + UUID.randomUUID();
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

            List<PaymentOrder> paymentOrders = orderService.getAllByMerchantOrderId(merchantOrderId);

            if (paymentOrders.isEmpty()) {
                Log.warnf("No orders found for merchantOrderId: %s", merchantOrderId);
                return;
            }

            for (PaymentOrder paymentOrder : paymentOrders) {
                if (Objects.equals(orderStatus, PaymentStatus.COMPLETED.name())) {
                    if (Objects.equals(paymentOrder.getOrderItemType(), OrderItemType.QR_UPGRADE)) {
                        paymentOrder.setPaymentStatus(PaymentStatus.COMPLETED);
                        QRUpgradeOrderDetails qrUpgradeOrderDetails = (QRUpgradeOrderDetails) paymentOrder.getOrderDetails();
                        qrCodeService.upgradeQrToPremium(qrUpgradeOrderDetails.getQrCodeId());
                        paymentOrder.setOrderStatus(OrderStatus.COMPLETED);
                    } else if (Objects.equals(paymentOrder.getOrderItemType(), OrderItemType.QR_STICKER)) {
                        paymentOrder.setOrderStatus(OrderStatus.CREATED);
                        paymentOrder.setPaymentStatus(PaymentStatus.COMPLETED);
                    }
                } else if (Objects.equals(orderStatus, PaymentStatus.FAILED.name())) {
                    paymentOrder.setPaymentStatus(PaymentStatus.FAILED);
                } else {
                    Log.warnf("Unhandled state: %s for order: %s", orderStatus, merchantOrderId);
                }

                orderService.save(paymentOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing webhook: " + e.getMessage(), e);
        }
    }

    private int computeOrderTotal(List<OrderItem> orderItems) {
        int total = 0;
        // Second pass: calculate total with special pricing if needed
        for (OrderItem orderItem : orderItems) {
            total += orderItem.calculatePrice();
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

            if (orderItem.getOrderItemType() == OrderItemType.QR_UPGRADE) {
                QRUpgrade qrUpgrade = (QRUpgrade) orderItem;
                QRUpgradeOrderDetails qrUpgradeOrderDetails = new QRUpgradeOrderDetails(qrUpgrade.getQrCodeId());
                paymentOrder.setOrderDetails(qrUpgradeOrderDetails);
            } else if (orderItem.getOrderItemType() == OrderItemType.QR_STICKER) {
                com.qrust.user.api.dto.order.QRStickerOrderItem stickerItem = (com.qrust.user.api.dto.order.QRStickerOrderItem) orderItem;
                QrStickerOrderDetails stickerDetails = new QrStickerOrderDetails(
                        stickerItem.getStickerType(),
                        stickerItem.getQuantity(),
                        stickerItem.getTemplateId(),
                        stickerItem.getUserAddress()
                );
                paymentOrder.setOrderDetails(stickerDetails);
            }

            orderService.save(paymentOrder);
        }
    }
}
