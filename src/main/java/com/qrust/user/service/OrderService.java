package com.qrust.user.service;

import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;
import com.qrust.common.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;


    public List<PaymentOrder> getAllByUserId(String userId) {
        return orderRepository.getAllByUserId(userId);
    }

    public List<PaymentOrder> getAllByPaymentStatus(PaymentStatus paymentStatus) {
        return orderRepository.getAllByPaymentStatus(PaymentStatus.PENDING);
    }

    public void save(PaymentOrder order) {
        orderRepository.save(order);
    }

    public List<PaymentOrder> getAllByMerchantOrderId(String merchantOrderId) {
        return orderRepository.getAllByMerchantOrderId(merchantOrderId);
    }
}
