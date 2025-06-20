package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.VEHICLE;
    @NotBlank
    @Size(min = 4, max = 15)
    private String numberPlate;
    @NotBlank
    @Size(min = 1, max = 20)
    private String modelDescription;
    @Valid
    @NotNull
    private ContactDto ownerContact;
    @NotNull
    @Valid
    private ContactDto emergencyContact;
}

