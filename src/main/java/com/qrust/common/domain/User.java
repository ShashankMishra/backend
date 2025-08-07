package com.qrust.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.Set;

@Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private Set<UserRole> role;

    public UserRole getHighestRole() {
        if (role.contains(UserRole.PREMIUM)) {
            return UserRole.PREMIUM;
        } else {
            return UserRole.FREE;
        }

    }
}
