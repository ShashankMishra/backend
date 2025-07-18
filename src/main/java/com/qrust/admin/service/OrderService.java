package com.qrust.admin.service;

import com.qrust.admin.api.dto.OrderShippingRequest;
import com.qrust.common.domain.order.*;
import com.qrust.common.domain.user.UserAddress;
import com.qrust.common.domain.user.UserInfo;
import com.qrust.common.repository.OrderRepository;
import com.qrust.common.repository.UserInfoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    UserInfoRepository userInfoRepository;

    public List<PaymentOrder> getRecentOrders() {
        return orderRepository.getAllByPaymentStatus(PaymentStatus.PENDING);
    }

    public List<PaymentOrder> getAllQrStickerOrders() {
        List<PaymentOrder> createdOrders = orderRepository.getAllQrStickerOrders(OrderStatus.CREATED);
        List<PaymentOrder> shippedOrders = orderRepository.getAllQrStickerOrders(OrderStatus.SHIPPED);

        List<PaymentOrder> allOrders = new ArrayList<>();
        allOrders.addAll(createdOrders);
        allOrders.addAll(shippedOrders);
        return allOrders;
    }

    public void updateOrderShippingDetails(OrderShippingRequest orderShippingRequest) {
        PaymentOrder order = orderRepository.getByOrderItemId(orderShippingRequest.getOrderItemId());
        order.setOrderStatus(orderShippingRequest.getOrderStatus());
        QrStickerOrderDetails orderDetails = (QrStickerOrderDetails) order.getOrderDetails();
        orderDetails.setShippingId(orderShippingRequest.getShippingId());
        orderDetails.setSerialNumber(orderShippingRequest.getSerialNumber());
        orderRepository.save(order);
    }

    public UserAddress getOrderAddressDetails(String userId, String addressId) {
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        List<UserAddress> addresses = userInfo.getAddresses();
        return addresses.stream()
                .filter(address -> address.getAddressId().equals(addressId))
                .findFirst().get();
    }
}
