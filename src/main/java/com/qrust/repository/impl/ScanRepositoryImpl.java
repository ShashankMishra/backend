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

import java.util.*;

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

    @Override
    public ScanHistory getScan(UUID scanId) {
        log.info("Fetching scan by ID: {}", scanId);
        return table.getItem(r -> r.key(k -> k.partitionValue(scanId.toString())));
    }

    @Override
    public List<ScanHistory> getScanHistoryByQrIds(List<UUID> qrIds) {
        log.info("Fetching scan history for QR IDs: {}", qrIds);
        if (qrIds == null || qrIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScanHistory> results = new ArrayList<>();
        // Use the qrId-index GSI for efficient querying by qrId
        for (UUID qrId : qrIds) {
            try {
                var queryResults = table.index("qrId-index")
                        .query(QueryConditional.keyEqualTo(k -> k.partitionValue(qrId.toString())));
                queryResults.stream()
                        .flatMap(page -> page.items().stream())
                        .forEach(results::add);
            } catch (Exception e) {
                log.error("Failed to fetch scan history for QR ID: {}", qrId, e);
            }
        }
        return results;
    }
}


