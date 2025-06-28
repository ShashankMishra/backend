package com.qrust.api;

import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
import com.qrust.domain.QRCode;
import com.qrust.domain.ScanHistory;
import com.qrust.exceptions.LimitReached;
import com.qrust.service.QRCodeService;
import com.qrust.service.ScanService;
import io.quarkus.security.Authenticated;
import io.vertx.ext.web.RoutingContext;
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
    public Response create(@Valid QRCodeRequest request) throws LimitReached {
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
                         @Context RoutingContext rc) {
        log.info("Scan QR: {}", qrId);
        UriBuilder.fromUri(frontendUri).build();
        URI redirectUrl;
        try {

            ScanHistory scanHistory = createScanHistory(qrId, headers, rc);

            redirectUrl = UriBuilder.fromUri(frontendUri)
                    .path("scans")
                    .path(scanHistory.getScanId().toString())
                    .build();
        } catch (LimitReached | IllegalArgumentException e) {
            log.warn("Limit reached for QR ID {}: {}", qrId, e.getMessage());
            redirectUrl = UriBuilder.fromUri(frontendUri)
                    .path("error")
                    .queryParam("message", e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error processing scan for QR ID {}: {}", qrId, e.getMessage(), e);
            redirectUrl = UriBuilder.fromUri(frontendUri).path("error")
                    .queryParam("message", "Server error occurred while processing your request, Please retry scaning")
                    .build();
        }

        log.info("Redirecting to: {}", redirectUrl);
        return Response.seeOther(redirectUrl).build();
    }

    @GET
    @Path("/{qrId}/scans")
    @Authenticated
    public Response scan(@PathParam("qrId") UUID qrId) {
        log.info("Get All scans for QR: {}", qrId);

        QRCode qrCode = qrCodeService.getQr(qrId);
        if (qrCode == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("QR Code not found")
                    .build();
        }
        List<ScanHistory> scanHistory = scanService.getScanHistoryForQr(qrId);
        // sort scan history by scan timestamp in descending order and just send latest 20
        scanHistory.sort((s1, s2) -> s2.getScanTimestamp().compareTo(s1.getScanTimestamp()));
        if (scanHistory.size() > 10) {
            scanHistory = scanHistory.subList(0, 10);
        }
        return Response.ok(scanHistory).build();
    }

    private ScanHistory createScanHistory(UUID qrId, HttpHeaders headers, RoutingContext rc) {

        String ip = headers.getHeaderString("X-Forwarded-For");
        if (ip == null) {
            ip = rc.request().remoteAddress().host();// fallback
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
