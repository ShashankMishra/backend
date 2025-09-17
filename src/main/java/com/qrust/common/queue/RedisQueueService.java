package com.qrust.common.queue;

import io.quarkus.redis.client.RedisClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Arrays;

@ApplicationScoped
public class RedisQueueService {

    @Inject
    RedisClient redisClient;

    public void enqueueWithDelay(String queueName, String message) {
        long scheduledTime = Instant.now().getEpochSecond() + 15;
        redisClient.zadd(Arrays.asList(queueName, String.valueOf(scheduledTime), message));
    }
}