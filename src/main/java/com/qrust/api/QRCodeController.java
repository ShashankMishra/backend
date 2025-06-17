package com.qrust.api;

import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
import com.qrust.domain.QRCode;
import com.qrust.domain.ScanHistory;
import com.qrust.service.QRCodeService;
import com.qrust.service.ScanService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Path("/qr-codes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class QRCodeController {
    @Inject
    QRCodeService qrCodeService;

    @Inject
    ScanService scanService;


    @ConfigProperty(name = "quarkus.frontend.uri")
    String frontendUri;

    @POST
    @Authenticated
    public Response create(@Valid QRCodeRequest request) {
        if (getAll().size() >= 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Maximum number of QR codes reached (100).").build();
        }
        QRCode qrCode = qrCodeService.createQr(request);
        return Response.status(Response.Status.CREATED).entity(qrCodeService.toResponse(qrCode)).build();
    }

    @GET
    @Authenticated
    public List<QRCodeResponse> getAll() {
        List<QRCode> allQrs = qrCodeService.getAllQrs();
        List<QRCodeResponse> response = allQrs.stream()
                .map(qrCodeService::toResponse)
                .toList();
        return response;
    }

    @GET
    @Path("/{id}")
    @Authenticated
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

    @GET
    @Path("/{qrId}/scan")
    @PermitAll
    public Response scan(@PathParam("qrId") UUID qrId,
                         @Context HttpHeaders headers,
                         @Context UriInfo uriInfo) {
        log.info("Scan QR: {}", qrId);

        QRCode qrCode = qrCodeService.getQr(qrId);
        if (qrCode == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("QR Code not found")
                    .build();
        }

        ScanHistory scanHistory = createScanHistory(qrId, headers, uriInfo);

        URI redirectUrl = UriBuilder.fromUri(frontendUri)
                .path("scans")
                .path(scanHistory.getScanId().toString())
                .build();

        log.info("Redirecting to: {}", redirectUrl);

        return Response.seeOther(redirectUrl).build();
    }

    private ScanHistory createScanHistory(UUID qrId, HttpHeaders headers, UriInfo uriInfo) {
        String ip = headers.getHeaderString("X-Forwarded-For");
        if (ip == null) {
            ip = uriInfo.getRequestUri().getHost(); // fallback
        }
        String userAgent = headers.getHeaderString("User-Agent");

        ScanHistory history = new ScanHistory();
        history.setScanId(UUID.randomUUID());
        history.setQrId(qrId);
        history.setScanTimestamp(Instant.now());
        history.setScannerIp(ip);
        history.setDeviceInfo(userAgent);

        return scanService.save(history);
    }
}
