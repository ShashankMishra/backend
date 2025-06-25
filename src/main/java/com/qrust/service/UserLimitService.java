package com.qrust.service;

import com.qrust.domain.User;
import com.qrust.domain.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserLimitService {
    @Inject
    @ConfigProperty(name = "plan.business.qrs.max-allowed", defaultValue = "1000")
    int businessQrLimit;
    @Inject
    @ConfigProperty(name = "plan.premium.qrs.max-allowed", defaultValue = "100")
    int premiumQrLimit;
    @Inject
    @ConfigProperty(name = "plan.basic.qrs.max-allowed", defaultValue = "10")
    int basicQrLimit;
    @Inject
    @ConfigProperty(name = "plan.free.qrs.max-allowed", defaultValue = "2")
    int freeQrLimit;

    @Inject
    @ConfigProperty(name = "plan.business.scans.max-allowed", defaultValue = "1000")
    int businessScanLimit;
    @Inject
    @ConfigProperty(name = "plan.premium.scans.max-allowed", defaultValue = "100")
    int premiumScanLimit;
    @Inject
    @ConfigProperty(name = "plan.basic.scans.max-allowed", defaultValue = "10")
    int basicScanLimit;
    @Inject
    @ConfigProperty(name = "plan.free.scans.max-allowed", defaultValue = "10")
    int freeScanLimit;

    public int getQrLimitForUser(User user) {
        if (user == null || user.getUserId() == null) {
            return 0;
        }
        UserRole role = user.getRole() != null ? user.getRole() : UserRole.FREE;
        return switch (role) {
            case BUSINESS -> businessQrLimit;
            case PREMIUM -> premiumQrLimit;
            case BASIC -> basicQrLimit;
            case FREE -> freeQrLimit;
        };
    }

    public int getScanLimitForUser(User user) {
        if (user == null || user.getUserId() == null) {
            return 0;
        }
        UserRole role = user.getRole() != null ? user.getRole() : UserRole.FREE;
        return switch (role) {
            case BUSINESS -> businessScanLimit;
            case PREMIUM -> premiumScanLimit;
            case BASIC -> basicScanLimit;
            case FREE -> freeScanLimit;
        };
    }
}
