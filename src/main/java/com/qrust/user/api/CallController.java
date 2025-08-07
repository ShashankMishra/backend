package com.qrust.user.api;

import com.qrust.user.api.dto.CallRequest;
import com.qrust.user.api.dto.CallResponse;
import com.qrust.user.service.CallService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/call")
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public CallResponse call(CallRequest callRequest) {
        return callService.getVirtualNumberWithExtension(callRequest);
    }
}