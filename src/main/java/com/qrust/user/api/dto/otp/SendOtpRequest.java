package com.qrust.user.api.dto.otp;

import com.qrust.user.api.dto.ContactDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOtpRequest {
    private ContactDto contactDto;
    private OtpType otpType;
}
