package com.qrust.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ContactDto {

    private String name;


    // add annotations for phone number validation
    @NotBlank
    @Size(min = 10, max = 10)
    private String phoneNumber;

    @Builder.Default
    private ContactPreference preference = new ContactPreference();
    private Date createdAt;
}

