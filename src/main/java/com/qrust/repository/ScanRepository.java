package com.qrust.repository;

import com.qrust.domain.ScanHistory;

import java.util.List;
import java.util.UUID;

public interface ScanRepository {
    void save(ScanHistory history);

    ScanHistory findLatestByIpAndQrId(String scannerIp, UUID qrId);

    ScanHistory getScan(UUID scanId);
    // Fetch scan histories for a list of QR code IDs
    List<ScanHistory> getScanHistoryByQrIds(List<UUID> qrIds);
}
