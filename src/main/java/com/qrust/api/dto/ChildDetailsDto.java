package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Optional;

@Data
public class ChildDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.CHILD;
    @NotNull
    private String fullName;
    @NotNull
    private Optional<String> schoolName;
    @Valid
    @NotNull
    private ContactDto guardianContact;
    @Valid
    private ContactDto emergencyContact;
    private Optional<MedicalDetailsDto> medicalDetails;

}

