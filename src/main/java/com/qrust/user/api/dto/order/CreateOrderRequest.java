package com.qrust.user.api.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItem> orderItems;
}
