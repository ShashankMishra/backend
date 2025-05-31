package com.qrust.api;

import com.qrust.auth.AuthService;
import com.qrust.domain.PlanType;
import com.qrust.domain.QRCode;
import com.qrust.service.QRCodeService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/qr")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QRCodeController {
    @Inject
    QRCodeService qrCodeService;
    @Inject
    AuthService authService;

    // Public endpoint: no auth required
    @GET
    @Path("/{token}")
    @PermitAll
    public Response getQrInfo(@PathParam("token") String publicToken) {
        QRCode qr = qrCodeService.getQrInfo(publicToken);
        if (qr == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(qr).build();
    }

    // Admin endpoint: create QR codes
    @POST
    @Path("/admin/create")
    public Response createQrCodes(@QueryParam("count") int count, @QueryParam("planType") PlanType planType) {
        // Optionally check roles via authService.getCurrentUserRoles()
        List<QRCode> codes = qrCodeService.createQrCodes(count, planType);
        return Response.status(Response.Status.CREATED).entity(codes).build();
    }

    // Admin endpoint: revoke QR code
    @POST
    @Path("/admin/revoke/{qrId}")
    public Response revokeQr(@PathParam("qrId") UUID qrId) {
        qrCodeService.revokeQr(qrId);
        return Response.noContent().build();
    }
}

