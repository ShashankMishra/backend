package com.qrust.common.domain.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.ArrayList;
import java.util.List;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String userId;
    private List<UserAddress> addresses = new ArrayList<>();

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }
}
