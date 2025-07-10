package com.qrust.user.service;

import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.qrust.common.domain.order.MembershipOrderDetails;
import com.qrust.common.domain.order.OrderStatus;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;
import com.qrust.common.repository.OrderRepository;
import com.qrust.user.api.dto.order.OrderItemType;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

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

    @Scheduled(every = "5m")
    void checkPendingOrders() {
        try {
            List<PaymentOrder> pendingOrders = orderRepository.getAllByPaymentStatus(PaymentStatus.PENDING);
            for (PaymentOrder order : pendingOrders) {
                OrderStatusResponse response = phonepePaymentService.fetchOrderStatus(order.getMerchantOrderId());
                String paymentStatus = response.getState();
                if (PaymentStatus.COMPLETED.name().equals(paymentStatus)) {
                    if (Objects.equals(order.getOrderItemType(), OrderItemType.MEMBERSHIP)) {
                        String userId = order.getUserId();
                        MembershipOrderDetails membershipDetails = (MembershipOrderDetails) order.getOrderDetails();
                        String planType = membershipDetails.getPlanType().name();
                        if (!userService.isUserInGroup(userId, planType)) {
                            cognitoService.upgradeUserGroup(userId, planType);
                        }
                    } else if (Objects.equals(order.getOrderItemType(), OrderItemType.QR_STICKER)) {
                        order.setOrderStatus(OrderStatus.CREATED);
                    }
                    order.setPaymentStatus(PaymentStatus.COMPLETED);
                    orderRepository.save(order);
                } else if (PaymentStatus.FAILED.name().equals(paymentStatus)) {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderRepository.save(order);
                }
            }
        } catch (Exception e) {
            log.error("Error checking pending orders: {}", e.getMessage());
        }
    }
}
