package com.qrust.user.service;

import com.qrust.common.domain.User;
import com.qrust.common.domain.UserRole;
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
    @ConfigProperty(name = "plan.premium.scans.max-allowed", defaultValue = "200")
    int premiumScanLimit;
    @Inject
    @ConfigProperty(name = "plan.basic.scans.max-allowed", defaultValue = "10")
    int basicScanLimit;
    @Inject
    @ConfigProperty(name = "plan.free.scans.max-allowed", defaultValue = "10")
    int freeScanLimit;

    public int getQrLimitForUser(User user) {
        return getLimitForUser(user, businessQrLimit, premiumQrLimit, basicQrLimit, freeQrLimit);
    }

    private int getLimitForUser(User user, int businessLimit, int premiumLimit, int basicLimit, int freeLimit) {
        if (user == null || user.getUserId() == null) {
            return 0;
        }
        UserRole role = user.getHighestRole();

        return switch (role) {
            case BUSINESS -> businessLimit;
            case PREMIUM -> premiumLimit;
            case BASIC -> basicLimit;
            case FREE -> freeLimit;
        };
    }

    public int getScanLimitForUser(User user) {
        return getLimitForUser(user, businessScanLimit, premiumScanLimit, basicScanLimit, freeScanLimit);
    }
}
