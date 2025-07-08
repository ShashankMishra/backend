package com.qrust.admin.api;

import com.qrust.common.domain.QRCode;
import com.qrust.user.api.dto.QRCodeResponse;
import com.qrust.user.service.QRCodeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Path("admin/qr-codes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class QRCodeController {
    @Inject
    QRCodeService qrCodeService;

    @GET
    @RolesAllowed("admin")
    public List<QRCodeResponse> getAll() {
        List<QRCode> allQrs = qrCodeService.getAllQrs();
        List<QRCodeResponse> response = allQrs.stream()
                .map(qrCodeService::toResponse)
                .toList();
        return response;
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response get(@PathParam("id") UUID id) {
        QRCode qrCode = qrCodeService.getQr(id);
        if (qrCode == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

}
