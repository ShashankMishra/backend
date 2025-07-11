package com.qrust.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

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
    private QRType type;
    private User owner;
    private User createdBy;
    private QRDetails details;
    private boolean isPublic = true;
    private Contact assignedOwnerContact;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    @DynamoDbConvertedBy(QRDetailsConverter.class)
    public QRDetails getDetails() {
        return details;
    }
}
