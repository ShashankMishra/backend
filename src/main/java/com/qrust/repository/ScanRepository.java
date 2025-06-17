package com.qrust.repository;

import com.qrust.domain.ScanHistory;

import java.util.UUID;

public interface ScanRepository {
    void save(ScanHistory history);

    ScanHistory findLatestByIpAndQrId(String scannerIp, UUID qrId);
}

