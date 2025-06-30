package com.qrust.service.impl;

import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.qrust.domain.OrderStatus;
import com.qrust.domain.PaymentOrder;
import com.qrust.repository.PaymentOrderRepository;
import com.qrust.service.UserService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PaymentOrderStatusScheduler {

    @Inject
    PaymentOrderRepository paymentOrderRepository;

    @Inject
    PhonepePaymentService phonepePaymentService;

    @Inject
    UserService userService;

    @Inject
    CognitoService cognitoService;

    @Scheduled(every = "2m")
    void checkPendingOrders() {
        List<PaymentOrder> pendingOrders = paymentOrderRepository.getAllByOrderStatus(OrderStatus.PENDING);
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
                paymentOrderRepository.save(order);
            } else if (OrderStatus.FAILED.name().equals(orderStatus)) {
                order.setOrderStatus(OrderStatus.FAILED);
                paymentOrderRepository.save(order);
            }
        }
    }
}

