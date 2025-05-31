package com.qrust.service.impl;

import com.qrust.domain.QRCode;
import com.qrust.domain.PlanType;
import com.qrust.repository.QRCodeRepository;
import com.qrust.service.QRCodeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QRCodeServiceImpl implements QRCodeService {
    private final QRCodeRepository qrCodeRepository;

    @Inject
    public QRCodeServiceImpl(QRCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    @Override
    public List<QRCode> createQrCodes(int count, PlanType planType) {
        List<QRCode> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QRCode code = new QRCode();
            code.setId(UUID.randomUUID());
            code.setPublicToken(UUID.randomUUID().toString());
            code.setStatus(com.qrust.domain.QRStatus.UNASSIGNED);
            code.setPlanType(planType);
            code.setCreatedAt(LocalDateTime.now());
            qrCodeRepository.save(code);
            codes.add(code);
        }
        return codes;
    }

    @Override
    public QRCode getQrInfo(String publicToken) {
        return qrCodeRepository.findByPublicToken(publicToken).orElse(null);
    }

    @Override
    public void revokeQr(UUID qrId) {
        qrCodeRepository.findById(qrId).ifPresent(qr -> {
            qr.setStatus(com.qrust.domain.QRStatus.REVOKED);
            qrCodeRepository.save(qr);
        });
    }
}

