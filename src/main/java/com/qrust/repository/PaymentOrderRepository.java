package com.qrust.repository;

import com.qrust.domain.PaymentOrder;
import com.qrust.domain.OrderStatus;
import java.util.List;

public interface PaymentOrderRepository {
    void save(PaymentOrder paymentOrder);
    List<PaymentOrder> getAllByUserId(String userId);
    PaymentOrder getByMerchantOrderId(String merchantOrderId);
    List<PaymentOrder> getAllByOrderStatus(OrderStatus status);
}
