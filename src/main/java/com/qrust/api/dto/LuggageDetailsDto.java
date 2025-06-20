package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LuggageDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.LUGGAGE;
    @Size(min = 1, max = 30)
    @NotBlank
    private String description;
    @Valid
    @NotNull
    private ContactDto ownerContact;
    @Valid
    @NotNull
    private ContactDto emergencyContact;

}

