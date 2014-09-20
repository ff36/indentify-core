/*
 * Created Jul 10, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.misc;

/**
 * Application Constants
 *
 * @version 1.0.0
 * @since Build 140710.123729
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class PS {

    /**
     * The map key used in the REST response for the unique request key.
     * <br> Maps to type: <b>String</b>
     */
    public static final String RESPONSE_KEY_KEY = "key";
    /**
     * The map key used in the REST response for the meta data.
     * <br> Maps to type: <b>Map[String, Object]</b>
     */
    public static final String RESPONSE_META_KEY = "meta";
    /**
     * The map key used in the REST response for the payload main data.
     * <br> Maps to type: <b>Map]String, Object]</b>
     */
    public static final String RESPONSE_DATA_KEY = "data";
    /**
     * The map key used in the REST response for the meta data message.
     * <br> Maps to type: <b>String</b>
     */
    public static final String RESPONSE_MESSAGE_KEY = "message";
    /**
     * The map key used in the REST response for the TLS based settings.
     * <br> Maps to type: <b>Boolean, Map[String, Object]</b>
     */
    public static final String RESPONSE_TLS_KEY = "tls";
    /**
     * The map key used in the REST response for the TLS host.
     * <br> Maps to type: <b>String</b>
     */
    public static final String RESPONSE_HOST_KEY = "host";
    /**
     * The map key used in the REST response for user based settings.
     * <br> Maps to type: <b>List[String]></b>
     */
    public static final String RESPONSE_USER_KEY = "user";
    /**
     * The map key used in the REST response for the list of existing users.
     * <br> Maps to type: <b>List[String]></b>
     */
    public static final String RESPONSE_USER_PUBLIC_KEYS_KEY = "public_keys";
    /**
     * The map key used in the REST response for a new users public key.
     * <br> Maps to type: <b>String></b>
     */
    public static final String RESPONSE_USER_PUBLIC_KEY_KEY = "public_key";
    /**
     * The map key used in the REST response for a new users private key.
     * <br> Maps to type: <b>String</b>
     */
    public static final String RESPONSE_USER_PRIVATE_KEY_KEY = "private_key";
    /**
     * The map key used in the REST response for the server time.
     * <br> Maps to type: <b>String</b>
     */
    public static final String RESPONSE_SERVER_TIME_KEY = "time";

    /**
     * The map key used in the REST request for the secrete API shutdown key.
     * <br> Maps to type: <b>String</b>
     */
    public static final String REQUEST_SHUTDOWN_KEY = "shutdownKey";
    /**
     * The name of the properties bundle that holds the project version and
     * build.
     */
    public static final String VERSION_BUNDLE = "dev";
    /**
     * The key name in the properties file of the current project version.
     * <br> Maps to type: <b>String</b>
     */
    public static final String BUNDLE_VERSION_KEY = "MAVEN_PROJECT_VERSION";
    /**
     * The key name in the properties file of the current project build.
     * <br> Maps to type: <b>String</b>
     */
    public static final String BUNDLE_BUILD_KEY = "MAVEN_PROJECT_BUILD";
    /**
     * The map key in the settings table that holds the shutdown key.
     * <br> Maps to type: <b>String</b>
     */
    public static final String SETTINGS_SHUTDOWN_KEY = "shutdownKey";
    /**
     * The map key in the settings table that holds the TLS settings.
     * <br> Maps to type: <b>Boolean</b>
     */
    public static final String SETTINGS_TLS_KEY = "tls";
    /**
     * The map key in the settings table that holds the TLS host address.
     * <br> Maps to type: <b>String</b>
     */
    public static final String SETTINGS_HOST_KEY = "host";
    /**
     * The map key in the settings table that holds the current version.
     * <br> Maps to type: <b>String</b>
     */
    public static final String SETTINGS_VERSION_KEY = "version";
    /**
     * The map key in the settings table that holds the current build.
     * <br> Maps to type: <b>String</b>
     */
    public static final String SETTINGS_BUILD_KEY = "build";
    /**
     * The default API username on startup.
     * <br> Maps to type: <b>String</b>
     */
    public static final String DEFAULT_USER_PUBLIC_KEY = "changeit";
    /**
     * The default API password on startup.
     * <br> Maps to type: <b>String</b>
     */
    public static final String DEFAULT_USER_PRIVATE_KEY = "changeit";
    /**
     * The relative local path to the logging directory.
     */
    public static final String LOGS_PATH = "./logs";
    /**
     * The relative local path to the h2 directory.
     */
    public static final String H2_PATH = "./h2";
    
    /**
     * Usage instructions as displayed during command line deployment.
     */
    public static final String USAGE_INSTRUCTIONS
            = "\t******************************************************************\n\t"
            + "*                           AUTH8 USAGE                         *\n\t"
            + "*****************************************************************\n\n\t"
            + "nohup java -D<flag> -jar <file.war> [start|stop|restart|status] &\n\t"
            + "*****************************************************************\n\t"
            + "start      Start Auth8 (default)\n\t"
            + "stop       Stop Auth8\n\t"
            + "restart    Restart Auth8 (default to start if not running)\n\t"
            + "status     Current Auth8 status\n\n\t"
            + "nohup...&  Required to prevent hangup when exiting terminal\n\t";

}
