package com.qrust.user.service;

import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.qrust.common.domain.OrderStatus;
import com.qrust.common.domain.PaymentOrder;
import com.qrust.common.repository.OrderRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class PaymentOrderStatusScheduler {

    @Inject
    OrderRepository orderRepository;

    @Inject
    PhonepePaymentService phonepePaymentService;

    @Inject
    UserService userService;

    @Inject
    CognitoService cognitoService;

    @Scheduled(every = "2m")
    void checkPendingOrders() {
        try {
            List<PaymentOrder> pendingOrders = orderRepository.getAllByOrderStatus(OrderStatus.PENDING);
            for (PaymentOrder order : pendingOrders) {
                OrderStatusResponse response = phonepePaymentService.fetchOrderStatus(order.getMerchantOrderId());
                String orderStatus = response.getState();
                if (OrderStatus.COMPLETED.name().equals(orderStatus)) {
                    String userId = order.getUserId();
                    String planType = order.getPlanType().name();
                    if (!userService.isUserInGroup(userId, planType)) {
                        cognitoService.upgradeUserGroup(userId, planType);
                    }
                    order.setOrderStatus(OrderStatus.COMPLETED);
                    orderRepository.save(order);
                } else if (OrderStatus.FAILED.name().equals(orderStatus)) {
                    order.setOrderStatus(OrderStatus.FAILED);
                    orderRepository.save(order);
                }
            }
        } catch (Exception e) {
            log.error("Error checking pending orders: {}", e.getMessage());

        }
    }
}

