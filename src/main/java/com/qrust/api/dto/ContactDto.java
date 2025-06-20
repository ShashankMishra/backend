package com.qrust.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactDto {
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;


    // add annotations for phone number validation
    @NotBlank
    @Size(min = 10, max = 10)
    private String phoneNumber;
}

