package com.qrust.api.dto;

import com.qrust.domain.Contact;
import com.qrust.domain.MedicalDetails;
import com.qrust.domain.QRType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Optional;

@Data
public class LockscreenDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.LOCKSCREEN;
    @NotNull
    private String deviceName;
    @NotNull
    private ContactDto ownerContact;
    @NotNull
    private ContactDto emergencyContact;
    private Optional<MedicalDetailsDto> medicalDetails;
}

