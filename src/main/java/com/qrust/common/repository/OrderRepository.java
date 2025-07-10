package com.qrust.common.repository;

import com.qrust.common.domain.order.OrderStatus;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;

import java.util.List;

public interface OrderRepository {
    void save(PaymentOrder paymentOrder);
    List<PaymentOrder> getAllByUserId(String userId);
    List<PaymentOrder> getAllByMerchantOrderId(String merchantOrderId);
    List<PaymentOrder> getAllByPaymentStatus(PaymentStatus status);
    List<PaymentOrder> getAllQrStickerOrders(OrderStatus orderStatus);
    PaymentOrder getByOrderItemId(String orderItemId);
}
