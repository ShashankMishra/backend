package com.qrust.admin.api;

import com.qrust.admin.api.dto.BulkQrCodeRequest;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.QRType;
import com.qrust.user.api.dto.*;
import com.qrust.user.exceptions.LimitReachedException;
import com.qrust.user.service.QRCodeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

    @POST
    @RolesAllowed("admin")
    public Response create(@Valid BulkQrCodeRequest request) throws LimitReachedException {

        QRType type = request.getType();
        int count = request.getCount();
        QRCodeRequest qrCodeRequest = createDummyRequest(type);

        while (count-- > 0) {
            qrCodeService.createQrForAdmin(qrCodeRequest);
        }
        return Response.status(Response.Status.CREATED)
                .entity("Bulk QR codes created successfully")
                .build();
    }

    @PUT
    @Path("/{id}/map-qr-barcode")
    @RolesAllowed("admin")
    public Response mapQrs(@PathParam("id") UUID id, @Valid MapQrRequest request) {
        log.info("Mapping QR Code with ID: {} to Barcode: {}", id, request.getBarcode());
        qrCodeService.mapQrToBarcode(id, request.getBarcode());
        return Response.ok("QR Code mapped to barcode successfully").build();
    }

    private QRCodeRequest createDummyRequest(QRType type) {
        switch (type) {
            case QRType.VEHICLE:
                return new QRCodeRequest(type, dummyVehicleDetails());
            case QRType.PERSON:
                return new QRCodeRequest(type, dummyPersonDetails());
            case CHILD:
                return new QRCodeRequest(type, dummyChildDetails());
            case LUGGAGE:
                return new QRCodeRequest(type, dummyLuggageDetails());
            default:
                return null;

        }

    }

    private QRDetailsDto dummyLuggageDetails() {
        LuggageDetailsDto luggageDetailsDto = new LuggageDetailsDto();
        luggageDetailsDto.setDescription("Sample Luggage");
        luggageDetailsDto.setOwnerContact(dummyContact());
        luggageDetailsDto.setEmergencyContact(dummyContact());
        return luggageDetailsDto;
    }

    private QRDetailsDto dummyChildDetails() {
        ChildDetailsDto childDetailsDto = new ChildDetailsDto();
        childDetailsDto.setFullName("Sample Child");
        childDetailsDto.setSchoolName("Some");
        childDetailsDto.setSchoolContact(dummyContact());
        childDetailsDto.setEmergencyContact(dummyContact());
        return childDetailsDto;
    }

    private QRDetailsDto dummyPersonDetails() {
        PersonDetailsDto personDetailsDto = new PersonDetailsDto();
        personDetailsDto.setFullName("Sample Person");
        personDetailsDto.setEmergencyContact(dummyContact());
        return personDetailsDto;
    }

    private QRDetailsDto dummyVehicleDetails() {
        VehicleDetailsDto vehicleDetailsDto = new VehicleDetailsDto();
        vehicleDetailsDto.setNumberPlate("ABC-1234");
        vehicleDetailsDto.setModelDescription("Car");
        vehicleDetailsDto.setOwnerContact(dummyContact());
        vehicleDetailsDto.setEmergencyContact(dummyContact());
        return vehicleDetailsDto;
    }

    private ContactDto dummyContact() {
        return ContactDto.builder().phoneNumber("1234567890").name("John Doe").build();
    }


}
