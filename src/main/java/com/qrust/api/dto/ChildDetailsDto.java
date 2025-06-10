package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChildDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.CHILD;
    @NotNull
    private String fullName;
    private String schoolName;
    @Valid
    @NotNull
    private ContactDto guardianContact;
    @Valid
    private ContactDto emergencyContact;
    private MedicalDetailsDto medicalDetails;
}

