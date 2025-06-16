package com.qrust.repository;

import com.qrust.domain.ScanHistory;

import java.util.UUID;

public interface ScanRepository {
    void save(ScanHistory history);

    ScanHistory findByIpAndQrId(String scannerIp, UUID qrId);
}

