package com.qrust.service.impl;

import com.qrust.api.dto.*;
import com.qrust.domain.QRCode;
import com.qrust.domain.QRStatus;
import com.qrust.domain.User;
import com.qrust.exceptions.LimitReached;
import com.qrust.mapper.*;
import com.qrust.repository.QRCodeRepository;
import com.qrust.service.QRCodeService;
import com.qrust.service.UserService;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QRCodeServiceImpl implements QRCodeService {
    @Inject
    QRCodeRepository qrCodeRepository;

    @Inject
    UserService userService;

    @Inject
    UserLimitService userLimitService;


    // Mappers
    private final PersonDetailsMapper personDetailsMapper = new PersonDetailsMapper();
    private final VehicleDetailsMapper vehicleDetailsMapper = new VehicleDetailsMapper();
    private final ChildDetailsMapper childDetailsMapper = new ChildDetailsMapper();
    private final LuggageDetailsMapper luggageDetailsMapper = new LuggageDetailsMapper();
    private final LockscreenDetailsMapper lockscreenDetailsMapper = new LockscreenDetailsMapper();

    @Override
    public QRCode createQr(QRCodeRequest req) throws LimitReached {
        if (getAllQrs().size() >= userLimitService.getQrLimitForUser(userService.getCurrentUser())) {
            throw new LimitReached("QR code limit reached, Please upgrade your plan to create more QR's.");
        }
        QRCode entity = toEntity(req);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(LocalDateTime.now());
        User currentUser = userService.getCurrentUser();
        entity.setOwner(currentUser);
        entity.setStatus(QRStatus.ASSIGNED);
        entity.setCreatedBy(currentUser);
        qrCodeRepository.save(entity);
        return entity;
    }

    @Override
    public QRCode updateQr(UUID qrId, QRCodeRequest req) {
        // get qrcode from repo and then anyone can update QR if it is unassigned status otherwise only owner can update
        User currentUser = userService.getCurrentUser();
        QRCode qrCode = getQr(qrId);
        if (qrCode == null) return null;
        if (qrCode.getOwner() == null || !qrCode.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You do not have permission to update this QR code.");
        }
        updateQrDetails(qrCode, req);
        qrCodeRepository.save(qrCode);
        return qrCode;
    }

    @Override
    public void deleteQr(UUID qrId) {
        // delete qr id only if it is owned by the current user
        QRCode existing = getQr(qrId);
        if (existing == null) return;
        User currentUser = userService.getCurrentUser();
        if (existing.getOwner() == null || !existing.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You do not have permission to delete this QR code.");
        }
        qrCodeRepository.delete(qrId);
    }

    @Override
    public List<QRCode> getAllQrs() {
        User currentUser = userService.getCurrentUser();
        return qrCodeRepository.findAll().stream()
                .filter(qr -> qr.getOwner() != null && qr.getOwner().getUserId().equals(currentUser.getUserId()))
                .toList();
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
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setDetails(entity.getDetails());
        return resp;
    }

    // Helper to map request DTO to entity
    private QRCode toEntity(QRCodeRequest req) {
        QRCode entity = new QRCode();
        entity.setType(req.getType());
        updateQrDetails(entity, req);
        return entity;
    }

    private void updateQrDetails(QRCode entity, QRCodeRequest req) {
        switch (req.getType()) {
            case PERSON -> entity.setDetails(personDetailsMapper.toEntity((PersonDetailsDto) req.getDetails()));
            case VEHICLE -> entity.setDetails(vehicleDetailsMapper.toEntity((VehicleDetailsDto) req.getDetails()));
            case CHILD -> entity.setDetails(childDetailsMapper.toEntity((ChildDetailsDto) req.getDetails()));
            case LUGGAGE -> entity.setDetails(luggageDetailsMapper.toEntity((LuggageDetailsDto) req.getDetails()));
            case LOCKSCREEN ->
                    entity.setDetails(lockscreenDetailsMapper.toEntity((LockscreenDetailsDto) req.getDetails()));
            default -> throw new IllegalArgumentException("Unsupported QR type: " + req.getType());
        }
    }

    @Override
    public QRCodePublicResponse toPublicResponse(QRCode entity) {
        if (entity == null) return null;
        QRCodePublicResponse resp = new QRCodePublicResponse();
        resp.setId(entity.getId());
        resp.setType(entity.getType());
        resp.setDetails(entity.getDetails());
        return resp;
    }
}
