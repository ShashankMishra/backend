package com.qrust.user.service;

import com.qrust.common.client.whatsapp.WhatsappClient;
import com.qrust.common.client.whatsapp.WhatsappRequest;
import com.qrust.common.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class WhatsappMessageService {

    @Inject
    @RestClient
    private WhatsappClient whatsappClient;

    @Inject
    ScanService scanService;

    @ConfigProperty(name = "whatsapp.system.user.token")
    String systemUserToken;

    @ConfigProperty(name = "whatsapp.phone.number.id")
    String phoneNumberId;

    public void sendMessageOnScan(QRCode qrCode, UUID scanId) {
        log.info("Sending whatsapp message for qr code scan: {}", qrCode.getId());

        ScanHistory scan = scanService.getScan(scanId);

        String latitude = scan.getLocation().getLatitude().toString();
        String longitude = scan.getLocation().getLongitude().toString();

        Contact ownerContact = getOwnerContact(qrCode);

        if (ownerContact == null) {
            log.warn("Owner contact not found for qr code: {}", qrCode.getId());
            return;
        }

        String ownerPhoneNumber = "91" + ownerContact.getPhoneNumber();
//        String ownerName = ownerContact.getName();
        //TODO: fix this
        String ownerName = "Test User";

        WhatsappRequest.Template template = WhatsappRequest.Template.builder()
                .name("qr_scan_alert")
                .language(WhatsappRequest.Language.builder().code("en").build())
                .components(List.of(
                        WhatsappRequest.Component.builder()
                                .type("body")
                                .parameters(List.of(
                                        WhatsappRequest.Parameter.builder().type("text").parameterName("qr_type").text(qrCode.getType().name().toLowerCase()).build(),
                                        WhatsappRequest.Parameter.builder().type("text").parameterName("owner").text(ownerName).build()
                                ))
                                .build(),
                        WhatsappRequest.Component.builder()
                                .type("button")
                                .subType("url")
                                .index(0)
                                .parameters(List.of(
                                        WhatsappRequest.Parameter.builder().type("text").text(latitude + "," + longitude).build()
                                ))
                                .build()
                ))
                .build();

        WhatsappRequest request = WhatsappRequest.builder()
                .messagingProduct("whatsapp")
                .to(ownerPhoneNumber)
                .type("template")
                .template(template)
                .build();

        try {
            whatsappClient.sendMessage("Bearer " + systemUserToken, phoneNumberId, request);
            log.info("Whatsapp message sent successfully to: {}", ownerPhoneNumber);
        } catch (Exception e) {
            log.error("Failed to send whatsapp message to: {}", ownerPhoneNumber, e);
        }
    }

    private Contact getOwnerContact(QRCode qrCode) {
        QRDetails details = qrCode.getDetails();
        return switch (qrCode.getType()) {
            case VEHICLE -> ((VehicleDetails) details).getOwnerContact();
            case PERSON -> ((PersonDetails) details).getEmergencyContact();
            case CHILD -> ((ChildDetails) details).getEmergencyContact();
            case LUGGAGE -> ((LuggageDetails) details).getOwnerContact();
            case LOCKSCREEN -> ((LockscreenDetails) details).getOwnerContact();
        };
    }
}