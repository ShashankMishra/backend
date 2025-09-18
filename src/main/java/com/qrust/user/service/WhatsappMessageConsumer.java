package com.qrust.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrust.common.JsonUtil;
import com.qrust.user.api.dto.ScanMessage;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Arrays;

import static com.qrust.Constants.*;

@ApplicationScoped
@Slf4j
public class WhatsappMessageConsumer {

    private static final int MAX_RETRIES = 3;
    private static final int DELAY_SECONDS = 15;

    @Inject
    RedisClient redisClient;

    @Inject
    WhatsappMessageService whatsappMessageService;

    private final ObjectMapper objectMapper = JsonUtil.createMapper();

    @Scheduled(every = "5s")
    void moveScheduledMessages() {
        long now = Instant.now().getEpochSecond();
        io.vertx.redis.client.Response messages = redisClient.zrangebyscore(Arrays.asList(SCHEDULED_QUEUE_NAME, "-inf", String.valueOf(now)));
        if (messages != null && messages.size() > 0) {
            for (io.vertx.redis.client.Response message : messages) {
                redisClient.lpush(Arrays.asList(QUEUE_NAME, message.toString()));
                redisClient.zrem(Arrays.asList(SCHEDULED_QUEUE_NAME, message.toString()));
            }
        }
    }

    @Scheduled(every = "10s")
    void consumeMessage() {
        io.vertx.redis.client.Response response;
        while ((response = redisClient.rpoplpush(QUEUE_NAME, PROCESSING_QUEUE_NAME)) != null) {
            final String message = response.toString();
            try {
                ScanMessage scanMessage = objectMapper.readValue(message, ScanMessage.class);
                whatsappMessageService.sendMessageOnScan(scanMessage.getQrCode(), scanMessage.getScanId(), scanMessage.getOwnerName());
            } catch (Exception e) {
                log.error("Failed to process message: {}", message, e);
                handleFailedMessage(message);
            } finally {
                redisClient.lrem(PROCESSING_QUEUE_NAME, "1", message);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?") // Every hour
    void recoverStaleMessages() {
        io.vertx.redis.client.Response messages = redisClient.lrange(PROCESSING_QUEUE_NAME, "0", "-1");
        if (messages != null && messages.size() > 0) {
            for (io.vertx.redis.client.Response message : messages) {
                log.info("Recovering stale message: {}", message.toString());
                redisClient.lpush(Arrays.asList(QUEUE_NAME, message.toString()));
                redisClient.lrem(PROCESSING_QUEUE_NAME, "1", message.toString());
            }
        }
    }

    private void handleFailedMessage(String message) {
        try {
            ScanMessage scanMessage = objectMapper.readValue(message, ScanMessage.class);
            int retryCount = scanMessage.getRetryCount();
            if (retryCount < MAX_RETRIES) {
                scanMessage.setRetryCount(retryCount + 1);
                String updatedMessage = objectMapper.writeValueAsString(scanMessage);
                long scheduledTime = Instant.now().getEpochSecond() + DELAY_SECONDS;
                redisClient.zadd(Arrays.asList(SCHEDULED_QUEUE_NAME, String.valueOf(scheduledTime), updatedMessage));
            } else {
                redisClient.lpush(Arrays.asList(DEAD_LETTER_QUEUE_NAME, message));
            }
        } catch (Exception e) {
            log.error("Failed to handle failed message: {}", message, e);
        }
    }
}
