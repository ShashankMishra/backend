package com.qrust.common.client;

import com.qrust.common.domain.ScanLocation;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "ipwhois-api")
@Singleton
public interface IpWhoIsClient {
    @GET
    @Path("/{ip}")
    ScanLocation getLocation(@PathParam("ip") String ip);
}

