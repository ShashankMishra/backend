package com.qrust.domain;

import lombok.Data;

@Data
public class PersonDetails implements QRDetails {
    private final QRType type = QRType.PERSON;
    private String fullName;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;
}

