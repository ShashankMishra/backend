package com.qrust.user.service;

import com.cashfree.pg.ApiException;
import com.cashfree.pg.ApiResponse;
import com.cashfree.pg.Cashfree;
import com.cashfree.pg.model.CreateOrderRequest;
import com.cashfree.pg.model.CustomerDetails;
import com.cashfree.pg.model.OrderEntity;
import com.cashfree.pg.model.OrderMeta;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrust.common.domain.WebhookEventType;
import com.qrust.user.api.dto.PlanType;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Objects;

@ApplicationScoped
public class CashfreePaymentService {

    @ConfigProperty(name = "cashfree.client-id")
    String clientId;

    @ConfigProperty(name = "cashfree.client-secret")
    String clientSecret;

    @ConfigProperty(name = "app.frontend.uri")
    String frontendUri;

    public static final String ORDER_CURRENCY = "INR";

    private Cashfree cashfree;

    @Inject
    CognitoService cognitoService;

    @Inject
    UserService userService;

    private static final EnumMap<PlanType, Long> PLAN_PRICES = new EnumMap<>(PlanType.class);
    static {
        PLAN_PRICES.put(PlanType.BASIC, 1L);
        PLAN_PRICES.put(PlanType.PREMIUM, 1L);
        PLAN_PRICES.put(PlanType.BUSINESS, 1L);
        PLAN_PRICES.put(PlanType.FREE, 1L);
    }

    @PostConstruct
    void init() {
        cashfree = new Cashfree(Cashfree.SANDBOX, clientId, clientSecret, null, null, null);
    }

    public OrderEntity createOrder(PlanType planType) {
        OrderMeta orderMeta = new OrderMeta();
        orderMeta.setReturnUrl(String.format("%s/pricing?paymentStatus=success", frontendUri));
        orderMeta.setNotifyUrl("https://webhook.site/1ac06e2c-65c3-4457-98c8-b4774129b67e");

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerId(userService.getCurrentUser().getUserId());
        customerDetails.setCustomerPhone("1234567890");

        CreateOrderRequest request = new CreateOrderRequest();
        // Set order amount based on plan type using PLAN_PRICES map
        Long amount = PLAN_PRICES.get(planType);
        if (amount == null) {
            throw new IllegalArgumentException("Unsupported plan type: " + planType);
        }
        request.setOrderAmount(BigDecimal.valueOf(amount));
        request.setOrderCurrency(ORDER_CURRENCY);
        request.setCustomerDetails(customerDetails);
        request.setOrderMeta(orderMeta);
        try {
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(request, null, null, null);
            return response.getData();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderEntity fetchOrder(String orderId) {
        try {
            ApiResponse<OrderEntity> responseFetchOrder = cashfree.PGFetchOrder(orderId, null, null, null);
            return responseFetchOrder.getData();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleWebhook(String body, String userId) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            String eventType = root.has("type") ? root.get("type").asText() : null;
            if (Objects.equals(eventType, WebhookEventType.PAYMENT_SUCCESS_WEBHOOK.name())) {
                cognitoService.upgradeUserGroup(userId, "basic");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
