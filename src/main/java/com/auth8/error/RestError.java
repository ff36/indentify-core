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
        INVALID_RANGE,
        INVALID_PAST_INTEGER,
        INVALID_LIMIT_INTEGER,
        INVALID_OFFSET_INTEGER,
        INVALID_STATE,
        INVALID_MASK,
        UNKNOWN_ERROR,
        SQL_ERROR,
        JSON_ERROR,
        UPGRADE_CONNECT_ERROR,
        UPGRADE_WRITE_ERROR,
        NO_UPGRADE_AVAILABLE,
        INVALID_BACKUP_KEY,
        CREATE_BACKUP_FAILED,
        DELETE_BACKUP_FAILED,
        MISSING_BACKUP_FILE,
        MISSING_TLS_HOST,
        INVALID_DMS,
        INVALID_SEVERITY,
        INVALID_HYSTERESIS,
        EXPIRED_TIMESTAMP,
        HASHING_ERROR,
        CURRUPTED_UPGRADE_PACKAGE;
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
            case RESOURCE_NOT_FOUND:
                this.code = 404;
                this.error.put("reason", "resource_not_found");
                this.error.put("message", "The requested resource could not "
                        + "be found.");
                this.error.put("location_type", "URI");
                this.error.put("location", "URI");
                break;
            case SQL_ERROR:
                this.code = 503;
                this.error.put("reason", "database_error");
                this.error.put("message", "The server experienced an exception "
                        + "whilst trying to read the database. If this error "
                        + "persists contact SOLiD support.");
                this.error.put("location_type", "SQL");
                this.error.put("location", "Database");
                break;
            case JSON_ERROR:
                this.code = 503;
                this.error.put("reason", "json_serialize_error");
                this.error.put("message", "The server encountered a problem "
                        + "whilst serializing the JSON response. If this error "
                        + "persists contact support.");
                this.error.put("location_type", "Serializer");
                this.error.put("location", "JSON");
                break;
            case INVALID_RANGE:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid range. It should be a "
                        + "string range in the format [MM-dd-yyyy_HH-mm-ss,"
                        + "MM-dd-yyyy_HH-mm-ss].");
                this.error.put("location_type", "parameter");
                this.error.put("location", "start");
                break;
            case INVALID_PAST_INTEGER:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for past. It should "
                        + "be an integer (representing seconds) between 0 and "
                        + "2,147,483,647.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "past");
                break;
            case INVALID_LIMIT_INTEGER:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for limit. It should "
                        + "be an integer between 0 and 2,147,483,647. "
                        + "The default in 500.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "limit");
                break;
            case INVALID_OFFSET_INTEGER:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for offset. It should "
                        + "be an integer between 0 and 2,147,483,647.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "offset");
                break;
            case INVALID_SEVERITY:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for severity. It should "
                        + "be an integer between 0 and 2,147,483,647. The value"
                        + " can optionally be prefixed with '>' or '<' to "
                        + "denote <greater than> or <smaller than> respectively.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "severity");
                break;
            case INVALID_HYSTERESIS:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for hysteresis. It should "
                        + "be an integer between 0 and 2,147,483,647. The value "
                        + "can optionally be prefixed with '>' or '<' to denote "
                        + "<greater than> or <smaller than> respectively.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "hysteresis");
                break;
            case INVALID_STATE:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for state. It should "
                        + "be a string value of either 'on' or 'off'.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "state");
                break;
            case INVALID_MASK:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "Invalid value for mask. It should "
                        + "be a string value of either 'on' or 'off'.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "mask");
                break;
            case UNKNOWN_ERROR:
                this.code = 400;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "OOPS we missed this one! If you see "
                        + "this message please contact SOLiD support with the "
                        + "query you tried to execute.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "unknown");
                break;
            case UPGRADE_CONNECT_ERROR:
                this.code = 503;
                this.error.put("reason", "connect_upgrade_server");
                this.error.put("message", "The server was unable to contact "
                        + "the SOLiD central upgrade repository. Make sure your "
                        + "firewall allows external requests to "
                        + "https://s3.amazonaws.com/com.solid.phantom/ on port "
                        + "446");
                this.error.put("location_type", "connection");
                this.error.put("location", "URI");
                break;
            case NO_UPGRADE_AVAILABLE:
                this.code = 400;
                this.error.put("reason", "no_upgrade_available");
                this.error.put("message", "You are on the latest version. "
                        + "Instead of requesting an upgrade without knowing if "
                        + "one is available you should us a GET request to "
                        + "obtain upgrade information.");
                this.error.put("location_type", "remote_server");
                this.error.put("location", "upgrade");
                break;
            case INVALID_DMS:
                this.code = 500;
                this.error.put("reason", "invalid_dms_implementation");
                this.error.put("message", "Could not find a persistence "
                        + "implentation for the set version of the DMS. "
                        + "Please contact SOLiD support.");
                this.error.put("location_type", "internal");
                this.error.put("location", "dms_service_implementation");
                break;
            case INVALID_BACKUP_KEY:
                this.code = 500;
                this.error.put("reason", "invalid_backup_key");
                this.error.put("message", "Could not find a backup that matched "
                        + "the specified key.");
                this.error.put("location_type", "payload");
                this.error.put("location", "backup_key");
                break;
            case CREATE_BACKUP_FAILED:
                this.code = 503;
                this.error.put("reason", "backup_file");
                this.error.put("message", "There was a problem creating your "
                        + "backup file. If this problem persists "
                        + "please contact SOLiD support.");
                this.error.put("location_type", "system");
                this.error.put("location", "backup");
                break;
            case DELETE_BACKUP_FAILED:
                this.code = 503;
                this.error.put("reason", "backup_file");
                this.error.put("message", "There was a problem deleting your "
                        + "backup file. If this problem persists "
                        + "please contact SOLiD support.");
                this.error.put("location_type", "system");
                this.error.put("location", "backup");
                break;
            case MISSING_BACKUP_FILE:
                this.code = 401;
                this.error.put("reason", "invalid_backup_key");
                this.error.put("message", "The backup key is invalid.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "backup_key");
                break;
            case HASHING_ERROR:
                this.code = 503;
                this.error.put("reason", "security");
                this.error.put("message", "There was an internal security problem."
                        + " Please contact SOLiD support.");
                this.error.put("location_type", "security");
                this.error.put("location", "security");
                break;
            case MISSING_TLS_HOST:
                this.code = 401;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "The host key is missing or invalid.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "host");
                break;
            case EXPIRED_TIMESTAMP:
                this.code = 401;
                this.error.put("reason", "invalid_parameter");
                this.error.put("message", "The signature timestamp has expired. "
                        + "Timestamps are in UTC and are valid for 60 secounds.");
                this.error.put("location_type", "parameter");
                this.error.put("location", "timestamp");
                break;
            case CURRUPTED_UPGRADE_PACKAGE:
                this.code = 503;
                this.error.put("reason", "currupted_package");
                this.error.put("message", "The upgrade package is currupted. Please download a new copy directly from the SOLiD repository.");
                this.error.put("location_type", "multipart_form");
                this.error.put("location", "upgrade_package");
                break;
            case UPGRADE_WRITE_ERROR:
                this.code = 503;
                this.error.put("reason", "security");
                this.error.put("message", "There was an internal security problem."
                        + " Please contact SOLiD support.");
                this.error.put("location_type", "security");
                this.error.put("location", "security");
                break;
        }
        return this;
    }

}
