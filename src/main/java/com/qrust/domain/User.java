package com.qrust.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import static com.qrust.domain.UserRole.FREE;

@Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private UserRole role = FREE;
}
