package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.VEHICLE;
    @NotNull
    private String numberPlate;
    @NotNull
    private String modelDescription;
    @NotNull
    private ContactDto ownerContact;
    @NotNull
    private ContactDto emergencyContact;
}

