package com.qrust.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {
    @Inject
    AuthService authService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        System.out.println(" Get user --");

        String path = requestContext.getUriInfo().getPath();
        // Allow public QR info endpoint
        log.info("Request path: {}", path);
        if (path.startsWith("/qr")) {
            return;
        }
        if (!authService.isAuthEnabled()) {
            return;
        }
        // If auth is enabled but no valid JWT, abort
        if (authService.getCurrentUserId() == null) {
            requestContext.abortWith(jakarta.ws.rs.core.Response.status(401).entity("Unauthorized").build());
        }
    }
}

