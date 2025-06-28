package com.qrust.service;

import com.qrust.domain.User;

public interface UserService {
    User getCurrentUser();
    User getUserById(String userId);
}

