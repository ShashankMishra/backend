package com.qrust.common.interceptor;

import com.qrust.user.service.UserService;
import com.qrust.common.redis.RedisService;
import com.qrust.user.exceptions.LimitReachedException;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@RateLimitByUser
@Interceptor
@Slf4j
public class RateLimitByUserInterceptor implements Serializable {

    @Inject
    UserService userService;

    @Inject
    RedisService redisService;

    @AroundInvoke
    public Object rateLimit(InvocationContext context) throws Exception {
        RateLimitByUser annotation = context.getMethod().getAnnotation(RateLimitByUser.class);
        int windowSize = annotation.windowSize();
        int maxRequests = annotation.maxRequests();
        String userId = userService.getCurrentUser().getUserId();

        log.info("Applying rate limiting for user: {}", userId);

        String key = "rate-limit:user:" + userId + ":" + context.getMethod().getName();

        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - windowSize * 1000L;

        redisService.getRedisAPI().zremrangebyscore(key, "-inf", String.valueOf(windowStart)).toCompletionStage().toCompletableFuture().get();

        long requestCount = redisService.getRedisAPI().zcard(key).toCompletionStage().toCompletableFuture().get().toLong();

        if (requestCount >= maxRequests) {
            log.warn("Rate limit exceeded for user: {}", userId);
            throw new LimitReachedException("Rate limit exceeded for user");
        }

        redisService.getRedisAPI().zadd(java.util.List.of(key, String.valueOf(currentTime), String.valueOf(currentTime))).toCompletionStage().toCompletableFuture().get();
        redisService.getRedisAPI().expire(java.util.List.of(key, String.valueOf(windowSize)));

        return context.proceed();
    }
}