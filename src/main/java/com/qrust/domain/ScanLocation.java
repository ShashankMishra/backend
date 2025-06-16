package com.qrust.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ScanLocation {
    private String country;
    private String region;
    private String city;
    private String postalCode;
    private Double latitude;
    private Double longitude;
}
