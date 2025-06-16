package com.qrust.api;

import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
import com.qrust.domain.QRCode;
import com.qrust.service.QRCodeService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/qr-codes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class QRCodeController {
    @Inject
    QRCodeService qrCodeService;

    @POST
    @Authenticated // Only authenticated users can create
    public Response create(@Valid QRCodeRequest request) {
        if (getAll().size() >= 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Maximum number of QR codes reached (100).").build();
        }
        QRCode qrCode = qrCodeService.createQr(request);
        return Response.status(Response.Status.CREATED).entity(qrCodeService.toResponse(qrCode)).build();
    }

    @GET
    public List<QRCodeResponse> getAll() {
        List<QRCode> allQrs = qrCodeService.getAllQrs();
        List<QRCodeResponse> response = allQrs.stream()
                .map(qrCodeService::toResponse)
                .toList();
        return response;
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response get(@PathParam("id") UUID id) {
        QRCode qrCode = qrCodeService.getQr(id);
        if (qrCode == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

    @PUT
    @Path("/{id}")
    @Authenticated
    public Response update(@PathParam("id") UUID id, @Valid QRCodeRequest request) {
        QRCode qrCode = qrCodeService.updateQr(id, request);
        if (qrCode == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

    @DELETE
    @Path("/{id}")
    @Authenticated
    public Response delete(@PathParam("id") UUID id) {
        qrCodeService.deleteQr(id);
        return Response.noContent().build();
    }
}
