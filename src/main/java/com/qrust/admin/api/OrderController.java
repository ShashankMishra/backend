package com.qrust.admin.api;

import com.qrust.admin.api.dto.OrderShippingRequest;
import com.qrust.admin.service.OrderService;
import com.qrust.common.domain.user.UserAddress;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;


@Path("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class OrderController {

    @Inject
    OrderService orderService;


    @GET
    @Path("/orders")
    @RolesAllowed("admin")
    public Response getOrdersForCurrentUser() {
        return Response.ok(orderService.getRecentOrders()).build();
    }


    @GET
    @Path("/qr-sticker-orders")
    @RolesAllowed("admin")
    public Response getAllQrStickerOrders() {
        return Response.ok(orderService.getAllQrStickerOrders()).build();
    }

    @POST
    @Path("/order-shipping-details")
    @RolesAllowed("admin")
    public Response updateOrderShippingDetails(OrderShippingRequest orderShippingRequest) {
        orderService.updateOrderShippingDetails(orderShippingRequest);
        return Response.ok().build();
    }

    @GET
    @Path("/order-address-details")
    @RolesAllowed("admin")
    public Response getOrderAddressDetails(@QueryParam("userId") String userId, @QueryParam("addressId") String addressId) {
        UserAddress orderAddressDetails = orderService.getOrderAddressDetails(userId, addressId);
        return Response.ok(orderAddressDetails).build();
    }
}
