package com.qrust.domain;

import lombok.Data;

import java.util.Optional;

@Data
public class ChildDetails implements QRDetails {
    private final QRType type = QRType.CHILD;
    private String fullName;
    private Optional<String> schoolName;
    private Contact guardianContact;
    private Contact emergencyContact;
    private Optional<MedicalDetails> medicalDetails;
}

