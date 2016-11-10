/*
 * Created Jun 21, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.error;

import java.util.HashMap;
import java.util.Map;

/**
 * In order to have fine grained control over error responses we want to be able
 * to construct a standard response that contains all the required information
 * about the error.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class RestError {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private Map<String, String> error;
    private int code;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of ErrorBuilder
     */
    public RestError() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of errors. A map containing the following parameters:
     * reason, message, location_type and location.
     *
     * @return the value of error
     */
    public Map<String, String> getError() {
        return error;
    }

    /**
     * Get the value of code. The error response code
     *
     * @return the value of code
     */
    public int getCode() {
        return code;
    }
//</editor-fold>

    /**
     * Response error types. If an error is encountered a predetermined error
     * response can be constructed with the known error type.
     */
    public static enum ErrorType {

        RESOURCE_NOT_FOUND,
        INVALID_CREDENTIALS,
        EXPIRED_TIMESTAMP,
        UNKNOWN_ERROR,
        DEVICE_NOT_REGISTERED,
        SERVER_ERROR;
    }

    /**
     * Construct the error message body.
     *
     * @param error
     * @return A JSON message body representation of the error.
     */
    public RestError build(ErrorType error) {
        this.error = new HashMap<>();

        switch (error) {
            case INVALID_CREDENTIALS:
                this.code = 401;
                this.error.put("reason", "invalid_credentials");
                this.error.put("message", "The supplied credentials are invalid.");
                this.error.put("location_type", "Request headers");
                this.error.put("location", "Authorization");
                break;
            case EXPIRED_TIMESTAMP:
                this.code = 401;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "The signature timestamp has expired. "
                        + "Timestamps are in UTC and are valid for 60 secounds.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "timestamp");
                break;
            case RESOURCE_NOT_FOUND:
                this.code = 404;
                this.error.put("reason", "resource_not_found");
                this.error.put("message", "The requested resource could not "
                        + "be found.");
                this.error.put("location_type", "URI");
                this.error.put("location", "URI");
                break;
            case UNKNOWN_ERROR:
                this.code = 503;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "OOPS we missed this one! If you see "
                        + "this message please contact SOLiD support with the "
                        + "query you tried to execute.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "unknown");
                break;
            case SERVER_ERROR:
                this.code = 503;
                this.error.put("reason", "server_error");
                this.error.put("message", "Internal server error.");
                this.error.put("location_type", "internal");
                this.error.put("location", "server");
                break;
            case DEVICE_NOT_REGISTERED:
                this.code = 503;
                this.error.put("reason", "device_not_registered");
                this.error.put("message", "No device is registered for this email.");
                this.error.put("location_type", "internal");
                this.error.put("location", "server");
                break;

        }
        return this;
    }

}
