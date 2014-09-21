package com.auth8.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Authenticate endpoint interface. Endpoints are defined as interfaces for
 * consistency. This prevents accidentally changing an endpoint within a given
 * version of the API.
 *
 * @version 1.0.0
 * @since Build 140718.095603
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface Authenticate {
    
    /**
     * Method handling HTTP POST requests. Performs an authentication.
     *
     * @param request
     * @param number
     * @return An HTTP Response object containing the response code, header and
     * the response payload.
     */
    public Response authenticate(
            @Context HttpServletRequest request, 
            String number);
    
}
