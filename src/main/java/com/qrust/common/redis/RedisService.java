package com.qrust.common.redis;

import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@ApplicationScoped
public class RedisService {

    private final RedisAPI redisAPI;

    @ConfigProperty(name = "rate.limit.ttl.seconds", defaultValue = "120")
    Integer rateLimitTtlSeconds;

    private static final int TTL_SECONDS = 600;
    private static final int MAX_RETRIES = 5;

    private static final String SHARED_TAG = "qrust";

    private static final String LUA_SCRIPT = """
            local contactKey = KEYS[1]
            local extensionKey = KEYS[2]
            local ttl = tonumber(ARGV[1])
            local contactNumber = ARGV[2]
            local extension = ARGV[3]
            local qrId = ARGV[4]
            
            local existingExtension = redis.call("HGET", contactKey, "extension")
            if existingExtension then
                redis.call("EXPIRE", contactKey, ttl)
                redis.call("EXPIRE", extensionKey, ttl)
                return existingExtension
            end
            
            if redis.call("EXISTS", extensionKey) == 1 then
                return nil
            end
            
            redis.call("HSET", contactKey, "extension", extension, "qrId", qrId)
            redis.call("EXPIRE", contactKey, ttl)
            redis.call("HSET", extensionKey, "contactNumber", contactNumber, "qrId", qrId)
            redis.call("EXPIRE", extensionKey, ttl)
            return extension
            """;

    public RedisAPI getRedisAPI() {
        return redisAPI;
    }

    public String getOrCreateExtension(String contactNumber, UUID qrId) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String extension = generateRandom6DigitExtension();
            String contactKey = String.format("contact:{%s}:%s", SHARED_TAG, contactNumber);
            String extensionKey = String.format("extension:{%s}:%s", SHARED_TAG, extension);

            try {
                Response response = redisAPI.eval(List.of(
                        LUA_SCRIPT,
                        "2",
                        contactKey,
                        extensionKey,
                        String.valueOf(TTL_SECONDS),
                        contactNumber,
                        extension,
                        qrId.toString()
                )).toCompletionStage().toCompletableFuture().get();

                if (response != null && !response.toString().equalsIgnoreCase("null")) {
                    return response.toString();
                }

            } catch (Exception e) {
                e.printStackTrace(); // Handle properly in production
            }
        }

        throw new IllegalStateException("Failed to assign unique extension after retries.");
    }

    public String getContactNumberByExtension(String extension) {
        String extensionKey = String.format("extension:{%s}:%s", SHARED_TAG, extension);

        try {
            Response response = redisAPI.hget(extensionKey, "contactNumber").toCompletionStage().toCompletableFuture().get();

            if (response != null && !"null".equalsIgnoreCase(response.toString())) {
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void storeSidToContactAndQrId(String sid, String contactNumber, String qrId) {
        String key = "sid:" + sid;
        try {
            if (qrId != null) {
                redisAPI.hset(List.of(key, "contactNumber", contactNumber, "qrId", qrId))
                        .toCompletionStage().toCompletableFuture().get();
            } else {
                redisAPI.hset(List.of(key, "contactNumber", contactNumber))
                        .toCompletionStage().toCompletableFuture().get();
            }
            redisAPI.expire(List.of(key, String.valueOf(TTL_SECONDS)))
                    .toCompletionStage().toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace(); // Handle properly
        }
    }

    public String getContactNumberBySid(String sid) {
        String key = "sid:" + sid;
        try {
            Response response = redisAPI.hget(key, "contactNumber").toCompletionStage().toCompletableFuture().get();
            if (response != null && !"null".equalsIgnoreCase(response.toString())) {
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getQrIdBySid(String sid) {
        String key = "sid:" + sid;
        try {
            Response response = redisAPI.hget(key, "qrId").toCompletionStage().toCompletableFuture().get();
            if (response != null && !"null".equalsIgnoreCase(response.toString())) {
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getQrIdByExtension(String extension) {
        String extensionKey = String.format("extension:{%s}:%s", SHARED_TAG, extension);
        try {
            Response response = redisAPI.hget(extensionKey, "qrId").toCompletionStage().toCompletableFuture().get();
            if (response != null && !"null".equalsIgnoreCase(response.toString())) {
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateRandom6DigitExtension() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
    }

    public long incrementAndGetCount(String callFrom, String callTo) {
        String key = String.format("rate_limit:%s:%s", callFrom, callTo);
        try {
            Response response = redisAPI.incr(key).toCompletionStage().toCompletableFuture().get();
            if (response != null) {
                long count = response.toLong();
                if (count == 1) {
                    redisAPI.expire(List.of(key, String.valueOf(rateLimitTtlSeconds)));
                }
                return count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}