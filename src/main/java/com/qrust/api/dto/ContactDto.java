package com.qrust.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactDto {
    @NotNull private String name;
    @NotNull private String phoneNumber;
}

