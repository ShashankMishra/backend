package com.qrust.auth;

import java.util.List;

public interface AuthService {
    String getCurrentUserId();
    String getCurrentUserEmail();
    List<String> getCurrentUserRoles();
    boolean isAuthEnabled();
}

