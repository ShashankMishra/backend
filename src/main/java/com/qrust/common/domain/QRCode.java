package com.qrust.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    private UUID id;
    private String shortId;
    private QRStatus status = QRStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private QRType type;
    private String userId;
    private String userEmail;
    private User createdBy;
    private QRDetails details;
    private boolean isPublic = true;
    private String accessCode;
    private boolean isPremium = false;


    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"userId-index"})
    public String getUserId() {
        return userId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"emailId-index"})
    public String getEmailId() {
        return userEmail;
    }

    @DynamoDbConvertedBy(QRDetailsConverter.class)
    public QRDetails getDetails() {
        return details;
    }
}
