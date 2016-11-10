package com.auth8.jaxrs.impl;

import com.auth8.jaxrs.RegDevice;
import com.auth8.jaxrs.Success;
import com.auth8.persistent.Device;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * The auth8 configuration request endpoints.
 *
 * @version 1.0-beta
 * @since Build 1.0-alpha (Jun 21, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Api(name = "register services", description = "Methods for registering a user device.")
@Path("v1")
public class V1RegDevice implements RegDevice {

    /**
     * See Settings.class interface.
     *
     * @param request
     * @return
     */
    @ApiMethod(
            path = "/v1/device",
            verb = ApiVerb.POST,
            description = "Registers a user device with the GCM id.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("device")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response regNewDevice(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.QUERY, required = true, name = "email", description = "The email of the user.")
            @QueryParam("email") String email,
            @ApiParam(paramType = ApiParamType.QUERY, required = true, name = "regid", description = "The GCM device user ID.")
            @QueryParam("regid") String regId,
            @ApiParam(paramType = ApiParamType.QUERY, required = true, name = "name", description = "The device name.")
            @QueryParam("name") String name) {

        ObjectContext ctx = new ServerRuntime("cayenne-project.xml").newContext();

        Expression deviceE = ExpressionFactory.likeExp(Device.EMAIL_PROPERTY, email);
        SelectQuery deviceQ = new SelectQuery(Device.class, deviceE);
        Device device;

        try {
            device = (Device) ctx.performQuery(deviceQ).get(0);

            // Update the existing gcm code
            device.setRegid(regId);
            device.setName(name);
            ctx.commitChanges();
        } catch (IndexOutOfBoundsException e) {
            // Persist a new email and device
            Device d = ctx.newObject(Device.class);
            d.setEmail(email);
            d.setRegid(regId);
            d.setName(name);
            ctx.commitChanges();
        } catch (Exception e) {
            System.out.println("General exception.");
        }

        // Add the data to the response
        return Success.response("Registered", request);
    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @return
     */
    @ApiMethod(
            path = "/v1/device",
            verb = ApiVerb.GET,
            description = "Gets user devices.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("device")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response getDevices(
            @Context HttpServletRequest request) {

        ObjectContext ctx = new ServerRuntime("cayenne-project.xml").newContext();

        // Add the users data to the var map and then to the data
        SelectQuery deviceQ = new SelectQuery(Device.class);
        List<Device> devices = ctx.performQuery(deviceQ);

        for (Device device : devices) {
            Map<String, Object> storedDevices = new HashMap<>();
            storedDevices.put("email", device.getEmail());
            storedDevices.put("regid", device.getRegid());
            storedDevices.put("name", device.getName());
            
        }

        return Success.response(null, storedDevices, request);
    }

    /**
     * See Settings.class interface.
     *
     * @param request
     * @param regid
     * @return
     */
    @ApiMethod(
            path = "/v1/device",
            verb = ApiVerb.DELETE,
            description = "Retire a device from active service.",
            produces = {MediaType.APPLICATION_JSON}
    )
    @ApiErrors(apierrors = {
        @ApiError(code = "503", description = "Internal server error"),
        @ApiError(code = "400", description = "Illegal argument"),
        @ApiError(code = "401", description = "Not authorized")
    })
    @Path("device")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response deleteDevice(
            @Context HttpServletRequest request,
            @ApiParam(paramType = ApiParamType.QUERY, required = true, name = "regid", description = "The device reg id.")
            @QueryParam("regid") String regid) {

        ObjectContext ctx = new ServerRuntime("cayenne-project.xml").newContext();
        // Get the user 
        Expression deviceE = ExpressionFactory.likeExp(Device.REGID_PROPERTY, regid);
        SelectQuery deviceQ = new SelectQuery(Device.class, deviceE);
        Device device = (Device) ctx.performQuery(deviceQ).get(0);

        if (device != null) {
            ctx.deleteObjects(device);
            ctx.commitChanges();
        }

        return Success.response("Deleted", null, request);

    }

}
