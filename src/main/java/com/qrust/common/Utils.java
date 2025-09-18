package com.qrust.common;

import com.qrust.common.domain.*;

public class Utils {

    public static Contact getOwnerContact(QRCode qrCode) {
        QRDetails details = qrCode.getDetails();
        return switch (qrCode.getType()) {
            case VEHICLE -> ((VehicleDetails) details).getOwnerContact();
            case PERSON -> ((PersonDetails) details).getEmergencyContact();
            case CHILD -> ((ChildDetails) details).getEmergencyContact();
            case LUGGAGE -> ((LuggageDetails) details).getOwnerContact();
            case LOCKSCREEN -> ((LockscreenDetails) details).getOwnerContact();
        };
    }
}
