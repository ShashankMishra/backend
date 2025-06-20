package com.qrust.domain;

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
    private String country;
    private String region;
    private String city;
    private String postal;
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private boolean isGpsEnabled = false;
}
