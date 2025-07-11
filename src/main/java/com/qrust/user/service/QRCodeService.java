package com.qrust.user.service;

import com.qrust.common.ShortNumericIdGenerator;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.QRStatus;
import com.qrust.common.domain.User;
import com.qrust.common.mapper.*;
import com.qrust.common.repository.QRCodeRepository;
import com.qrust.user.api.dto.*;
import com.qrust.user.exceptions.LimitReachedException;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QRCodeService {
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


    public QRCode createUserQr(QRCodeRequest req) throws LimitReachedException {
        if (getAllQrs().size() >= userLimitService.getQrLimitForUser(userService.getCurrentUser())) {
            throw new LimitReachedException("QR code limit reached, Please upgrade your plan to create more QR's.");
        }
        QRCode entity = toEntity(req);
        entity.setId(UUID.randomUUID());
        entity.setShortId(ShortNumericIdGenerator.generate());
        entity.setCreatedAt(LocalDateTime.now());
        User currentUser = userService.getCurrentUser();
        entity.setOwner(currentUser);
        entity.setStatus(QRStatus.ACTIVE);
        entity.setCreatedBy(currentUser);

        qrCodeRepository.save(entity);
        return entity;
    }


    public QRCode updateQr(UUID qrId, QRCodeRequest req) {
        // get qrcode from repo and then anyone can update QR if it is unassigned status otherwise only owner can update
        QRCode qrCode = getQrAuthorisedCode(qrId);
        if (qrCode == null) return null;
        updateQrDetails(qrCode, req);
        qrCodeRepository.save(qrCode);
        return qrCode;
    }




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


    public List<QRCode> getAllQrs() {
        User currentUser = userService.getCurrentUser();
        return qrCodeRepository.findAll().stream()
                .filter(qr -> qr.getOwner() != null && qr.getOwner().getUserId().equals(currentUser.getUserId()))
                .toList();
    }


    public QRCode getQr(UUID id) {
        QRCode entity = qrCodeRepository.findById(id).orElse(null);
        return entity;
    }


    public QRCodeResponse toResponse(QRCode entity) {
        if (entity == null) return null;
        QRCodeResponse resp = new QRCodeResponse();
        resp.setId(entity.getId());
        resp.setType(entity.getType());
        resp.setStatus(entity.getStatus());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setDetails(entity.getDetails());
        resp.setPublic(entity.isPublic());
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


    public QRCodePublicResponse toPublicResponse(QRCode entity) {
        if (entity == null) return null;
        QRCodePublicResponse resp = new QRCodePublicResponse();
        resp.setId(entity.getId());
        resp.setType(entity.getType());
        resp.setDetails(entity.getDetails());
        return resp;
    }


    public QRCode updateIsPublic(UUID id, boolean isPublic) {
        QRCode qrCode = getQrAuthorisedCode(id);
        if (qrCode == null) return null;
        qrCode.setPublic(isPublic);
        qrCodeRepository.save(qrCode);
        return qrCode;
    }

    @RolesAllowed("admin")
    public QRCode createQrForAdmin(QRCodeRequest req) throws LimitReachedException {
        QRCode entity = toEntity(req);
        entity.setId(UUID.randomUUID());
        entity.setShortId(ShortNumericIdGenerator.generate());
        entity.setCreatedAt(LocalDateTime.now());
        User currentUser = userService.getCurrentUser();
        entity.setOwner(currentUser);
        entity.setStatus(QRStatus.UNASSIGNED);
        entity.setCreatedBy(currentUser);

        qrCodeRepository.save(entity);
        return entity;
    }

    private @Nullable QRCode getQrAuthorisedCode(UUID id) {
        User currentUser = userService.getCurrentUser();
        QRCode qrCode = getQr(id);
        if (qrCode == null) return null;
        if (qrCode.getOwner() == null || !qrCode.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You do not have permission to update this QR code.");
        }
        return qrCode;
    }


}
