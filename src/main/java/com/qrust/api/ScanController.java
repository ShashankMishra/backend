package com.qrust.api;

import com.qrust.domain.QRCode;
import com.qrust.domain.ScanHistory;
import com.qrust.service.QRCodeService;
import com.qrust.service.ScanService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Path("/scans")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ScanController {

    @Inject
    ScanService scanService;

    @Inject
    QRCodeService qrCodeService;

    @GET
    @Path("/{scanId}")
    @PermitAll
    public Response get(@PathParam("scanId") UUID scanId) {
        ScanHistory scanHistory = scanService.getScan(scanId);
        if (scanHistory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        QRCode qrCode = qrCodeService.getQr(scanHistory.getQrId());
        return Response.ok(qrCodeService.toResponse(qrCode)).build();
    }

    @GET
    @Authenticated
    public Response getAllScans() {
        List<ScanHistory> scanHistory = scanService.getAllScanHistory();
        return Response.ok(scanHistory).build();
    }


}
