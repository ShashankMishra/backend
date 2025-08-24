package com.qrust.user.service;

import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.qrust.common.domain.order.OrderStatus;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;
import com.qrust.common.domain.order.QRUpgradeOrderDetails;
import com.qrust.user.api.dto.order.OrderItemType;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class PaymentOrderStatusScheduler {

    @Inject
    OrderService orderService;

    @Inject
    PhonepePaymentService phonepePaymentService;

    @Inject
    QRCodeService qrCodeService;

    @Scheduled(every = "1m")
    void checkPendingOrders() {
        try {
            List<PaymentOrder> pendingOrders = orderService.getAllByPaymentStatus(PaymentStatus.PENDING);
            for (PaymentOrder order : pendingOrders) {
                OrderStatusResponse response = phonepePaymentService.fetchOrderStatus(order.getMerchantOrderId());
                String paymentStatus = response.getState();
                if (PaymentStatus.COMPLETED.name().equals(paymentStatus)) {
                    if (Objects.equals(order.getOrderItemType(), OrderItemType.QR_UPGRADE)) {
                        QRUpgradeOrderDetails upgradeOrderDetails = (QRUpgradeOrderDetails) order.getOrderDetails();
                        UUID qrId = upgradeOrderDetails.getQrCodeId();
                        qrCodeService.upgradeQrToPremium(qrId);
                    } else if (Objects.equals(order.getOrderItemType(), OrderItemType.QR_STICKER)) {
                        order.setOrderStatus(OrderStatus.CREATED);
                    }
                    order.setPaymentStatus(PaymentStatus.COMPLETED);
                    orderService.save(order);

                } else if (PaymentStatus.FAILED.name().equals(paymentStatus)) {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderService.save(order);
                }
            }
        } catch (Exception e) {
            log.error("Error checking pending orders: {}", e.getMessage());
        }
    }
}
