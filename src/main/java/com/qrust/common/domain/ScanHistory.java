package com.qrust.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanHistory {

    private UUID scanId;
    private UUID qrId;
    private Instant scanTimestamp;
    private String scannerIp;
    private String deviceInfo;
    private ScanLocation location;
    private long expiry;

    @DynamoDbPartitionKey
    public UUID getScanId() {
        return scanId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"scannerIp-qrId-index"})
    public String getScannerIp() {
        return scannerIp;
    }

    @DynamoDbSecondarySortKey(indexNames = {"scannerIp-qrId-index"})
    @DynamoDbSecondaryPartitionKey(indexNames = {"qrId-index"})
    public UUID getQrId() {
        return qrId;
    }

    @DynamoDbSecondarySortKey(indexNames = {"qrId-index"})
    public Instant getScanTimestamp() {
        return scanTimestamp;
    }

    public long getExpiry() {
        if (scanTimestamp == null) return 0L;
        return scanTimestamp.plus(30, ChronoUnit.DAYS).getEpochSecond();
    }
}
