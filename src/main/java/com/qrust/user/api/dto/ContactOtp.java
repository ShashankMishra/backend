package com.qrust.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactOtp {
    private String verificationId;
    private ContactDto contactDto;
    private String otp;
}
