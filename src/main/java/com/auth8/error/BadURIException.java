/*
 * Created Jun 21, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * If a request is made to a non existing resource we want to intercept the
 * default response and build a new 404 error response that matches the rest of
 * the application error responses.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Provider
public class BadURIException implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {

        return Response
                .status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new RestError()
                        .build(RestError.ErrorType.RESOURCE_NOT_FOUND))
                .build();
    }

}
