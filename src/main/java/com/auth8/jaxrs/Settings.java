/*
 * Created Jul 18, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.jaxrs;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * Settings endpoint interface. Endpoints are defined as interfaces for
 * consistency. This prevents accidentally changing an endpoint within a given
 * version of the API.
 *
 * @version 1.0.0
 * @since Build 140718.095603
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface Settings {

    /**
     * Retrieve the server time. All request <b>EXCEPT</b> this one require a 
     * valid signature and timestamp. The timestamp plays a critical part in
     * the signature process and must be within a defined precision range. Due
     * to server drift and/or NTP server configurations it is sometimes difficult
     * to synchronize timestamps between the requesting application and the 
     * server. As such it is possible to use this endpoint <b>without</b> a 
     * signature to obtain the current server time.
     *
     * @param request
     * @return A <code>Long</code> expressing the servers current time as a Unix
     * epoch in milliseconds. (Number of milliseconds that have elapsed since 
     * 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970, 
     * not counting leap seconds)
     * @statuscode 200 OK
     */
    public Response getServerTime(@Context HttpServletRequest request);
    
    /**
     * Method handling HTTP GET requests. Retrieves Auth8 users.
     *
     * @param request
     * @return An HTTP Response object containing the response code, header and
     * the response payload.
     */
    public Response getUsers(@Context HttpServletRequest request);

    /**
     * Method handling HTTP POST requests. Creates a new user.
     * 
     * @param request
     * @return
     */
    public Response createUser(@Context HttpServletRequest request);

    /**
     * Method handling HTTP DELETE requests. Deletes specified users.
     * 
     * @param request
     * @param publicKeys
     * @return 
     */
    public Response deleteUser(
            @Context HttpServletRequest request,
            @QueryParam("public_key") List<String> publicKeys);

    
}
