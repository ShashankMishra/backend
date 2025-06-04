package com.qrust.api.dto;

import com.qrust.domain.BloodGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalDetailsDto {
    @NotNull
    private BloodGroup bloodGroup;
    private String medicalHistory;
    private String allergies;
    private String currentMedications;
}

