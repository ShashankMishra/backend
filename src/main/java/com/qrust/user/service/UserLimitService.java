package com.qrust.user.service;

import com.qrust.common.domain.User;
import com.qrust.common.domain.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserLimitService {

    @Inject
    @ConfigProperty(name = "plan.premium.qrs.max-allowed", defaultValue = "100")
    int premiumQrLimit;

    @Inject
    @ConfigProperty(name = "plan.free.qrs.max-allowed", defaultValue = "2")
    int freeQrLimit;

    int businessScanLimit;
    @Inject
    @ConfigProperty(name = "plan.premium.scans.max-allowed", defaultValue = "200")
    int premiumScanLimit;

    @Inject
    @ConfigProperty(name = "plan.free.scans.max-allowed", defaultValue = "10")
    int freeScanLimit;

    public int getQrLimitForUser(User user) {
        return getLimitForUser(user, premiumQrLimit, freeQrLimit);
    }

    private int getLimitForUser(User user, int premiumLimit, int freeLimit) {
        if (user == null || user.getUserId() == null) {
            return 0;
        }
        UserRole role = user.getHighestRole();

        return switch (role) {
            case PREMIUM -> premiumLimit;
            case FREE -> freeLimit;
        };
    }

    public int getScanLimitForUser(User user) {
        return getLimitForUser(user, premiumScanLimit, freeScanLimit);
    }
}
