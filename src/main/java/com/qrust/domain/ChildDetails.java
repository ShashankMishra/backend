package com.qrust.domain;

import lombok.Data;

@Data
public class ChildDetails implements QRDetails {
    private final QRType type = QRType.CHILD;
    private String fullName;
    private String schoolName;
    private Contact guardianContact;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;
}

