package com.qrust.user.service;

import com.qrust.common.domain.QRCode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class LimitService {

    @Inject
    @ConfigProperty(name = "qrs.max-allowed", defaultValue = "10")
    int qrCreationLimit;


    int businessScanLimit;
    @Inject
    @ConfigProperty(name = "plan.premium.scans.max-allowed", defaultValue = "200")
    int premiumScanLimit;

    @Inject
    @ConfigProperty(name = "plan.free.scans.max-allowed", defaultValue = "10")
    int freeScanLimit;

    public int getQrCreationLimitForUser() {
        return qrCreationLimit;
    }

    public int getScanLimitForQR(QRCode qrCode) {
        return qrCode.isPremium() ? premiumScanLimit : freeScanLimit;
    }
}
