package com.qrust.user.api.dto;

import com.qrust.common.domain.QRType;
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
