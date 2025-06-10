package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LuggageDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.LUGGAGE;
    @NotNull private String description;
    @Valid @NotNull private ContactDto ownerContact;
    @Valid @NotNull private ContactDto emergencyContact;

}

