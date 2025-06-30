package com.qrust.api;

import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.qrust.api.dto.CreateOrderRequest;
import com.qrust.service.impl.PhonepePaymentService;
import io.quarkus.logging.Log;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/payment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PaymentController {

    @Inject
    PhonepePaymentService phonepePaymentService;

    @Inject
    @ConfigProperty(name = "cashfree.client-id", defaultValue = "dummy-client-id")
    String cashfreeClientId;

    @POST
    @Path("/create-order")
    public Response createOrder(CreateOrderRequest requestDto) {
        StandardCheckoutPayResponse checkoutPayResponse = phonepePaymentService.createOrder(requestDto.getPlanType());
        return Response.ok(checkoutPayResponse).build();
    }

    @GET
    @Path("/fetch-order-status/{orderId}")
    public Response fetchOrder(@PathParam("orderId") String orderId) {
        OrderStatusResponse orderStatusResponse = phonepePaymentService.fetchOrderStatus(orderId);
        return Response.ok(orderStatusResponse).build();
    }

    @POST
    @Path("/phonepe-webhook")
    @PermitAll
    public Response handleWebhook(@Context HttpHeaders headers, String body) {
        String receivedAuth = headers.getHeaderString("Authorization");

        if (receivedAuth == null || receivedAuth.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing Authorization header").build();
        }
        phonepePaymentService.handleWebhook(receivedAuth, body);
        return Response.ok().build();
    }

    @POST
    @Path("/phonepe-webhook-uat")
    @PermitAll
    public Response handleWebhookUAT(@Context HttpHeaders headers, String body) {
        Log.infof("Received PhonePe webhook in UAT environment with body: %s", body);
        String receivedAuth = headers.getHeaderString("Authorization");

        if (receivedAuth == null || receivedAuth.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing Authorization header").build();
        }
        phonepePaymentService.handleWebhook(receivedAuth, body);
        return Response.ok().build();
    }

//    @POST
//    @Path("/create-order")
//    public Response createOrder(CreateOrderRequest requestDto) {
//        log.info("Create a order: {}", requestDto);
//        log.info("Cashfree client id: {}", cashfreeClientId);
//        OrderEntity orderEntity = paymentService.createOrder(requestDto.getPlanType());
//        return Response.ok(orderEntity).build();
//    }
//
//    @GET
//    @Path("/fetch-order/{orderId}")
//    public Response fetchOrder(@PathParam("orderId") String orderId) {
//        OrderEntity orderEntity = paymentService.fetchOrder(orderId);
//        return Response.ok(orderEntity).build();
//    }
//
//    @POST
//    @Path("/webhook/{userId}")
//    @PermitAll
//    public Response handleWebhook(@Context HttpHeaders headers, String body, @PathParam("userId") String userId) {
//        String signature = headers.getHeaderString("x-webhook-signature");
//        String timestamp = headers.getHeaderString("x-webhook-timestamp");
//        //TODO: verify cashfree webhook signature
//        cashfreePaymentService.handleWebhook(body, userId);
//        return Response.ok().build();
//    }
}
