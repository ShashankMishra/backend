package com.qrust.auth;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Collections;
import java.util.List;

@RequestScoped
public class AuthServiceImpl implements AuthService {
    @ConfigProperty(name = "auth.enabled", defaultValue = "false")
    boolean authEnabled;

    @Inject
    JsonWebToken jwt;

    @Context
    HttpHeaders headers;

    @Override
    public String getCurrentUserId() {
        System.out.println(" Get user " );
        System.out.println(headers.getRequestHeader(HttpHeaders.AUTHORIZATION));
        if (!authEnabled) return "dev-user";
        return jwt.getClaim("sub");
    }

    @Override
    public String getCurrentUserEmail() {
        if (!authEnabled) return "dev@example.com";
        return jwt.getClaim("email");
    }

    @Override
    public List<String> getCurrentUserRoles() {
        if (!authEnabled) return Collections.singletonList("DEV");
        Object groups = jwt.getClaim("cognito:groups");
        if (groups instanceof List<?>) {
            return (List<String>) groups;
        } else if (groups instanceof String) {
            return List.of((String) groups);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isAuthEnabled() {
        return authEnabled;
    }
}

