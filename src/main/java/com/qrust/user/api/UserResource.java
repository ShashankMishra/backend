package com.qrust.user.api;

import com.qrust.user.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource {
    @Inject
    UserService userService;

    @GET
    @Path("/is-premium")
    @RolesAllowed("premium") // Only normal users can upgrade
    public Response isPremium(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return Response.ok().entity("User is premium : " + username).build();
    }

    @GET
    @Path("/orders")
    @Authenticated
    public Response getOrdersForCurrentUser() {
        return Response.ok(userService.getOrdersForCurrentUser()).build();
    }

}
