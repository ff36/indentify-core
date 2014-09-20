/*
 * Created Jun 21, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.security;

import com.auth8.error.RestError;
import java.io.IOException;
import java.io.NotActiveException;
import java.net.URLEncoder;
import java.util.Calendar;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An request filter that intercepts every request to determine whether the
 * supplied authorization credentials are valid.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(
            AuthenticationFilter.class);
//</editor-fold>

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        /*
         The `$/time` endpoint provides the current server time and should be 
         accessed without a signature.
         */
        if (requestContext.getUriInfo().getPath().equals("/v1/$/time")) {
            // Do nothing as we want this endpoint to be accessed without signature
        } else if (requestContext.getUriInfo().getPath().equals("/jsondoc")
                && requestContext.getUriInfo().getQueryParameters(true).isEmpty()) {
            // Do nothing as we want this endpoint to be accessed without signature
        } else {
            // Perform signature validation
            try {

                MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters(true);
                String timestamp = queryParameters.getFirst("timestamp");
                String publicKey = queryParameters.getFirst("public_key");
                String signature = queryParameters.getFirst("signature");

                // Check that all the attributes are present
                if (timestamp != null && publicKey != null && signature != null) {

                    // Check the timestamp 5 mins each side 
                    long now = Calendar.getInstance().getTimeInMillis();
                    if (Long.parseLong(timestamp) < now - 300000 || Long.parseLong(timestamp) > now + 300000) {
                        // Timestamp expired
                        throw new NotActiveException();
                    }

                    String requestURI = requestContext.getUriInfo().getRequestUri().toString();
                    requestURI = requestURI.replace("&signature=" + URLEncoder.encode(signature, "UTF-8"), "");

                    // Sign the URL string with private key (obtained from public key)
                    String signed = Signature.signUrl(requestURI, publicKey);

                    // Check the two signatures match
                    if (!signature.trim().equals(signed.trim())) {
                        // Wrong signiture
                        throw new NullPointerException();
                    }

                } else {
                    // Missing query param
                    throw new NullPointerException();
                }

            } catch (NullPointerException | IndexOutOfBoundsException e) {
                // Invalid credentials
                requestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(new RestError()
                                .build(RestError.ErrorType.INVALID_CREDENTIALS))
                        .build());
            } catch (NotActiveException e) {
                // Timestamp older than 60 secs
                requestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(new RestError()
                                .build(RestError.ErrorType.EXPIRED_TIMESTAMP))
                        .build());
            }

        }
    }

}
