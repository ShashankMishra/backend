package com.qrust.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.UUID;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile{
    private UUID profileId;
    private String ownerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String bio; // Short biography or description
}
