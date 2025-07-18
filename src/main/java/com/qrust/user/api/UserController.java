package com.qrust.user.api;

import com.qrust.user.api.dto.userinfo.UpgradeUserInfoRequest;
import com.qrust.user.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserController {
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

    @GET
    @Path("/info")
    @Authenticated
    public Response getUserInfo() {
        return Response.ok(userService.getUserInfoResponse()).build();
    }

    @POST
    @Path("/upgrade-info")
    @Authenticated
    public Response upgradeUserInfo(UpgradeUserInfoRequest request) {
        userService.upgradeUserInfo(request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/remove-address")
    @Authenticated
    public Response removeAddress(String addressId) {
        userService.removeUserAddress(addressId);
        return Response.ok().build();
    }
}
