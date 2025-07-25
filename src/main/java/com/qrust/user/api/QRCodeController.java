package com.qrust.user.api;

import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.ScanHistory;
import com.qrust.user.api.dto.ClaimRequest;
import com.qrust.user.api.dto.QRCodeRequest;
import com.qrust.user.api.dto.QRCodeResponse;
import com.qrust.user.exceptions.LimitReachedException;
import com.qrust.user.service.ClaimService;
import com.qrust.user.service.QRCodeService;
import com.qrust.user.service.ScanService;
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

import static com.qrust.common.domain.QRStatus.ACTIVE;

@Path("/qr-codes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class QRCodeController {
    @Inject
    QRCodeService qrCodeService;

    @Inject
    ScanService scanService;

    @Inject
    ClaimService claimService;


    @ConfigProperty(name = "app.frontend.uri")
    String frontendUri;


    @POST
    @Authenticated
    public Response create(@Valid QRCodeRequest request) throws LimitReachedException {
        QRCode qrCode = qrCodeService.createUserQr(request);
        return Response.status(Response.Status.CREATED).entity(qrCodeService.toResponse(qrCode)).build();
    }

    @GET
    @Authenticated
    public List<QRCodeResponse> getAll() {
        List<QRCode> allQrs = qrCodeService.getAllQrs();
        List<QRCodeResponse> response = allQrs.stream()
                .filter(qrCode -> qrCode.getStatus() == ACTIVE) // Exclude deleted QRs
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
        } catch (LimitReachedException | IllegalArgumentException e) {
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
        if (scanHistory.size() > 200) {
            scanHistory = scanHistory.subList(0, 200);
        }
        return Response.ok(scanHistory).build();
    }

    @PUT
    @Path("/{id}/public")
    @Authenticated
    public Response setPublic(@PathParam("id") UUID id) {
        QRCode qrCode = qrCodeService.updateIsPublic(id, true);
        if (qrCode == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

    @PUT
    @Path("/{id}/private")
    @Authenticated
    public Response setPrivate(@PathParam("id") UUID id) {
        QRCode qrCode = qrCodeService.updateIsPublic(id, false);
        if (qrCode == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }


    @POST
    @Path("/{id}/claim")
    public Response verifyClaim(@PathParam("id") UUID id, @Valid ClaimRequest request) {

        //@TODO add rate limit for this endpoint

        claimService.verifyClaim(id, request.getCode());
        return Response.ok().build();
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
