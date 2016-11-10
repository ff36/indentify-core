package com.auth8.jaxrs.impl;

import com.auth8.aws.SNSMobilePush;
import com.auth8.error.RestError;
import com.auth8.jaxrs.Authenticate;
import com.auth8.jaxrs.Success;
import com.auth8.persistent.Device;
import com.auth8.util.AuthRequest;
import com.auth8.util.LazySingleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
@Api(name = "authentication services", description = "Methods for performing authentications")
@Path("v1")
public class V1Authenticate implements Authenticate {

    private static final Logger LOG = LoggerFactory.getLogger(V1Authenticate.class);

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param email
     * @param authority
     * @param timeout
     * @param points
     * @param purpose
     * @return
     */
    @ApiMethod(
            path = "/v1/authenticate/{email}/{authority}/{timeout}/{points}/{purpose}",
            verb = ApiVerb.POST,
            description = "Initilizes a phone authentication for the specified user.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("authenticate/{email}/{authority}/{timeout}/{points}/{purpose}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response authenticate(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "email", description = "The email of the user to authenticate.")
            @PathParam("email") String email,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "authority", description = "The authority requesting the authentication.")
            @PathParam("authority") String authority,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "timeout", description = "Timeout period in seconds.")
            @PathParam("timeout") int timeout,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "points", description = "Required authentication points.")
            @PathParam("points") int points,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "purpose", description = "The purpose of authentication requested.")
            @PathParam("purpose") String purpose) {

        try {
            //Get the User Registered ID from DB
            Device device;
            try {
                ObjectContext ctx = new ServerRuntime("cayenne-project.xml").newContext();
                Expression deviceE = ExpressionFactory.likeExp(Device.EMAIL_PROPERTY, email);
                SelectQuery deviceQ = new SelectQuery(Device.class, deviceE);
                device = (Device) ctx.performQuery(deviceQ).get(0);
            } catch (IndexOutOfBoundsException e) {
                // Not a registered user
                return Response
                        .status(503)
                        .entity(new RestError()
                                .build(RestError.ErrorType.DEVICE_NOT_REGISTERED))
                        .build();
            }

            // Put the request into the singlton
            LazySingleton ls = LazySingleton.getInstance();
            AuthRequest authRequest = new AuthRequest(timeout, device);
            authRequest.pauseThread();
            String uniqueRequestId = UUID.randomUUID().toString().replace("-", "");
            ls.getRequest().put(uniqueRequestId, authRequest);
            System.out.println(uniqueRequestId);

            // Build the message to send
            // TODO build a proper message
            Map<String, String> payload = new HashMap<>();
            payload.put("timeout", String.valueOf(timeout));
            payload.put("authority", authority);
            payload.put("origin", request.getRemoteAddr());
            payload.put("points", String.valueOf(points));
            payload.put("purpose", purpose);

            // Send the request to the phone
            SNSMobilePush.sendMessage(device, uniqueRequestId, payload);

            // Pause the thread until a response from the client is obtained
            while (!authRequest.isRunning()) {
                // Do nothing
            }
            // A response has been obtained or the timeout was reached
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", authRequest.isAuthenticated());
            if (!authRequest.isTimedout()) {
                response.put("details", authRequest.getDevice().getName());
            } else {
                response.put("details", "time_out");
            }
            return Success.response(null, response, request);

        } catch (InterruptedException | IOException e) {
            // Singleton Exception
            return Response
                    .status(503)
                    .entity(new RestError()
                            .build(RestError.ErrorType.SERVER_ERROR))
                    .build();
        } catch (Exception e) {
            LOG.error("SNS Error", e);
            return null;
        }

    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param auth
     * @param requestId
     * @return
     */
    @ApiMethod(
            path = "/v1/wake/{auth}/{requestId}",
            verb = ApiVerb.POST,
            description = "Wakes thread.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("wake/{auth}/{requestId}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response wake(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "auth", description = "iIs the user auth?")
            @PathParam("auth") boolean auth,
            @ApiParam(paramType = ApiParamType.PATH, required = true, name = "requestId", description = "oOriginal request ID")
            @PathParam("requestId") String requestId) {

        LazySingleton ls = LazySingleton.getInstance();
        AuthRequest a = ls.getRequest().get(requestId);
        a.resumeThread(auth, false);

        return Success.response("OK", request);
    }

}
