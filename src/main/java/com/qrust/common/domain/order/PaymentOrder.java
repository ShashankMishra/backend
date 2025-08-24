package com.qrust.common.domain.order;

import com.qrust.user.api.dto.order.OrderItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {
    private String merchantOrderId;
    private String orderItemId;
    private String userId;
    private OrderItemType orderItemType;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private OrderStatus orderStatus = OrderStatus.INVALID;
    private OrderDetails orderDetails;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    @DynamoDbSortKey
    public String getOrderItemId() {
        return orderItemId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"GSI_UserOrders"})
    public String getUserId() {
        return userId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"GSI_PaymentStatus"})
    public PaymentStatus getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : null;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"GSI_OrderStatus"})
    public OrderStatus getOrderStatus() {
        return orderStatus != null ? orderStatus : null;
    }

    @DynamoDbSecondarySortKey(indexNames = {"GSI_UserOrders", "GSI_PaymentStatus", "GSI_OrderStatus"})
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @DynamoDbConvertedBy(OrderDetailsConverter.class)
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
}
