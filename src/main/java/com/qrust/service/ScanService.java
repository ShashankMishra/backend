package com.qrust.service;

import com.qrust.domain.ScanHistory;
import com.qrust.repository.ScanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ScanService {
    @Inject
    ScanRepository scanRepository;

    public ScanHistory save(ScanHistory history) {

        // if scan history already exists based on ip address and qrId within last 1 min then return existing history
        ScanHistory existingHistory = scanRepository.findLatestByIpAndQrId(history.getScannerIp(), history.getQrId());
        if (existingHistory != null && existingHistory.getScanTimestamp().isAfter(history.getScanTimestamp().minusSeconds(60))) {
            log.info("Scan history already exists for IP: {} and QR ID: {} within the last minute. Returning existing history.",
                    history.getScannerIp(), history.getQrId());
            return existingHistory;
        }
        scanRepository.save(history);
        return history;
    }

    public ScanHistory getScan(UUID scanId) {
        return scanRepository.getScan(scanId);
    }
}
