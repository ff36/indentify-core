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

        Map<String, Object> meta = new HashMap<>();
        meta.put(PS.RESPONSE_KEY_KEY, UUID.randomUUID());
        if (message != null) {
            meta.put(PS.RESPONSE_MESSAGE_KEY, message);
        }

        Map<String, Object> reply = new HashMap<>();
        reply.put(PS.RESPONSE_META_KEY, meta);

        // Add the data to the response
        reply.put(PS.RESPONSE_DATA_KEY, data);

        // Log the request
        log(reply, request);

        return Response
                .status(200)
                .entity(reply)
                .build();

    }

}
