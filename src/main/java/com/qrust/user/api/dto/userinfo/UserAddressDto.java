package com.qrust.user.api.dto.userinfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressDto {
    String addressId;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    @Size(min = 1, max = 10)
    private String pincode;

    @NotBlank
    @Size(min = 10, max = 10)
    private String phoneNumber;
}
