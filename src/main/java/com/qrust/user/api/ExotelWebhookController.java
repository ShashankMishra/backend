package com.qrust.user.api;

import com.qrust.common.domain.CallHistory;
import com.qrust.common.redis.RedisService;
import com.qrust.user.service.CallHistoryService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/exotel-webhook")
@Slf4j
public class ExotelWebhookController {

    @Inject
    RedisService redisService;

    @Inject
    CallHistoryService callHistoryService;

    @ConfigProperty(name = "rate.limit.max.requests", defaultValue = "5")
    Integer rateLimitMaxRequests;

    @GET
    @PermitAll
    @Path("/pass-thru")
    public Response passThru(@QueryParam("CallSid") String callSid, @QueryParam("digits") String digits, @QueryParam("CallFrom") String callFrom, @QueryParam("CallTo") String callTo) {
        log.info("Received pass-thru webhook from Exotel for CallSid: {} and digits: {} and callFrom: {}", callSid, digits, callFrom);

        long count = redisService.incrementAndGetCount(callFrom, callTo);
        if (count > rateLimitMaxRequests) {
            throw new WebApplicationException("Rate limit exceeded for this number combination", Response.Status.TOO_MANY_REQUESTS);
        }

        String extractedDigits = digits.replace("\"", "");
        String contactNumber = redisService.getContactNumberByExtension(extractedDigits);
        String qrId = redisService.getQrIdByExtension(extractedDigits);
        log.info("Found contact number: {} and qrId: {} for extension: {}", contactNumber, qrId, digits);
        redisService.storeSidToContactAndQrId(callSid, contactNumber, qrId);
        return Response.ok().build();
    }

    @GET
    @PermitAll
    @Path("/connect")
    @Produces(MediaType.APPLICATION_JSON)
    public Response connect(@QueryParam("CallSid") String callSid, @QueryParam("CallFrom") String callFrom) {
        log.info("Received connect webhook from Exotel for CallSid: {}", callSid);
        String contactNumber = redisService.getContactNumberBySid(callSid);
        log.info("Found contact number: {} for CallSid: {}", contactNumber, callSid);

        //TODO: should we implement it asynchronously so that we don't fail the call flow if DB is down?
        String qrId = redisService.getQrIdBySid(callSid);
        if (qrId != null && contactNumber != null && callFrom != null) {
            CallHistory callHistory = CallHistory.builder()
                    .qrId(qrId)
                    .contactNumber(contactNumber)
                    .callFrom(callFrom)
                    .callSid(callSid)
                    .timestamp(Instant.now())
                    .build();
            callHistoryService.save(callHistory);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("fetch_after_attempt", false);

        Map<String, Object> destination = new HashMap<>();
        destination.put("numbers", List.of(contactNumber));
        response.put("destination", destination);

        response.put("record", true);
        response.put("recording_channels", "dual");
        response.put("max_ringing_duration", 45);
        response.put("max_conversation_duration", 90);

        Map<String, Object> musicOnHold = new HashMap<>();
        musicOnHold.put("type", "operator_tone");
        response.put("music_on_hold", musicOnHold);

        return Response.ok(response).build();
    }
}

