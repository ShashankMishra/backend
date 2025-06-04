package com.qrust.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    private UUID id;
    private QRStatus status;
    private PlanType planType;
    private LocalDateTime createdAt;
    private QRType type;

    private QRDetails details;
    private User owner;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    @DynamoDbConvertedBy(QRDetailsConverter.class)
    public QRDetails getDetails() {
        return details;
    }
}
