package com.qrust.common.interceptor;

import com.qrust.common.redis.RedisService;
import com.qrust.user.exceptions.LimitReachedException;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@RateLimitByIp
@Interceptor
@Slf4j
public class RateLimitByIpInterceptor implements Serializable {

    @Inject
    RedisService redisService;

    @Context
    HttpHeaders headers;

    @AroundInvoke
    public Object rateLimit(InvocationContext context) throws Exception {
        RateLimitByIp annotation = context.getMethod().getAnnotation(RateLimitByIp.class);
        int windowSize = annotation.windowSize();
        int maxRequests = annotation.maxRequests();
        String ipAddress = headers.getHeaderString("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            log.info("Rate limiting by IP address is not applicable as the IP address is null or empty.");
            return context.proceed();
        }

        log.info("Applying rate limiting for IP address: {}", ipAddress);

        String key = "rate-limit:ip:" + ipAddress + ":" + context.getMethod().getName();

        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - windowSize * 1000L;

        redisService.getRedisAPI().zremrangebyscore(key, "-inf", String.valueOf(windowStart)).toCompletionStage().toCompletableFuture().get();

        long requestCount = redisService.getRedisAPI().zcard(key).toCompletionStage().toCompletableFuture().get().toLong();

        if (requestCount >= maxRequests) {
            log.warn("Rate limit exceeded for IP address: {}", ipAddress);
            throw new LimitReachedException("Rate limit exceeded for IP");
        }

        redisService.getRedisAPI().zadd(java.util.List.of(key, String.valueOf(currentTime), String.valueOf(currentTime))).toCompletionStage().toCompletableFuture().get();
        redisService.getRedisAPI().expire(java.util.List.of(key, String.valueOf(windowSize)));

        return context.proceed();
    }
}
