package com.qrust.user.service;

import com.qrust.common.client.IpWhoIsClient;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.ScanHistory;
import com.qrust.common.domain.ScanLocation;
import com.qrust.common.repository.ScanRepository;
import com.qrust.user.api.dto.LocationRequest;
import com.qrust.user.exceptions.LimitReachedException;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    UserLimitService userLimitService;

    @Inject
    UserService userService;

    @Inject
    @RestClient
    IpWhoIsClient ipWhoIsClient;


    public ScanHistory save(ScanHistory history) {

        QRCode qrCode = qrCodeService.getQr(history.getQrId());
        if (qrCode == null) {
            throw new IllegalArgumentException("QR Code not found for ID: " + history.getQrId());
        }
        if (!qrCode.isPublic()) {
            throw new IllegalArgumentException("QR Code is made private by owner");
        }
        List<ScanHistory> scanHistoryForQr = getScanHistoryForQr(history.getQrId());
        if (scanHistoryForQr.size() >= userLimitService.getScanLimitForUser(userService.getUserById(qrCode.getOwner().getUserId()))) {
            throw new LimitReachedException("Scan limit reached, Owner need to upgrade plan to allow more scans.");
        }
        // if scan history already exists based on ip address and qrId within last 1 min then return existing history
        ScanHistory finalHistory = history;
        Optional<ScanHistory> existingHistory = scanHistoryForQr.stream().filter(h -> h.getScannerIp().equals(finalHistory.getScannerIp()) && h.getQrId().equals(finalHistory.getQrId()))
                .findFirst();

        if (existingHistory.isPresent() && existingHistory.get().getScanTimestamp().isAfter(history.getScanTimestamp().minusSeconds(60))) {
            log.info("Scan history already exists for IP: {} and QR ID: {} within the last minute. Returning existing history.",
                    history.getScannerIp(), history.getQrId());
            history = existingHistory.get();
            history.setScanTimestamp(Instant.now());
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

    public void updateScanLocation(ScanHistory scanHistory, LocationRequest locationRequest) {
        ScanLocation scanLocation = scanHistory.getLocation();
        if (scanLocation == null) {
            scanLocation = ScanLocation.builder()
                    .latitude(locationRequest.getLatitude())
                    .longitude(locationRequest.getLongitude())
                    .isGpsEnabled(true)
                    .build();
        } else {
            scanLocation.setLatitude(locationRequest.getLatitude());
            scanLocation.setLongitude(locationRequest.getLongitude());
            scanLocation.setGpsEnabled(true);
        }

        scanHistory.setLocation(scanLocation);
        scanRepository.save(scanHistory);
    }

    public List<ScanHistory> getScanHistoryForQr(UUID qrId) {
        return scanRepository.getScanHistoryByQrIds(List.of(qrId));
    }
}
