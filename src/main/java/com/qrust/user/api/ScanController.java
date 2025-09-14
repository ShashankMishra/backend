package com.qrust.user.api;

import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.ScanHistory;
import com.qrust.user.api.dto.LocationRequest;
import com.qrust.user.service.CallService;
import com.qrust.user.service.QRCodeService;
import com.qrust.user.service.ScanService;
import com.qrust.user.service.WhatsappMessageService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Path("/scans")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ScanController {

    @Inject
    ScanService scanService;

    @Inject
    QRCodeService qrCodeService;
    @Inject
    CallService callService;

    @Inject
    WhatsappMessageService whatsappMessageService;

    @Inject
    @ConfigProperty(name = "masking.enabled")
    boolean maskingEnabled;

    @GET
    @Path("/{scanId}")
    @PermitAll
    public Response get(@PathParam("scanId") UUID scanId) {
        ScanHistory scanHistory = scanService.getScan(scanId);
        if (scanHistory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Check if the scan history is older than 2 minute
        if (scanHistory.getScanTimestamp().isBefore(Instant.now().minusSeconds(180))) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
        QRCode qrCode = qrCodeService.getQr(scanHistory.getQrId());
        if(maskingEnabled) {
            qrCode = callService.getMaskedNumberForQr(qrCode);
        }

        QRCode finalQrCode = qrCode;
        CompletableFuture.runAsync(() -> whatsappMessageService.sendMessageOnScan(finalQrCode));

        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

    @PUT
    @Path("/{scanId}/location")
    @PermitAll
    public Response get(@PathParam("scanId") UUID scanId, @Valid LocationRequest locationRequest) {
        ScanHistory scanHistory = scanService.getScan(scanId);
        // @TODO add a check with current ip and scanHistory ip
        if (scanHistory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scanService.updateScanLocation(scanHistory, locationRequest);
        return Response.ok().build();
    }

    @GET
    @Authenticated
    public Response getAllScans() {
        List<ScanHistory> scanHistory = scanService.getAllScanHistory();
        // sort scan history by scan timestamp in descending order and just send latest 10
        scanHistory.sort((s1, s2) -> s2.getScanTimestamp().compareTo(s1.getScanTimestamp()));
        if (scanHistory.size() > 100) {
            scanHistory = scanHistory.subList(0, 100);
        }
        // Convert to QRCodeResponse
        return Response.ok(scanHistory).build();
    }


}

