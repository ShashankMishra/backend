package com.qrust.user.api.dto.userinfo;

import com.qrust.common.domain.user.UserAddress;
import lombok.Data;

@Data
public class UpgradeUserInfoRequest {
    private final UserAddress userAddress;
}
