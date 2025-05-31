package com.qrust.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    private UUID id;
    private String publicToken;
    private QRStatus status;
    private PlanType planType;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }
}

