package com.qrust.common.domain;

import lombok.Data;

@Data
public class VehicleDetails implements QRDetails {
    private final QRType type = QRType.VEHICLE;
    private String numberPlate;
    private String modelDescription;
    private Contact ownerContact;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;
}

