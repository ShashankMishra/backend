package com.qrust.user.api;

import com.qrust.user.api.dto.ContactDto;
import com.qrust.user.api.dto.ContactOtp;
import com.qrust.user.api.dto.SendOtpResponse;
import com.qrust.user.api.dto.userinfo.UpgradeUserInfoRequest;
import com.qrust.user.service.MessageService;
import com.qrust.user.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Path("/user")
@Slf4j
public class UserController {
    @Inject
    UserService userService;

    @Inject
    MessageService messageService;

    @GET
    @Path("/is-premium")
    @RolesAllowed("premium") // Only normal users can upgrade
    public Response isPremium(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return Response.ok().entity("User is premium : " + username).build();
    }

    @GET
    @Path("/orders")
    @Authenticated
    public Response getOrdersForCurrentUser() {
        return Response.ok(userService.getOrdersForCurrentUser()).build();
    }

    @GET
    @Path("/info")
    @Authenticated
    public Response getUserInfo() {
        return Response.ok(userService.getUserInfoResponse()).build();
    }

    @POST
    @Path("/upgrade-info")
    @Authenticated
    public Response upgradeUserInfo(@Valid UpgradeUserInfoRequest request) {
        userService.upgradeUserInfo(request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/remove-address")
    @Authenticated
    public Response removeAddress(String addressId) {
        userService.removeUserAddress(addressId);
        return Response.ok().build();
    }

    @GET
    @Path("/contacts")
    @Authenticated
    public Response getContacts() {
        var userInfo = userService.getCurrentUserInfo();
        return Response.ok(userInfo.getContacts()).build();

    }

    @POST
    @Path("/contacts/otp")
    @Authenticated
    public Response sendOtp(ContactDto contactDto) throws IOException {
        log.info("Generating OTP for contact: {}", contactDto);
        String verificationId = messageService.sendOtp(contactDto.getPhoneNumber());
        return Response.ok(new SendOtpResponse(verificationId)).build();
    }

    @POST
    @Path("/contacts/verify")
    @Authenticated
    public Response verifyOtp(@Valid ContactOtp contactOtp) throws IOException {
        log.info("Verifying OTP for contact: {} & otp {}", contactOtp.getVerificationId(), contactOtp.getOtp());
        if (messageService.validateOtp(contactOtp.getContactDto().getPhoneNumber(), contactOtp.getOtp(), contactOtp.getVerificationId())) {
            userService.addContact(contactOtp.getContactDto().getName(), contactOtp.getContactDto().getPhoneNumber());
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Otp didn't match").build();
    }
}
