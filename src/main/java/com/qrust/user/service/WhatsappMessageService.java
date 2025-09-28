package com.qrust.user.service;

import com.qrust.common.Utils;
import com.qrust.common.client.whatsapp.WhatsappClient;
import com.qrust.common.client.whatsapp.WhatsappRequest;
import com.qrust.common.domain.Contact;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.ScanHistory;
import com.qrust.common.redis.RedisService;
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

    @Inject
    RedisService redisService;

    @ConfigProperty(name = "whatsapp.system.user.token")
    String systemUserToken;

    @ConfigProperty(name = "whatsapp.phone.number.id")
    String phoneNumberId;

    public void sendMessageOnScan(QRCode qrCode, UUID scanId, String ownerName) {
        log.info("Sending whatsapp message for qr code scan: {}", qrCode.getId());

        ScanHistory scan = scanService.getScan(scanId);

        String latitude = scan.getLocation().getLatitude().toString();
        String longitude = scan.getLocation().getLongitude().toString();

        Contact ownerContact = Utils.getOwnerContact(qrCode);

        if (ownerContact == null) {
            log.warn("Owner contact not found for qr code: {}", qrCode.getId());
            return;
        }

        String ownerPhoneNumber = "91" + ownerContact.getPhoneNumber();

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

        whatsappClient.sendMessage("Bearer " + systemUserToken, phoneNumberId, request);
        log.info("Whatsapp message sent successfully to: {}", ownerPhoneNumber);
    }

    public String sendOtp(String phoneNumber) {
        String destinationPhoneNUmber = "91" + phoneNumber; //because whatsapp requires country code

        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);
        String verificationId = UUID.randomUUID().toString();
        redisService.storeOtp(verificationId, otp);

        WhatsappRequest.Template template = WhatsappRequest.Template.builder()
                .name("wa_otp_verification")
                .language(WhatsappRequest.Language.builder().code("en").build())
                .components(List.of(
                        WhatsappRequest.Component.builder()
                                .type("body")
                                .parameters(List.of(
                                        WhatsappRequest.Parameter.builder().type("text").text(otp).build()
                                ))
                                .build(),
                        WhatsappRequest.Component.builder()
                                .type("button")
                                .subType("url")
                                .index(0)
                                .parameters(List.of(
                                        WhatsappRequest.Parameter.builder().type("text").text(otp).build()
                                ))
                                .build()
                ))
                .build();

        WhatsappRequest request = WhatsappRequest.builder()
                .messagingProduct("whatsapp")
                .to(destinationPhoneNUmber)
                .type("template")
                .template(template)
                .build();

        whatsappClient.sendMessage("Bearer " + systemUserToken, phoneNumberId, request);
        log.info("Whatsapp OTP sent successfully to: {}", destinationPhoneNUmber);

        return verificationId;
    }

    public boolean validateOtp(String verificationId, String otp) {
        String storedOtp = redisService.getOtp(verificationId);
        return storedOtp != null && storedOtp.equals(otp);
    }
}