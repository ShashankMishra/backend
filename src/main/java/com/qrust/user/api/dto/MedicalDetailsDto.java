package com.qrust.user.api.dto;

import com.qrust.common.domain.BloodGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalDetailsDto {
    @NotNull
    private BloodGroup bloodGroup;
    @Size(max = 100)
    private String medicalHistory;
    @Size(max = 100)
    private String allergies;
    @Size(max = 100)
    private String currentMedications;
}

