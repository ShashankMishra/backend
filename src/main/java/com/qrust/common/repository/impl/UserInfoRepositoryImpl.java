package com.qrust.common.repository.impl;

import com.qrust.common.domain.user.UserInfo;
import com.qrust.common.repository.UserInfoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;

@ApplicationScoped
@Slf4j
public class UserInfoRepositoryImpl implements UserInfoRepository {

    private final DynamoDbTable<UserInfo> table;

    public UserInfoRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("UserInfo", TableSchema.fromBean(UserInfo.class));
    }

    @Override
    public void save(UserInfo userInfo) {
        table.putItem(userInfo);
    }

    @Override
    public UserInfo getByUserId(String userId) {

        Key key = Key.builder()
                .partitionValue(userId)
                .build();

        UserInfo userInfo = table.getItem(r -> r.key(key));

        if(userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setAddresses(new ArrayList<>());
        }

        return userInfo;
    }
}
