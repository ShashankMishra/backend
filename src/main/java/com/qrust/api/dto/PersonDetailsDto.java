package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.Optional;

@Data
public class PersonDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.PERSON;
    @NotNull
    private String fullName;
    @Valid
    private ContactDto emergencyContact;
    private Optional<MedicalDetailsDto> medicalDetails;
}

