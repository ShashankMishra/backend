package com.qrust.repository.impl;

import com.qrust.domain.UserProfile;
import com.qrust.repository.UserProfileRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@ApplicationScoped
public class UserProfileRepositoryImpl implements UserProfileRepository {
    private final DynamoDbTable<UserProfile> table;

    public UserProfileRepositoryImpl(
            DynamoDbEnhancedClient enhancedClient,
            @ConfigProperty(name = "dynamodb.table.user") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(UserProfile.class));
    }

    @Override
    public void save(UserProfile profile) {
        log.info("Saving UserProfile: {}", profile);
        table.putItem(profile);
    }

    @Override
    public Optional<UserProfile> findByProfileId(UUID profileId) {
        log.debug("Finding UserProfile by id: {}", profileId);
        return Optional.ofNullable(table.getItem(r -> r.key(k -> k.partitionValue(profileId.toString()))));
    }

    @Override
    public List<UserProfile> findByOwnerId(String ownerId) {
        log.debug("Finding UserProfiles by ownerId: {}", ownerId);
        return table.scan().items().stream()
                .filter(p -> ownerId.equals(p.getOwnerId()))
                .collect(toList());
    }

    @Override
    public void delete(UUID profileId) {
        log.info("Deleting UserProfile by id: {}", profileId);
        table.deleteItem(r -> r.key(k -> k.partitionValue(profileId.toString())));
    }
}
