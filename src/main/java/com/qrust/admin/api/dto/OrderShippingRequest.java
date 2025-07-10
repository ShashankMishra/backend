package com.qrust.admin.api.dto;

import com.qrust.common.domain.order.OrderStatus;
import lombok.Data;

@Data
public class OrderShippingRequest {
    private String orderItemId;
    private String shippingId;
    private String serialNumber;
    private OrderStatus orderStatus;
}
