package com.qrust.repository.impl;

import com.qrust.domain.ScanHistory;
import com.qrust.repository.ScanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ScanRepositoryImpl implements ScanRepository {

    private final DynamoDbTable<ScanHistory> table;

    public ScanRepositoryImpl(DynamoDbEnhancedClient enhancedClient, @ConfigProperty(name = "dynamodb.table.scanHistory") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ScanHistory.class));
    }

    @Override
    public void save(ScanHistory history) {
        log.info("Saving scan: {}", history);
        table.putItem(history);
    }

    @Override
    public ScanHistory findLatestByIpAndQrId(String scannerIp, UUID qrId) {
        log.info("Finding latest scan for IP: {} and QR ID: {}", scannerIp, qrId);

        try {
            var results = table.index("scannerIp-qrId-index")
                    .query(QueryConditional.keyEqualTo(k ->
                            k.partitionValue(scannerIp)
                                    .sortValue(qrId.toString()))
                    );

            // Use a single-pass reduction to find the latest by timestamp
            return results.stream()
                    .flatMap(page -> page.items().stream())
                    .max(Comparator.comparing(ScanHistory::getScanTimestamp))
                    .orElse(null);

        } catch (Exception e) {
            log.error("Failed to fetch latest scan history for IP: {} and QR ID: {}", scannerIp, qrId, e);
            return null;
        }
    }


}
