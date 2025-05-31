package com.qrust.api;

import com.qrust.auth.AuthService;
import com.qrust.domain.PlanType;
import com.qrust.domain.QRCode;
import com.qrust.service.QRCodeService;
import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
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

    // Create a new QR code with type and details
    @POST
    public Response createQr(QRCodeRequest qrRequest) {
        QRCodeResponse created = qrCodeService.createQr(qrRequest);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Update an existing QR code and its details
    @PUT
    @Path("/{qrId}")
    public Response updateQr(@PathParam("qrId") UUID qrId, QRCodeRequest qrRequest) {
        QRCodeResponse updated = qrCodeService.updateQr(qrId, qrRequest);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    // Delete a QR code
    @DELETE
    @Path("/{qrId}")
    public Response deleteQr(@PathParam("qrId") UUID qrId) {
        qrCodeService.deleteQr(qrId);
        return Response.noContent().build();
    }

    // List all QR codes
    @GET
    public Response getAllQrs() {
        List<QRCodeResponse> responses = qrCodeService.getAllQrs().stream()
            .map(qrCodeService::toResponse).toList();
        return Response.ok(responses).build();
    }

    // Link a QR code to a user
    @POST
    @Path("/{qrId}/link/{userId}")
    public Response linkQrToUser(@PathParam("qrId") UUID qrId, @PathParam("userId") String userId) {
        qrCodeService.linkQrToUser(qrId, userId);
        return Response.ok().build();
    }

    // Fetch QR code with all associated details
    @GET
    @Path("/details/{qrId}")
    public Response getQrWithDetails(@PathParam("qrId") UUID qrId) {
        QRCodeResponse qr = qrCodeService.getQrWithDetailsResponse(qrId);
        if (qr == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(qr).build();
    }
}
