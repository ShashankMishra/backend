package com.qrust.user.api;

import com.qrust.user.api.dto.ContactPolicyDto;
import com.qrust.user.api.dto.ContactPreference;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Path("/data")
@Slf4j
public class MasterDataController {


    @GET
    @Path("/contact-policies")
    public Response contactPolicies() {
        List<ContactPreference> contactPreferences = new ArrayList<>();
        for (ContactPolicyDto policy : ContactPolicyDto.values()) {
            ContactPreference dto = new ContactPreference();
            dto.setContactPolicy(policy);
            contactPreferences.add(dto);
        }

        return Response.ok().entity(contactPreferences).build();
    }

}
