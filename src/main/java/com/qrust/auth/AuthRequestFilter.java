package com.qrust.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {
    @Inject
    AuthService authService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        // Allow public QR info endpoint
        if (path.startsWith("qr/") || path.startsWith("qr")) {
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

