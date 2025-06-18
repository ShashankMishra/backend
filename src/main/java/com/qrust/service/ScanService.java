package com.qrust.service;

import com.qrust.client.IpWhoIsClient;
import com.qrust.domain.QRCode;
import com.qrust.domain.ScanHistory;
import com.qrust.domain.ScanLocation;
import com.qrust.repository.ScanRepository;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@Slf4j
public class ScanService {
    @Inject
    ScanRepository scanRepository;

    @Inject
    QRCodeService qrCodeService;

    @Inject
    @RestClient
    IpWhoIsClient ipWhoIsClient;


    public ScanHistory save(ScanHistory history) {

        // if scan history already exists based on ip address and qrId within last 1 min then return existing history
        ScanHistory existingHistory = scanRepository.findLatestByIpAndQrId(history.getScannerIp(), history.getQrId());
        if (existingHistory != null && existingHistory.getScanTimestamp().isAfter(history.getScanTimestamp().minusSeconds(60))) {
            log.info("Scan history already exists for IP: {} and QR ID: {} within the last minute. Returning existing history.",
                    history.getScannerIp(), history.getQrId());
            return existingHistory;
        }
        saveWithLocation(history);
        return history;
    }

    private void saveWithLocation(ScanHistory history) {
        scanRepository.save(history);

        CompletableFuture.runAsync(() -> {
            String scanIp = getIpAddressOrElse(history.getScannerIp());
            try {
                ScanLocation location = ipWhoIsClient.getLocation(scanIp);
                history.setLocation(location);
                scanRepository.save(history);
                log.info("Updated scan history with location: {}", location);
            } catch (Exception e) {
                log.error("Failed to update scan history with location for scanIp: {}", scanIp, e);
            }
        });
    }

    private String getIpAddressOrElse(String scannerIp) {
        // return hardcoded IP if scannerIp is 127.0.0.1
        String randomIp = "152.59.190.149";
        return LaunchMode.current() == LaunchMode.DEVELOPMENT ? randomIp : scannerIp;
    }

    public ScanHistory getScan(UUID scanId) {
        return scanRepository.getScan(scanId);
    }

    public List<ScanHistory> getAllScanHistory() {
        log.info("Fetching all scan history");
        List<QRCode> allQr = qrCodeService.getAllQrs();
        List<UUID> qrIds = allQr.stream().map(QRCode::getId).toList();
        return scanRepository.getScanHistoryByQrIds(qrIds);
    }
}
