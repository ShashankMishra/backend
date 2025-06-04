package com.qrust.repository.impl;

import com.qrust.domain.QRCode;
import com.qrust.repository.QRCodeRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class QRCodeRepositoryImpl implements QRCodeRepository {
    private final DynamoDbTable<QRCode> table;

    public QRCodeRepositoryImpl(
            DynamoDbEnhancedClient enhancedClient,
            @ConfigProperty(name = "dynamodb.table.qrcode") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(QRCode.class));
    }

    @Override
    @WithSpan
    public void save(QRCode qrCode) {
        log.info("Saving QRCode: {}", qrCode);
        table.putItem(qrCode);
    }

    @Override
    @WithSpan
    public Optional<QRCode> findById(UUID id) {
        log.debug("Finding QRCode by id: {}", id);
        return Optional.ofNullable(table.getItem(r -> r.key(k -> k.partitionValue(id.toString()))));
    }

    @Override
    public List<QRCode> findAll() {
        log.info("Retrieving all QRCodes");
        return table.scan().items().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting QRCode with id: {}", id);
        table.deleteItem(r -> r.key(k -> k.partitionValue(id.toString())));
    }
}
