/*
 * Created Jul 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.jaxrs;

import com.auth8.misc.PS;
import static com.auth8.util.RequestLog.log;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 *
 *
 * @version 1.0.0
 * @since Build 140729.163924
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class Success {

    /**
     * Creates a success response with the specified message
     *
     * @param message
     * @param request
     * @return A response object with a success message and/or payload
     */
    public static Response response(
            String message,
            HttpServletRequest request) {
        return response(message, new HashMap<>(), request);
    }

    /**
     * Creates a success response with the specified message
     *
     * @param message
     * @param data
     * @param request
     * @return A response object with a success message and/or payload
     */
    public static Response response(
            String message,
            Map<String, Object> data,
            HttpServletRequest request) {

        // Log the request
        //log(data, request);

        return Response
                .status(200)
                .entity(data)
                .build();

    }

}
