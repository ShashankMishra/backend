package com.qrust.user.api.dto.userinfo;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class UpgradeUserInfoRequest {
    @Valid
    private final UserAddressDto userAddress;
}
