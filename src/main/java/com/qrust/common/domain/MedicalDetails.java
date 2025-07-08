package com.qrust.common.domain;

import lombok.Data;

@Data
public class MedicalDetails {
    private BloodGroup bloodGroup;
    private String medicalHistory;
    private String allergies;
    private String currentMedications;
}
