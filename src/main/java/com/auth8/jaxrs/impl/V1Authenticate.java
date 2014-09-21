package com.auth8.jaxrs.impl;

import com.auth8.error.RestError;
import com.auth8.jaxrs.Authenticate;
import com.auth8.jaxrs.Success;
import com.auth8.util.AuthRequest;
import com.auth8.util.LazySingleton;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiParamType;
import org.jsondoc.core.pojo.ApiVerb;

/**
 * The auth8 configuration request endpoints.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Api(name = "authentication services", description = "Methods for performing authentications")
@Path("v1")
public class V1Authenticate implements Authenticate {

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param number
     * @return
     */
    @ApiMethod(
            path = "/v1/authenticate/{number}",
            verb = ApiVerb.POST,
            description = "Initilizes a phone authentication for the specified user.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("authenticate/{number}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response authenticate(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "number", description = "The phone number of the user to authenticate.")
            @PathParam("number") String number) {

        try {
            LazySingleton ls = LazySingleton.getInstance();
            AuthRequest authRequest = new AuthRequest(30);
            authRequest.pauseThread();
            ls.getRequest().put("1", authRequest);
            // Pause the thread until a response from the client is obtained
            while (!authRequest.isRunning()) {
                // Do nothing
            }
            // A response has been obtained or the timeout was reached
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", authRequest.isAuthenticated());
            return Success.response(null, response, request);
            
        } catch (Exception e) {
            // Singleton Exception
            return Response
                    .status(503)
                    .entity(new RestError()
                            .build(RestError.ErrorType.SERVER_ERROR))
                    .build();
        }

    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param auth
     * @return
     */
    @ApiMethod(
            path = "/v1/wake/{auth}",
            verb = ApiVerb.GET,
            description = "Wakes thread.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("wake/{auth}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response wake(@Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "auth", description = "is the user auth?")
            @PathParam("auth") boolean auth) {

        LazySingleton ls = LazySingleton.getInstance();
        AuthRequest a = ls.getRequest().get("1");
        a.setAuthenticated(auth);
        a.resumeThread();

        return Success.response("OK", request);
    }

}
