package com.auth8.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * RegDevice endpoint interface. Endpoints are defined as interfaces for
 * consistency. This prevents accidentally changing an endpoint within a given
 * version of the API.
 *
 * @version 1.0.0
 * @since Build 140718.095603
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface RegDevice {
    
    /**
     * Method handling HTTP POST requests. Performs an authentication.
     *
     * @param request
     * @param email
     * @param regId
     * @param name
     * @return An HTTP Response object containing the response code, header and
     * the response payload.
     */
    public Response regNewDevice(
            @Context HttpServletRequest request, 
            String email,
            String regId,
            String name);
    
    /**
     * Method handling HTTP POST requests. Performs an authentication.
     *
     * @param request
     * @return An HTTP Response object containing the response code, header and
     * the response payload.
     */
    public Response getDevices(
            @Context HttpServletRequest request);
    
    /**
     * Method handling HTTP DELETE requests. Performs an authentication.
     *
     * @param request
     * @param regid
     * @return An HTTP Response object containing the response code, header and
     * the response payload.
     */
    public Response deleteDevice(
            @Context HttpServletRequest request,
            String regid);
}
