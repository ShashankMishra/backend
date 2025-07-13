package com.qrust.user.service;

import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.QRStatus;
import com.qrust.user.exceptions.InvaidAccessCodeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@ApplicationScoped
public class ClaimService {

    @Inject
    QRCodeService qrCodeService;
    @Inject
    UserService userService;


    public void verifyClaim(UUID id, String code) {
        QRCode qrCode = qrCodeService.getQr(id);

        if (qrCode == null) {
            throw new IllegalArgumentException("QR code not found");
        }
        if (qrCode.getStatus() != QRStatus.ASSIGNED) {
            throw new IllegalArgumentException("QR code not available to be claimed");
        }


        if (!qrCode.getAccessCode().equals(sha256Hex(code))) {
            throw new InvaidAccessCodeException("Input access code is invalid, Retry.");
        }
        qrCodeService.claimQR(qrCode);
    }
}
