package com.qrust.common.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    private String addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pincode;
    private String phoneNumber;
}
