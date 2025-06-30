package com.qrust.domain;

import com.qrust.api.dto.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDateTime;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {
    private String merchantOrderId;
    private String userId;
    private PlanType planType;
    private OrderStatus orderStatus = OrderStatus.PENDING;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "userId-index")
    public String getUserId() {
        return userId;
    }
}
