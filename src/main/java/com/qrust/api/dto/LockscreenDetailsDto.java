package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private MedicalDetailsDto medicalDetails;
}

