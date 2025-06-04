package com.qrust.service;

import com.qrust.api.dto.*;
import com.qrust.domain.QRCode;
import com.qrust.mapper.*;
import com.qrust.repository.QRCodeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QRCodeServiceImpl implements QRCodeService {
    @Inject
    QRCodeRepository qrCodeRepository;

    // Mappers
    private final PersonDetailsMapper personDetailsMapper = new PersonDetailsMapper();
    private final VehicleDetailsMapper vehicleDetailsMapper = new VehicleDetailsMapper();
    private final ChildDetailsMapper childDetailsMapper = new ChildDetailsMapper();
    private final LuggageDetailsMapper luggageDetailsMapper = new LuggageDetailsMapper();
    private final LockscreenDetailsMapper lockscreenDetailsMapper = new LockscreenDetailsMapper();

    @Override
    public QRCode createQr(QRCodeRequest req) {
        QRCode entity = toEntity(req);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(LocalDateTime.now());
        qrCodeRepository.save(entity);
        return entity;
    }

    @Override
    public QRCode updateQr(UUID qrId, QRCodeRequest req) {
        QRCode existing = qrCodeRepository.findById(qrId).orElse(null);
        if (existing == null) return null;
        QRCode updated = toEntity(req);
        updated.setId(qrId);
        updated.setCreatedAt(existing.getCreatedAt());
        qrCodeRepository.save(updated);
        return updated;
    }

    @Override
    public void deleteQr(UUID qrId) {
        qrCodeRepository.delete(qrId);
    }

    @Override
    public List<QRCode> getAllQrs() {
        return qrCodeRepository.findAll();
    }

    @Override
    public QRCode getQr(UUID id) {
        QRCode entity = qrCodeRepository.findById(id).orElse(null);
        return entity;
    }

    @Override
    public QRCodeResponse toResponse(QRCode entity) {
        if (entity == null) return null;
        QRCodeResponse resp = new QRCodeResponse();
        resp.setId(entity.getId());
        resp.setType(entity.getType());
        resp.setStatus(entity.getStatus());
        resp.setPlanType(entity.getPlanType());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setDetails(entity.getDetails());
        return resp;
    }
    // Helper to map request DTO to entity
    private QRCode toEntity(QRCodeRequest req) {
        QRCode entity = new QRCode();
        entity.setType(req.getType());
        entity.setStatus(req.getStatus());
        entity.setPlanType(req.getPlanType());
        updateQrDetails(entity, req);
        return entity;
    }

    private void updateQrDetails(QRCode entity, QRCodeRequest req) {
        switch (req.getType()) {
            case PERSON -> entity.setDetails(personDetailsMapper.toEntity((PersonDetailsDto) req.getDetails()));
            case VEHICLE -> entity.setDetails(vehicleDetailsMapper.toEntity((VehicleDetailsDto) req.getDetails()));
            case CHILD -> entity.setDetails(childDetailsMapper.toEntity((ChildDetailsDto) req.getDetails()));
            case LUGGAGE -> entity.setDetails(luggageDetailsMapper.toEntity((LuggageDetailsDto) req.getDetails()));
            case LOCKSCREEN -> entity.setDetails(lockscreenDetailsMapper.toEntity((LockscreenDetailsDto) req.getDetails()));
            default -> throw new IllegalArgumentException("Unsupported QR type: " + req.getType());
        }
    }
}

