package com.qrust.admin.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MapQrRequest {
    @NotBlank
    @Size(min = 4, max = 20)
    private String barcode;
}
