package com.qrust.admin.api;

import com.qrust.admin.service.OrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
