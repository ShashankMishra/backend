package com.qrust.api.dto;

import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QRCodeRequest {

    @NotNull
    private QRType type;

    // Use DTO subtypes for details
    @Valid
    private QRDetailsDto details;

}
