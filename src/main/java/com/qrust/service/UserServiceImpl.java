package com.qrust.service;

import com.qrust.domain.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

// This is a stub. Replace with actual authentication integration.
@Slf4j
@RequestScoped
@Named
public class UserServiceImpl implements UserService {
    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public User getCurrentUser() {
        if (securityIdentity == null || securityIdentity.isAnonymous()) {
            return null;
        }
        User user = new User();
        // OIDC subject (sub) is a stable unique identifier
        String sub = securityIdentity.getPrincipal().getName();
        user.setUserId(sub);
        // Optionally set more fields if present in claims
        String email = securityIdentity.getAttribute("email");
        log.info("Current user id, email: " + sub + "," + email);
        return user;
    }
}
