package com.qrust.service;

import com.qrust.domain.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import static com.qrust.domain.UserRole.FREE;

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
        String sub = securityIdentity.getPrincipal().getName();
        User user = new User(sub, FREE);
        return user;
    }
}
