package com.qrust.user.exceptions.mapper;

import com.qrust.user.exceptions.CustomUIException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomUIExceptionMapper implements ExceptionMapper<CustomUIException> {
    @Override
    public Response toResponse(CustomUIException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
    }
}
