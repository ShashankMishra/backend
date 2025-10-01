package com.qrust.user.service;

import com.qrust.common.domain.*;
import com.qrust.common.redis.RedisService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.qrust.Constants.EXOTEL_VIRTUAL_NUMBER;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CallService {

    private final RedisService redisService;

    public QRCode getMaskedNumberForQr(QRCode qrCode) {

        var detailsJson = qrCode.getDetails().toJson();
        var details = QRDetails.fromJson(detailsJson);
        var qrCodeCopy = new QRCode(qrCode.getId(), qrCode.getShortId(), qrCode.getStatus(), qrCode.getCreatedAt(), qrCode.getUpdatedAt(), qrCode.getType(), qrCode.getUserId(), qrCode.getUserEmail(), qrCode.getCreatedBy(), details, qrCode.isPublic(), qrCode.getAccessCode(), qrCode.isPremium());

        switch (qrCodeCopy.getType()) {
            case VEHICLE -> {
                var vehicleDetails = ((VehicleDetails) qrCodeCopy.getDetails());
                vehicleDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(vehicleDetails.getEmergencyContact().getPhoneNumber(), qrCodeCopy.getId()));
                vehicleDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(vehicleDetails.getOwnerContact().getPhoneNumber(), qrCodeCopy.getId()));
            }
            case CHILD -> {
                var childDetails = ((ChildDetails) qrCodeCopy.getDetails());
                childDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(childDetails.getEmergencyContact().getPhoneNumber(), qrCodeCopy.getId()));
            }
            case LUGGAGE -> {
                var luggageDetails = ((LuggageDetails) qrCodeCopy.getDetails());
                luggageDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(luggageDetails.getEmergencyContact().getPhoneNumber(), qrCodeCopy.getId()));
                luggageDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(luggageDetails.getOwnerContact().getPhoneNumber(), qrCodeCopy.getId()));

            }
            case LOCKSCREEN -> {
                var lockscreenDetails = ((LockscreenDetails) qrCodeCopy.getDetails());
                lockscreenDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(lockscreenDetails.getEmergencyContact().getPhoneNumber(), qrCodeCopy.getId()));
                lockscreenDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(lockscreenDetails.getOwnerContact().getPhoneNumber(), qrCodeCopy.getId()));
            }
            case PERSON -> {
                var personDetails = ((PersonDetails) qrCodeCopy.getDetails());
                personDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(personDetails.getEmergencyContact().getPhoneNumber(), qrCodeCopy.getId()));
            }
        }
        return qrCodeCopy;
    }

    private String getVirtualNumber(String contactNumber, UUID qrId) {
        return String.join(",", EXOTEL_VIRTUAL_NUMBER, redisService.getOrCreateExtension(contactNumber, qrId));
    }


}