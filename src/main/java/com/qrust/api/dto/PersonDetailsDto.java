package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PersonDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.PERSON;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 20)
    private String fullName;
    @Valid
    private ContactDto emergencyContact;
    @Valid
    private MedicalDetailsDto medicalDetails;
}

