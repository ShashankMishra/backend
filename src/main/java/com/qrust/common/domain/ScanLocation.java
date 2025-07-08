package com.qrust.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@Builder
public class ScanLocation {
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private boolean isGpsEnabled = false;
}
