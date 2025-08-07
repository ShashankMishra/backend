package com.qrust.user.api;

import com.qrust.common.redis.RedisService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/exotel-webhook")
@Slf4j
public class ExotelWebhookController {

    @Inject
    RedisService redisService;

    @GET
    @PermitAll
    @Path("/pass-thru")
    public Response passThru(@QueryParam("CallSid") String callSid, @QueryParam("digits") String digits) {
        log.info("Received pass-thru webhook from Exotel for CallSid: {} and digits: {}", callSid, digits);
        String contactNumber = redisService.getContactNumberByExtension(digits.replace("\"", ""));
        log.info("Found contact number: {} for extension: {}", contactNumber, digits);
        redisService.storeSidToContact(callSid, contactNumber);
        return Response.ok().build();
    }

    @GET
    @PermitAll
    @Path("/connect")
    @Produces(MediaType.APPLICATION_JSON)
    public Response connect(@QueryParam("CallSid") String callSid) {
        log.info("Received connect webhook from Exotel for CallSid: {}", callSid);
        String contactNumber = redisService.getContactNumberBySid(callSid);
        log.info("Found contact number: {} for CallSid: {}", contactNumber, callSid);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("fetch_after_attempt", false);

        Map<String, Object> destination = new HashMap<>();
        destination.put("numbers", List.of(contactNumber));
        response.put("destination", destination);

        response.put("outgoing_phone_number", "+918047115777");
        response.put("record", true);
        response.put("recording_channels", "dual");
        response.put("max_ringing_duration", 45);
        response.put("max_conversation_duration", 90);

        Map<String, Object> musicOnHold = new HashMap<>();
        musicOnHold.put("type", "operator_tone");
        response.put("music_on_hold", musicOnHold);

        Map<String, Object> startCallPlayback = new HashMap<>();
        startCallPlayback.put("playback_to", "both");
        startCallPlayback.put("type", "text");
        startCallPlayback.put("value", "This text would be spoken out to the callee");
        response.put("start_call_playback", startCallPlayback);

        return Response.ok(response).build();
    }
}

