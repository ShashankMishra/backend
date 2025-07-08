package com.qrust.user.api.dto;

import com.qrust.common.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

