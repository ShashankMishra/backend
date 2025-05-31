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
public class UserQRCodeLink {
    private UUID linkId;
    private UUID qrId;
    private String ownerId;
    private UUID profileId;
    private String activationCode;
    private LocalDateTime linkedAt;

    @DynamoDbPartitionKey
    public UUID getLinkId() {
        return linkId;
    }
}

