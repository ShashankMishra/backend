package com.qrust.common.repository.impl;

import com.qrust.common.domain.CallHistory;
import com.qrust.common.repository.CallHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CallHistoryRepositoryImpl implements CallHistoryRepository {

    private final DynamoDbTable<CallHistory> callHistoryTable;
    private final DynamoDbIndex<CallHistory> contactNumberIndex;

    @Inject
    public CallHistoryRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.callHistoryTable = enhancedClient.table(CallHistory.TABLE_NAME, TableSchema.fromBean(CallHistory.class));
        this.contactNumberIndex = callHistoryTable.index(CallHistory.CONTACT_NUMBER_INDEX);
    }

    @Override
    public void save(CallHistory callHistory) {
        callHistoryTable.putItem(callHistory);
    }

    @Override
    public List<CallHistory> findByQrId(String qrId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(qrId).build());
        return callHistoryTable.query(queryConditional).items().stream().collect(Collectors.toList());
    }

    @Override
    public List<CallHistory> findByContactNumber(String contactNumber) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(contactNumber).build());
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder().queryConditional(queryConditional).scanIndexForward(false).build();
        return contactNumberIndex.query(queryRequest).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
}
