package com.qrust.admin.service;

import com.qrust.common.domain.OrderStatus;
import com.qrust.common.domain.PaymentOrder;
import com.qrust.common.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    // get all recent orders
    public List<PaymentOrder> getRecentOrders() {
        return orderRepository.getAllByOrderStatus(OrderStatus.PENDING);

    }
}
