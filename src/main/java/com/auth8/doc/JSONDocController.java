/*
 * Created Sep 10, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.doc;

import com.auth8.misc.PS;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.util.JSONDocUtils;

/**
 * {description}
 *
 * @version {project.version}
 * @since Build 140910.103432
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Path("jsondoc")
public class JSONDocController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONDoc getApi(@Context HttpServletRequest request) {

        ResourceBundle current = ResourceBundle.getBundle(PS.VERSION_BUNDLE);
        String url = request.getRequestURL().toString();
        
        List<String> packages = new ArrayList<>();
        packages.add("com.auth8.jaxrs.impl");
        packages.add("com.auth8.pojo");
        return JSONDocUtils.getApiDoc(
                current.getString(PS.BUNDLE_VERSION_KEY)
                + "-"
                + current.getString(PS.BUNDLE_BUILD_KEY),
                url.substring(0, url.length() - 8),
                packages);
    }

}
