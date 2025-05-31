package com.qrust.repository.impl;

import com.qrust.repository.UserQRCodeLinkRepository;
import com.qrust.domain.UserQRCodeLink;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class UserQRCodeLinkRepositoryImpl implements UserQRCodeLinkRepository {
    private final DynamoDbTable<UserQRCodeLink> table;

    public UserQRCodeLinkRepositoryImpl(
            DynamoDbEnhancedClient enhancedClient,
            @ConfigProperty(name = "dynamodb.table.userqrcodelink") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(UserQRCodeLink.class));
    }

    @Override
    public void save(UserQRCodeLink link) {
        table.putItem(link);
    }

    @Override
    public Optional<UserQRCodeLink> findByLinkId(UUID linkId) {
        return Optional.ofNullable(table.getItem(r -> r.key(k -> k.partitionValue(linkId.toString()))));
    }

    @Override
    public List<UserQRCodeLink> findByOwnerId(String ownerId) {
        return table.scan().items().stream()
                .filter(link -> ownerId.equals(link.getOwnerId()))
                .collect(toList());
    }

    @Override
    public List<UserQRCodeLink> findByQrId(UUID qrId) {
        return table.scan().items().stream()
                .filter(link -> qrId.equals(link.getQrId()))
                .collect(toList());
    }

    @Override
    public void delete(UUID linkId) {
        table.deleteItem(r -> r.key(k -> k.partitionValue(linkId.toString())));
    }
}

