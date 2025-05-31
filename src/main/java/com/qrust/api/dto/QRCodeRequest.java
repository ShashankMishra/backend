package com.qrust.api.dto;

import lombok.Data;


@Data
public class QRCodeRequest {
    private QRType type;
    private PersonDetails personDetails;
    private VehicleDetails vehicleDetails;
    private ChildDetails childDetails;
    private LuggageDetails luggageDetails;
    private PhoneDetails phoneDetails;
    private String planType;

    public boolean isDetailsValid() {
        if (type == null) return false;
        switch (type) {
            case PERSON:
                return personDetails != null;
            case VEHICLE:
                return vehicleDetails != null;
            case CHILD:
                return childDetails != null;
            case LUGGAGE:
                return luggageDetails != null;
            case PHONE:
                return phoneDetails != null;
            default:
                return false;
        }
    }
}
