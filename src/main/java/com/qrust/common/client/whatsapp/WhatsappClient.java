package com.qrust.common.client.whatsapp;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.HeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "whatsapp-api")
public interface WhatsappClient {

    @POST
    @Path("/{phoneNumberId}/messages")
    void sendMessage(@HeaderParam("Authorization") String token, @PathParam("phoneNumberId") String phoneNumberId, WhatsappRequest request);
}
