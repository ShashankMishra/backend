package com.qrust.user.api.dto;

import com.qrust.common.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QRCodeRequest {

    @NotNull
    private QRType type;

    // Use DTO subtypes for details
    @Valid
    private QRDetailsDto details;

}
