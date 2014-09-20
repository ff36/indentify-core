/*
 * Created Jul 10, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.util;

import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs API requests
 *
 * @version 1.0.0
 * @since Build 140710.113033
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class RequestLog {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(RequestLog.class);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of RequestLogger
     */
    public RequestLog() {
    }
//</editor-fold>

    /**
     * Log the request
     *
     * @param response
     * @param request
     */
    public static void log(Map<String, Object> response, HttpServletRequest request) {
        // Extract the unique request key
        Map<String, Object> map = (Map<String, Object>) response.get("meta");
        UUID key = (UUID) map.get("key");

        // Reconstitude the URL
        String url, query;
        StringBuffer requestURL = request.getRequestURL();
        query = request.getQueryString();

        if (query == null) {
            url = requestURL.toString();
        } else {
            url = requestURL.append('?').append(query).toString();
        }
        LOG.info("Key: " + key.toString()
                + " | "
                + "(" + request.getRemoteAddr() + ")"
                + " | "
                + request.getMethod() + " " + url);
    }

}
