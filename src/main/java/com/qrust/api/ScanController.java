package com.qrust.api;

import com.qrust.domain.QRCode;
import com.qrust.domain.ScanHistory;
import com.qrust.service.QRCodeService;
import com.qrust.service.ScanService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@Path("/scan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class ScanController {

    @Inject
    ScanService scanService;

    @Inject
    QRCodeService qrCodeService;

    @ConfigProperty(name = "quarkus.frontend.uri")
    String frontendUri;

    @GET
    @Path("/{qrId}")
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
                .path("qr")
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
