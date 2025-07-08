package com.qrust.common.repository;

import com.qrust.common.domain.QRCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QRCodeRepository {
    void save(QRCode qrCode);
    Optional<QRCode> findById(UUID id);
    List<QRCode> findAll();
    void delete(UUID id);
}

