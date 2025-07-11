package com.qrust.admin.api.dto;

import com.qrust.common.domain.QRType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkQrCodeRequest {

    @NotNull
    private QRType type;
    @Min(1)
    @Max(500)
    private int count;

}
