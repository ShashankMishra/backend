package com.qrust.user.api.dto.userinfo;

import com.qrust.common.domain.user.UserAddress;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
    private final List<UserAddress> userAddresses;
}
