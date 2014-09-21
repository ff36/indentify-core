/*
 * Created Jun 21, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.jaxrs.impl;

import com.auth8.jaxrs.Settings;
import com.auth8.jaxrs.Success;
import com.auth8.misc.PS;
import com.auth8.persistent.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiParamType;
import org.jsondoc.core.pojo.ApiVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The auth8 configuration request endpoints.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Api(name = "setting services", description = "Methods for managing system setting")
@Path("v1/$")
public class V1Settings implements Settings {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(V1Settings.class);
    private static final ObjectContext context = new ServerRuntime("cayenne-project.xml").newContext();
//</editor-fold>

    /**
     * See Settings.class interface.
     *
     * @param request
     * @return
     */
    @ApiMethod(
            path = "/v1/$/time",
            verb = ApiVerb.GET,
            description = "Gets the system time as a unix timestamp. The number of seconds that have elapsed since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970, not counting leap seconds.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error")
    })
    @GET
    @Path("time")
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response getServerTime(@Context HttpServletRequest request) {

        // Add the current server time.
        Map<String, Object> data = new HashMap<>();
        data.put(PS.RESPONSE_SERVER_TIME_KEY, Calendar.getInstance().getTimeInMillis());

        return Success.response(null, data, request);
    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @return
     */
    @ApiMethod(
            path = "/v1/$/users",
            verb = ApiVerb.GET,
            description = "Gets a list of public keys in service.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("users")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response getUsers(@Context HttpServletRequest request) {

        // Add the users data to the var map and then to the data
        SelectQuery usersQ = new SelectQuery(User.class);
        List<User> users = context.performQuery(usersQ);

        List<String> publicKeys = new ArrayList<>();
        for (User user : users) {
            publicKeys.add(user.getPublicKey());
        }
        Map<String, Object> data = new HashMap<>();
        data.put(PS.RESPONSE_USER_PUBLIC_KEYS_KEY, publicKeys);

        return Success.response(null, data, request);
    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @return
     */
    @ApiMethod(
            path = "/v1/$/users",
            verb = ApiVerb.POST,
            description = "Creates a new keypair.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("users")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response createUser(@Context HttpServletRequest request) {

        // Create the user 
        User user = context.newObject(User.class);
        user.setPublicKey(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setPrivateKey(UUID.randomUUID().toString());
        context.commitChanges();

        // Add the data to the response
        Map<String, Object> data = new HashMap<>();
        data.put(PS.RESPONSE_USER_PUBLIC_KEY_KEY, user.getPublicKey());
        data.put(PS.RESPONSE_USER_PRIVATE_KEY_KEY, user.getPrivateKey());

        return Success.response(null, data, request);

    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param publicKeys
     * @return
     */
    @ApiMethod(
            path = "/v1/$/users",
            verb = ApiVerb.DELETE,
            description = "Retire a keypair from active service.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("users")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response deleteUser(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.QUERY, required = true, name = "key", description = "The public key of the keypair to be removed from service.")
            @QueryParam("key") List<String> publicKeys) {

        // Get the user 
        for (String publicKey : publicKeys) {

            Expression userE = ExpressionFactory.likeExp(User.PUBLIC_KEY.getName(), publicKey);
            SelectQuery userQ = new SelectQuery(User.class, userE);
            User user = (User) context.performQuery(userQ).get(0);

            if (user != null) {
                context.deleteObjects(user);
                context.commitChanges();
            }
        }

        return Success.response("The specified keys have been "
                + "removed from service.", null, request);

    }

    
}
