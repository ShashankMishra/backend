package com.qrust.common.repository;

import com.qrust.common.domain.user.UserInfo;

public interface UserInfoRepository {
    void save(UserInfo userInfo);
    UserInfo getByUserId(String userId);
}
