package com.qrust.exceptions.mapper;

import com.qrust.exceptions.LimitReachedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LimitReachedExceptionMapper implements ExceptionMapper<LimitReachedException> {
    @Override
    public Response toResponse(LimitReachedException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
    }
}
