package com.qrust.user.service;

import com.qrust.common.domain.*;
import com.qrust.common.redis.RedisService;
import com.qrust.common.repository.QRCodeRepository;
import com.qrust.user.api.dto.CallRequest;
import com.qrust.user.api.dto.CallResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import static com.qrust.Constants.EXOTEL_VIRTUAL_NUMBER;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CallService {

    private final RedisService redisService;
    private final QRCodeRepository qrCodeRepository;

    public CallResponse getVirtualNumberWithExtension(CallRequest callRequest) {
        Optional<QRCode> qrCodeOptional = qrCodeRepository.findById(UUID.fromString(callRequest.getId()));
        if (qrCodeOptional.isPresent()) {
            QRCode qrCode = qrCodeOptional.get();
            String contactNumber = getContactNumber(qrCode, callRequest.getContactType());
            String extension = redisService.getOrCreateExtension(contactNumber);
            return new CallResponse(EXOTEL_VIRTUAL_NUMBER + "," + extension);
        } else {
            throw new RuntimeException("QR code not found");
        }
    }

    private String getContactNumber(QRCode qrCode, String contactType) {
        if ("owner".equalsIgnoreCase(contactType)) {
            return switch (qrCode.getType()) {
                case VEHICLE -> ((VehicleDetails) qrCode.getDetails()).getOwnerContact().getPhoneNumber();
                case LUGGAGE -> ((LuggageDetails) qrCode.getDetails()).getOwnerContact().getPhoneNumber();
                case LOCKSCREEN -> ((LockscreenDetails) qrCode.getDetails()).getOwnerContact().getPhoneNumber();
                default -> throw new RuntimeException("Invalid QR type for owner contact");
            };
        } else if ("emergency".equalsIgnoreCase(contactType)) {
            return switch (qrCode.getType()) {
                case VEHICLE -> ((VehicleDetails) qrCode.getDetails()).getEmergencyContact().getPhoneNumber();
                case CHILD -> ((ChildDetails) qrCode.getDetails()).getEmergencyContact().getPhoneNumber();
                case LUGGAGE -> ((LuggageDetails) qrCode.getDetails()).getEmergencyContact().getPhoneNumber();
                case LOCKSCREEN -> ((LockscreenDetails) qrCode.getDetails()).getEmergencyContact().getPhoneNumber();
                case PERSON -> ((PersonDetails) qrCode.getDetails()).getEmergencyContact().getPhoneNumber();
            };
        }
        throw new RuntimeException("Invalid contact type");
    }
}