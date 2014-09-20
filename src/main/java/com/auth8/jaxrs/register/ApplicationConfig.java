/*
 * Created Aug 15, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.auth8.jaxrs.register;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * Registers the JAXRS resources with the application. This class should add
 * all the classes that have JAXRS annotations.
 *
 * @version 1.0.0
 * @since Build 140815.163437
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@javax.ws.rs.ApplicationPath("")
public class ApplicationConfig extends Application {

    /**
     * Creates a new instance of ApplicationConfig
     */
    public ApplicationConfig ()
    {
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.auth8.doc.JSONDocController.class);
        resources.add(com.auth8.error.BadURIException.class);
        resources.add(com.auth8.jaxrs.impl.V1Settings.class);
        resources.add(com.auth8.security.AuthenticationFilter.class);
    }

}
