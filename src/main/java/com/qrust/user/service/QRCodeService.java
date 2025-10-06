package com.qrust.user.service;

import com.qrust.common.ShortNumericIdGenerator;
import com.qrust.common.domain.Contact;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.QRStatus;
import com.qrust.common.domain.User;
import com.qrust.common.mapper.*;
import com.qrust.common.repository.QRCodeRepository;
import com.qrust.user.api.dto.*;
import com.qrust.user.exceptions.InvalidActionException;
import com.qrust.user.exceptions.LimitReachedException;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.qrust.common.domain.QRStatus.ACTIVE;
import static com.qrust.common.domain.QRStatus.UNASSIGNED;

@ApplicationScoped
public class QRCodeService {
    @Inject
    QRCodeRepository qrCodeRepository;

    @Inject
    UserService userService;

    @Inject
    LimitService limitService;


    // Mappers
    private final PersonDetailsMapper personDetailsMapper = new PersonDetailsMapper();
    private final VehicleDetailsMapper vehicleDetailsMapper = new VehicleDetailsMapper();
    private final ChildDetailsMapper childDetailsMapper = new ChildDetailsMapper();
    private final LuggageDetailsMapper luggageDetailsMapper = new LuggageDetailsMapper();
    private final LockscreenDetailsMapper lockscreenDetailsMapper = new LockscreenDetailsMapper();


    public QRCode createUserQr(QRCodeRequest req) throws LimitReachedException {
        if (getAllQrs().stream().filter(qr -> qr.getStatus() == ACTIVE).toList().size() >= limitService.getQrCreationLimitForUser()) {
            throw new LimitReachedException("QR code limit reached, More QR codes not allowed for this account.");
        }
        validateQrDetails(req);
        QRCode entity = toEntity(req);
        entity.setId(UUID.randomUUID());
        entity.setShortId(ShortNumericIdGenerator.generate());
        entity.setCreatedAt(LocalDateTime.now());
        User currentUser = userService.getCurrentUser();
        entity.setUserId(currentUser.getUserId());
        entity.setUserEmail(currentUser.getEmail());
        entity.setUserEmail(currentUser.getEmail());
        entity.setStatus(ACTIVE);
        entity.setCreatedBy(currentUser);

        qrCodeRepository.save(entity);
        return entity;
    }


    public QRCode updateQr(UUID qrId, QRCodeRequest req) {
        // get qrcode from repo and then anyone can update QR if it is unassigned status otherwise only owner can update
        QRCode qrCode = getQrAuthorisedCode(qrId);
        if (qrCode == null) return null;

        validateQrDetails(req);


        updateQrDetails(qrCode, req);
        qrCodeRepository.save(qrCode);
        return qrCode;
    }


    public QRCode claimQR(QRCode qrCode) {
        qrCode.setStatus(ACTIVE);
        User currentUser = userService.getCurrentUser();
        qrCode.setUserId(currentUser.getUserId());
        qrCode.setUserEmail(currentUser.getEmail());
        qrCode.setUserEmail(currentUser.getEmail());
        qrCodeRepository.save(qrCode);
        return qrCode;
    }


    public void deleteQr(UUID qrId) {
        // delete qr id only if it is owned by the current user
        QRCode existing = getQr(qrId);
        if (existing == null) return;
        qrCodeRepository.delete(qrId);
    }


    public List<QRCode> getAllQrs() {
        User currentUser = userService.getCurrentUser();
        List<QRCode> qrCodes = qrCodeRepository.findAllByUserId(currentUser.getUserId());
        return qrCodes;
    }


    public QRCode getQr(UUID id) {
        return qrCodeRepository.findById(id).orElse(null);
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
        resp.setShortId(entity.getShortId());
        resp.setPremium(entity.isPremium());
        resp.setSetupDone(entity.isSetupFinished());
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
        entity.setStatus(QRStatus.UNASSIGNED);
        entity.setCreatedBy(currentUser);
        entity.setPremium(true);

        qrCodeRepository.save(entity);
        return entity;
    }

    public QRCode getQrAuthorisedCode(UUID id) {
        User currentUser = userService.getCurrentUser();
        QRCode qrCode = getQr(id);
        if (qrCode == null) return null;

        if (qrCode.getUserId() == null || !qrCode.getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You do not have permission to update this QR code.");
        }
        return qrCode;
    }


    @RolesAllowed("admin")
    public void mapQrToBarcode(UUID qrCodeId, String barcode) {
        QRCode qrCode = getQr(qrCodeId);
        if (qrCode == null) {
            throw new InvalidActionException("QR Code cannot be null");
        }
        if (qrCode.getStatus() != QRStatus.UNASSIGNED) {
            throw new InvalidActionException("QR Code must be in UNASSIGNED status to map to a barcode.");
        }
        String sha256Hex = DigestUtils.sha256Hex(barcode);
        qrCode.setAccessCode(sha256Hex);
        qrCode.setStatus(QRStatus.ASSIGNED);
        qrCodeRepository.save(qrCode);
    }

    public void upgradeQrToPremium(UUID qrCodeId) {
        QRCode qrCode = getQr(qrCodeId);
        qrCode.setPremium(true);
        qrCodeRepository.save(qrCode);
    }

    public List<QRCode> getAllQrsForAdmins() {
        // TODO: get all unassigned and assigned QRs as well
        return qrCodeRepository.findAll().stream()
                .filter(qr -> qr.getStatus() == QRStatus.ASSIGNED || qr.getStatus() == UNASSIGNED)
                .toList();
    }

    private void validateQrDetails(QRCodeRequest req) {

        List<Contact> verifiedContacts = userService.getCurrentUserInfo().getContacts();
        Set<String> verifiedPhoneNumbers = verifiedContacts.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        req.getContactList().forEach(contact -> {
            if (!(contact.getPhoneNumber() == null || verifiedPhoneNumbers.contains(contact.getPhoneNumber()))) {
                throw new InvalidActionException("Contact with phone number " + contact.getPhoneNumber() + " is not verified.");
            }
        });
    }
}
