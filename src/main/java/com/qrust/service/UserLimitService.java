package com.qrust.service;

import com.qrust.domain.User;
import com.qrust.domain.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserLimitService {
    @Inject
    @ConfigProperty(name = "quarkus.qr-plan.business.max-allowed", defaultValue = "1000")
    int businessLimit;
    @Inject
    @ConfigProperty(name = "quarkus.qr-plan.premium.max-allowed", defaultValue = "100")
    int premiumLimit;
    @Inject
    @ConfigProperty(name = "quarkus.qr-plan.basic.max-allowed", defaultValue = "10")
    int basicLimit;
    @Inject
    @ConfigProperty(name = "quarkus.qr-plan.free.max-allowed", defaultValue = "2")
    int freeLimit;

    public int getLimitForUser(User user) {
        if (user == null || user.getUserId() == null) {
            return 0;
        }
        UserRole role = user.getRole() != null ? user.getRole() : UserRole.FREE;
        return switch (role) {
            case BUSINESS -> businessLimit;
            case PREMIUM -> premiumLimit;
            case BASIC -> basicLimit;
            case FREE -> freeLimit;
        };
    }
}
