package com.qrust.common.repository;

import com.qrust.common.domain.OrderStatus;
import com.qrust.common.domain.PaymentOrder;

import java.util.List;

public interface OrderRepository {
    void save(PaymentOrder paymentOrder);
    List<PaymentOrder> getAllByUserId(String userId);
    PaymentOrder getByMerchantOrderId(String merchantOrderId);
    List<PaymentOrder> getAllByOrderStatus(OrderStatus status);
}
