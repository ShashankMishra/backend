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

        switch (qrCode.getType()) {
            case VEHICLE -> {
                var vehicleDetails = ((VehicleDetails) qrCode.getDetails());
                vehicleDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(vehicleDetails.getEmergencyContact().getPhoneNumber(), qrCode.getId()));
                vehicleDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(vehicleDetails.getOwnerContact().getPhoneNumber(), qrCode.getId()));
            }
            case CHILD -> {
                var childDetails = ((ChildDetails) qrCode.getDetails());
                childDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(childDetails.getEmergencyContact().getPhoneNumber(), qrCode.getId()));
            }
            case LUGGAGE -> {
                var luggageDetails = ((LuggageDetails) qrCode.getDetails());
                luggageDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(luggageDetails.getEmergencyContact().getPhoneNumber(), qrCode.getId()));
                luggageDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(luggageDetails.getOwnerContact().getPhoneNumber(), qrCode.getId()));

            }
            case LOCKSCREEN -> {
                var lockscreenDetails = ((LockscreenDetails) qrCode.getDetails());
                lockscreenDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(lockscreenDetails.getEmergencyContact().getPhoneNumber(), qrCode.getId()));
                lockscreenDetails.getOwnerContact().setPhoneNumber(getVirtualNumber(lockscreenDetails.getOwnerContact().getPhoneNumber(), qrCode.getId()));
            }
            case PERSON -> {
                var personDetails = ((PersonDetails) qrCode.getDetails());
                personDetails.getEmergencyContact().setPhoneNumber(getVirtualNumber(personDetails.getEmergencyContact().getPhoneNumber(), qrCode.getId()));
            }
        }
        return qrCode;

    }

    private String getVirtualNumber(String contactNumber, UUID qrId) {
        return String.join(",", EXOTEL_VIRTUAL_NUMBER, redisService.getOrCreateExtension(contactNumber, qrId));
    }


}