package com.qrust.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.qrust.api.dto.PlanType;
import com.qrust.domain.OrderStatus;
import com.qrust.domain.PaymentOrder;
import com.qrust.repository.PaymentOrderRepository;
import com.qrust.service.UserService;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.EnumMap;
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
    Env env = Env.PRODUCTION;

    @ConfigProperty(name = "quarkus.frontend.uri")
    String frontendUri;

    StandardCheckoutClient client;

    @Inject
    CognitoService cognitoService;

    @Inject
    UserService userService;

    @Inject
    PaymentOrderRepository paymentOrderRepository;

    private static final EnumMap<PlanType, Long> PLAN_PRICES = new EnumMap<>(PlanType.class);

    static {
        PLAN_PRICES.put(PlanType.BASIC, 1L);
        PLAN_PRICES.put(PlanType.PREMIUM, 1L);
        PLAN_PRICES.put(PlanType.BUSINESS, 1L);
        PLAN_PRICES.put(PlanType.FREE, 1L);
    }

    @PostConstruct
    void init() {
        client = StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);
    }

    public StandardCheckoutPayResponse createOrder(PlanType planType) {
        String merchantOrderId = "qrust-"+ UUID.randomUUID();
        Log.infof("Creating order with ID: %s for plan type: %s", merchantOrderId, planType);

        saveOrderToRepository(merchantOrderId, planType);

        // Set order amount based on plan type using PLAN_PRICES map
        Long amount = PLAN_PRICES.get(planType);
        if (amount == null) {
            throw new IllegalArgumentException("Unsupported plan type: " + planType);
        }
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

            PaymentOrder paymentOrder = paymentOrderRepository.getByMerchantOrderId(merchantOrderId);

            if(Objects.equals(orderStatus, OrderStatus.COMPLETED.name())) {
                cognitoService.upgradeUserGroup(paymentOrder.getUserId(), paymentOrder.getPlanType().name());
                paymentOrder.setOrderStatus(OrderStatus.COMPLETED);
            }else if(Objects.equals(orderStatus, OrderStatus.FAILED.name())) {
                paymentOrder.setOrderStatus(OrderStatus.FAILED);
            } else {
                Log.warnf("Unhandled state: %s for order: %s", orderStatus, merchantOrderId);
            }

            paymentOrderRepository.save(paymentOrder);
        } catch (Exception e) {
            throw new RuntimeException("Error processing webhook: " + e.getMessage(), e);
        }
    }

    private void saveOrderToRepository(String merchantOrderId, PlanType planType) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setMerchantOrderId(merchantOrderId);
        paymentOrder.setPlanType(planType);
        paymentOrder.setUserId(userService.getCurrentUser().getUserId());
        paymentOrder.setCreatedAt(java.time.LocalDateTime.now());
        paymentOrderRepository.save(paymentOrder);
    }
}
