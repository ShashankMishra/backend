package com.qrust.api;

import com.qrust.auth.AuthService;
import com.qrust.domain.UserQRCodeLink;
import com.qrust.service.UserQRCodeLinkService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/link")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserQRCodeLinkController {
    @Inject
    AuthService authService;
    @Inject
    UserQRCodeLinkService linkService;

    @POST
    @Path("/activate")
    public Response activateQr(@QueryParam("activationCode") String activationCode) {
        String userId = authService.getCurrentUserId();
        UserQRCodeLink link = linkService.activateQr(activationCode, userId);
        return Response.ok(link).build();
    }

    @POST
    @Path("/link")
    public Response linkQrToProfile(@QueryParam("qrId") UUID qrId, @QueryParam("profileId") UUID profileId) {
        String userId = authService.getCurrentUserId();
        linkService.linkQrToProfile(qrId, profileId, userId);
        return Response.noContent().build();
    }

    @GET
    @Path("/profile")
    public Response profile() {
        String userId = authService.getCurrentUserId();
        return Response.ok(userId).build();
    }

    @GET
    public Response getLinkedQrCodes() {
        String userId = authService.getCurrentUserId();
        List<UserQRCodeLink> links = linkService.getLinkedQrCodes(userId);
        return Response.ok(links).build();
    }
}

