package com.qrust.api;

import com.qrust.service.UserService;
import com.qrust.service.impl.CognitoService;
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
    CognitoService cognitoService;
    @Inject
    UserService userService;

    //TODO: this method is only for testing purposes, remove it later
//    @POST
//    @Path("/upgrade/{group}")
//    @Authenticated() // Only normal users can upgrade
//    public Response upgradeUserGroup(@Context SecurityContext securityContext, @PathParam("group") String group) {
//        String username = securityContext.getUserPrincipal().getName();
//        cognitoService.upgradeUserGroup(username, group);
//        return Response.ok().entity("User group upgraded").build();
//    }

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

    //TODO: this method is only for testing purposes, remove it later
//    @POST
//    @Path("/downgrade/{group}")
//    @Authenticated()
//    public Response downgradeUser(@Context SecurityContext securityContext, @PathParam("group") String group) {
//        String username = securityContext.getUserPrincipal().getName();
//        cognitoService.downgradeUser(username, group);
//        return Response.ok().entity("User downgraded").build();
//    }
}
