package com.qrust.common.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@DynamoDbBean
public class CallHistory {

    public static final String TABLE_NAME = "callHistory";
    public static final String CONTACT_NUMBER_INDEX = "contactNumber-timestamp-index";

    private String qrId;
    private Instant timestamp;
    private String contactNumber;
    private String callFrom;
    private String callSid;
    private long expiry;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("qrId")
    public String getQrId() {
        return qrId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("timestamp")
    public Instant getTimestamp() {
        return timestamp;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = CONTACT_NUMBER_INDEX)
    @DynamoDbAttribute("contactNumber")
    public String getContactNumber() {
        return contactNumber;
    }

    @DynamoDbAttribute("callFrom")
    public String getCallFrom() {
        return callFrom;
    }

    @DynamoDbAttribute("callSid")
    public String getCallSid() {
        return callSid;
    }

    public long getExpiry() {
        if (timestamp == null) return 0L;
        return timestamp.plus(90, ChronoUnit.DAYS).getEpochSecond();
    }
}
