package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.PERSON;
    @NotNull
    private String fullName;
    @Valid
    private ContactDto emergencyContact;
    private MedicalDetailsDto medicalDetails;
}

