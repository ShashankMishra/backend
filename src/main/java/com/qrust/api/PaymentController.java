package com.qrust.api;

import com.cashfree.pg.model.OrderEntity;
import com.qrust.api.dto.CreateOrderRequest;
import com.qrust.service.PaymentService;
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
    PaymentService paymentService;

    @Inject
    @ConfigProperty(name="cashfree.client-id",defaultValue = "dummy-client-id")
    String cashfreeClientId;

    @POST
    @Path("/create-order")
    public Response createOrder(CreateOrderRequest requestDto) {
        log.info("Create a order: {}", requestDto);
        log.info("Cashfree client id: {}", cashfreeClientId);
        OrderEntity orderEntity = paymentService.createOrder(requestDto.getPlanType());
        return Response.ok(orderEntity).build();
    }

    @GET
    @Path("/fetch-order/{orderId}")
    public Response fetchOrder(@PathParam("orderId") String orderId) {
        OrderEntity orderEntity = paymentService.fetchOrder(orderId);
        return Response.ok(orderEntity).build();
    }

    @POST
    @Path("/webhook/{userId}")
    @PermitAll
    public Response handleWebhook(@Context HttpHeaders headers, String body, @PathParam("userId") String userId) {
        String signature = headers.getHeaderString("x-webhook-signature");
        String timestamp = headers.getHeaderString("x-webhook-timestamp");
        //TODO: verify cashfree webhook signature
        paymentService.handleWebhook(body, userId);
        return Response.ok().build();
    }
}
