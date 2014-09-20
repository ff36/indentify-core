/*
 * Created Jun 30, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.jetty;

import com.auth8.misc.PS;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty stop handler enabling shutdown of jetty via an HTTP request
 * with the system secret shutdown key.
 *
 * @version 1.0.0
 * @since Build {build} (Jun 30, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class StopHandler extends AbstractHandler {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(StopHandler.class);
    private final String shutdownKey;
    private final Server server;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param secretKey shared secret key
     * @param server jetty server instance
     */
    StopHandler(String shutdownKey, Server server) {
        this.shutdownKey = shutdownKey;
        this.server = server;
    }
//</editor-fold>

    /**
     * This handler (along with all other handlers) is the first port of call 
     * for EVERY HTTP request. If you want jersey to handle the request you 
     * must let the request through. Otherwise you can intercept it here.
     * 
     * @param string
     * @param rqst
     * @param hsr
     * @param hsr1
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void handle(
            String string, 
            Request rqst, 
            HttpServletRequest hsr,
            HttpServletResponse hsr1) throws IOException, ServletException {
        
        // get url parameters
        String sharedSecretKey = rqst.getParameter(PS.REQUEST_SHUTDOWN_KEY);

        if (sharedSecretKey != null) {
            if (sharedSecretKey.equals(this.shutdownKey)) {
                // Stop the server in separate thread.
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            server.stop();
                        } catch (Exception ex) {
                            LOG.error("FAILED TO STOP AUTH8", ex);
                        }
                    }
                }.start();
            } else {
                LOG.info("INVALID SHUTDOWN KEY");
            }
        } else {
            // Do nothing missing key means its a request to pass to jersey
        }
    }

    /**
     * The static shutdown method that triggers the HTTP call to shutdown the
     * server.
     * 
     * @param port
     * @param shutdownSecret
     * @return 
     */
    public static String shutdown(int port, String shutdownSecret) {
        try {
            String uri = "http://localhost:" + port
                    + "/shutdown/?"
                    + PS.REQUEST_SHUTDOWN_KEY 
                    + "=" 
                    + shutdownSecret;
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getContent();
            
        } catch (SocketException e) {
            return "404 SOCKET EXCEPTION DURRING SHUTDOWN";
        } catch (IOException e) {
            return null;
        }
        return "REQUESTING SHUTDOWN";
    }

}
