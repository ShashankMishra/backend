package com.qrust.user.service;

import com.qrust.common.domain.QRCode;
import com.qrust.common.redis.RedisService;
import com.qrust.user.exceptions.LimitReachedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@RequiredArgsConstructor
public class LimitService {

    private final RedisService redisService;
    private final UserService userService;

    private static final int OTP_LIMIT_TTL_SECONDS = 86400; // 24 hours

    @Inject
    @ConfigProperty(name = "qrs.max-allowed", defaultValue = "10")
    int qrCreationLimit;

    @Inject
    @ConfigProperty(name = "plan.premium.scans.max-allowed", defaultValue = "200")
    int premiumScanLimit;

    @Inject
    @ConfigProperty(name = "plan.free.scans.max-allowed", defaultValue = "10")
    int freeScanLimit;

    @Inject
    @ConfigProperty(name = "otp.daily.limit", defaultValue = "10")
    int otpDailyLimit;

    public int getQrCreationLimitForUser() {
        return qrCreationLimit;
    }

    public int getScanLimitForQR(QRCode qrCode) {
        return qrCode.isPremium() ? premiumScanLimit : freeScanLimit;
    }

    /**
     * Throws LimitReachedException if the user has exceeded the OTP send limit in the last 24 hours.
     * Uses a Redis sorted set to store OTP send timestamps for the userId.
     */
    public void checkOtpRateLimit() {
        String userId = userService.getCurrentUser().getUserId();
        String redisKey = "otp_limit_window:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - OTP_LIMIT_TTL_SECONDS * 1000L;
        try {
            // Remove all timestamps older than 24 hours
            redisService.getRedisAPI().zremrangebyscore(redisKey, "-inf", String.valueOf(windowStart)).toCompletionStage().toCompletableFuture().get();
            // Get current count in window
            long count = redisService.getRedisAPI().zcard(redisKey).toCompletionStage().toCompletableFuture().get().toLong();
            if (count >= otpDailyLimit) {
                throw new LimitReachedException("OTP send limit reached for last 24 hours");
            }
            // Add current timestamp
            redisService.getRedisAPI().zadd(java.util.List.of(redisKey, String.valueOf(now), String.valueOf(now))).toCompletionStage().toCompletableFuture().get();
            // Set expiry to slightly more than 24 hours to allow cleanup
            redisService.getRedisAPI().expire(java.util.List.of(redisKey, String.valueOf(OTP_LIMIT_TTL_SECONDS + 60)));
        } catch (Exception e) {
            throw new RuntimeException("Error accessing Redis for OTP rate limiting", e);
        }
    }
}
