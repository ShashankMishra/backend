package com.qrust.api.dto;

import com.qrust.domain.PlanType;
import com.qrust.domain.QRStatus;
import com.qrust.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.qrust.domain.PlanType.FREE;
import static com.qrust.domain.QRStatus.UNASSIGNED;

@Data
public class QRCodeRequest {

    @NotNull
    private QRType type;

    @NotNull
    private QRStatus status = UNASSIGNED;

    @NotNull
    private PlanType planType = FREE;

    // Use DTO subtypes for details
    @Valid
    private QRDetailsDto details;

}
