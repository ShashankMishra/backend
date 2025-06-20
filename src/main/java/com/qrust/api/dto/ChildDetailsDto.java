package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChildDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.CHILD;
    @NotBlank
    private String fullName;
    @Size(max = 30)
    private String schoolName;
    @Valid
    private ContactDto schoolContact;
    @Valid
    @NotNull
    private ContactDto emergencyContact;
    @Valid
    private MedicalDetailsDto medicalDetails;
}

