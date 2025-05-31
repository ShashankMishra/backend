package com.qrust.service.impl;

import com.qrust.domain.*;
import com.qrust.repository.QRCodeRepository;
import com.qrust.repository.UserQRCodeLinkRepository;
import com.qrust.service.QRCodeService;
import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
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
            code.setStatus(QRStatus.UNASSIGNED);
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
            qr.setStatus(QRStatus.REVOKED);
            qrCodeRepository.save(qr);
        });
    }

    // Map QRCodeRequest to QRCode entity
    private QRCode fromRequest(QRCodeRequest req) {
        QRCode entity = new QRCode();
        entity.setType(req.getType() != null ? QRType.valueOf(req.getType().name()) : null);
        //entity.setDetails(req.getDetails());
        entity.setPlanType(req.getPlanType() != null ? PlanType.valueOf(req.getPlanType()) : null);
        entity.setStatus(QRStatus.UNASSIGNED);
        return entity;
    }

    // Map QRCode entity to QRCodeResponse
    public QRCodeResponse toResponse(QRCode entity) {
        if (entity == null) return null;
        QRCodeResponse resp = new QRCodeResponse();
        resp.setId(entity.getId());
        resp.setType(entity.getType() != null ? entity.getType().name() : null);
        resp.setDetails(entity.getDetails());
        resp.setPlanType(entity.getPlanType() != null ? entity.getPlanType().name() : null);
        resp.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        resp.setCreatedAt(entity.getCreatedAt());
        return resp;
    }

    // --- Service methods using DTOs ---
    @Override
    public QRCodeResponse createQr(QRCodeRequest req) {
        QRCode entity = fromRequest(req);
        entity.setId(UUID.randomUUID());
        entity.setPublicToken(UUID.randomUUID().toString());
        entity.setCreatedAt(LocalDateTime.now());
        qrCodeRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    public QRCodeResponse updateQr(UUID qrId, QRCodeRequest req) {
        return qrCodeRepository.findById(qrId).map(existing -> {
            QRCode entity = fromRequest(req);
            entity.setId(qrId);
            entity.setCreatedAt(existing.getCreatedAt());
            entity.setPublicToken(existing.getPublicToken());
            entity.setStatus(existing.getStatus());
            qrCodeRepository.save(entity);
            return toResponse(entity);
        }).orElse(null);
    }

    @Override
    public List<QRCode> getAllQrs() {
        List<QRCode> all = qrCodeRepository.findAll();
        return all;
    }

    @Override
    public QRCodeResponse getQrWithDetailsResponse(UUID qrId) {
        return qrCodeRepository.findById(qrId).map(this::toResponse).orElse(null);
    }

    @Override
    public void deleteQr(UUID qrId) {
        qrCodeRepository.delete(qrId);
    }

    @Inject
    UserQRCodeLinkRepository userQRCodeLinkRepository;

    @Override
    public void linkQrToUser(UUID qrId, String userId) {
        UserQRCodeLink link = new UserQRCodeLink();
        link.setLinkId(UUID.randomUUID());
        link.setQrId(qrId);
        link.setOwnerId(userId);
        link.setLinkedAt(LocalDateTime.now());
        userQRCodeLinkRepository.save(link);
    }
}
