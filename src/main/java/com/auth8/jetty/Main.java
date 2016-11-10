/*
 * Created Jun 23, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.jetty;

import com.auth8.misc.PS;
import com.auth8.persistent.Setting;
import com.auth8.persistent.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point into the application. This is where the embedded jetty
 * server is initialized making the API accessible.
 *
 * @version 1.0-SNAPSHOT
 * @since Build 1.0-SNAPSHOT (Jun 23, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class Main {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String keystorePassword = "11aaBB**";
    private final int httpPort = 8080;
    private final int httpsPort = 8443;
    private final String shutdownKey;
    private final ObjectContext context = new ServerRuntime("cayenne-project.xml").newContext();
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * The applications main initialization class.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Main sc = new Main();

        try {
            switch (args[0]) {
                case "start":
                    sc.start();
                    break;
                case "stop":
                    sc.stop();
                    break;
                case "status":
                    Main.status(true);
                    break;
                case "restart":
                    sc.restart();
                    break;
                default:
                    sc.usage();
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // No aruments where supplied
            sc.start();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * The main class constructor setting up the default values if none are
     * specified via the command line.
     *
     * @throws java.io.IOException
     */
    public Main() throws IOException {
        // Initialize the persistence layer
        Map<String, String> keys = init();
        shutdownKey = keys.get(PS.SETTINGS_SHUTDOWN_KEY);
    }
//</editor-fold>

    /**
     * The main start method that initializes the application. Jetty is launched
     * and all the server configurations are handled here.
     */
    private void start() {

        // Check if the server is running
        if (status(false)) {
            // Server is running so just inform the user
            System.out.println("AUTH8 IS ALREADY ONLINE");
        } else {
            // Server is off so we can start it
            // Setup Threadpool
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setMaxThreads(500);

            // Setup Jetty Server instance
            Server server = new Server(threadPool);

            // Extra options
            server.setDumpAfterStart(false);
            server.setDumpBeforeStop(false);
            server.setStopAtShutdown(true);
            server.setStopTimeout(5000);

            // HTTP Connector
            ServerConnector http = new ServerConnector(server);
            http.setPort(httpPort);

            server.setConnectors(new Connector[]{http});

            // Read the settings to see if we need TLS
            try {
                Expression tlsE = ExpressionFactory.likeExp(Setting.NAME_PROPERTY, PS.SETTINGS_TLS_KEY);
                SelectQuery tlsQ = new SelectQuery(Setting.class, tlsE);
                Setting tls = (Setting) context.performQuery(tlsQ).get(0);

                if (Boolean.parseBoolean(tls.getValue())) {
                    // Get the host
                    Expression hostE = ExpressionFactory.likeExp(Setting.NAME_PROPERTY, PS.SETTINGS_HOST_KEY);
                    SelectQuery hostQ = new SelectQuery(Setting.class, hostE);
                    Setting host = (Setting) context.performQuery(hostQ).get(0);

                    server = configureTLS(
                            server,
                            httpsPort,
                            host.getValue());
                }
            } catch (Exception ex) {
                LOG.error("ERROR ACCESSING TLS SETTINGS", ex);
                System.exit(-1);
            }

            // Prevent jetty from sending server information
            for (Connector connector : server.getConnectors()) {
                for (ConnectionFactory cf : connector.getConnectionFactories()) {
                    if (cf instanceof HttpConnectionFactory) {
                        ((HttpConnectionFactory) cf)
                                .getHttpConfiguration()
                                .setSendServerVersion(false);
                    }
                }
            }

            // Get the WAR project file
            ProtectionDomain protectionDomain
                    = Main.class.getProtectionDomain();
            String warFile = protectionDomain
                    .getCodeSource()
                    .getLocation()
                    .toExternalForm();

            // Add the WAR project file
            WebAppContext webapp = new WebAppContext(warFile, "/");
            webapp.setServer(server);

            // Reset the temporary folder to unpack the JAR into.
            String currentDir = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();
            resetTempDirectory(webapp, currentDir);

//            // This webapp will use jsps and jstl. We need to enable the AnnotationConfiguration in order to correctly 
//            // set up the jsp container
//            org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
//            classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
//
//            // Set the ContainerIncludeJarPattern so that jetty examines these container-path jars for tlds, web-fragments etc. 
//            // If you omit the jar that contains the jstl .tlds, the jsp engine will scan for them instead. 
//            webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
//                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");

            // Add handlers for server
            HandlerCollection handlerCollection = new HandlerCollection();
            Handler[] handlers = {webapp, new StopHandler(shutdownKey, server)};
            handlerCollection.setHandlers(handlers);
            server.setHandler(handlerCollection);

            // Create logging directory
            new File(PS.LOGS_PATH).mkdir();

            // Create H2 directory
            new File(PS.H2_PATH).mkdir();

            // Start the server
            try {
                // Get the current version properties
                ResourceBundle current = ResourceBundle.getBundle(
                        PS.VERSION_BUNDLE);
                String v, b;
                v = current.getString(PS.BUNDLE_VERSION_KEY);
                b = current.getString(PS.BUNDLE_BUILD_KEY);
                System.out.println("*******************************************************");
                System.out.println("*              AUTH8 " + v + "-" + b + "              *");
                System.out.println("*******************************************************");
                System.out.println("INITIALIZING...");
                server.start();
                System.out.println("AUTH8 IS ONLINE");
                server.join();
            } catch (Exception ex) {
                LOG.error("FAILED TO START AUTH8", ex);
                try {
                    server.stop();
                } catch (Exception ex1) {
                    LOG.error("FAILED TO STOP AUTH8", ex1);
                }
                server.destroy();
            }

        }
    }

    /**
     * Accessed when the application want to be stopped from the command line.
     */
    private void stop() {
        LOG.info(StopHandler.shutdown(httpPort, shutdownKey));
    }

    /**
     * Accessed when the application want to be restarted from the command line.
     */
    private void restart() {
        if (status(false)) {
            // Server is running
            try {
                // Stop
                stop();
                Thread.sleep(10000);
                start();
            } catch (InterruptedException ex) {
                LOG.info("SLEEPING THREAD INTERRUPTED DURRING RESTART", ex);
            }
        } else {
            // Server is not running so just start
            start();
        }
    }

    /**
     * Check the current status of auth8 by opening a socket to the HTTP port.
     *
     * @param printMessage
     * @return
     */
    public static boolean status(boolean printMessage) {
        try {
            new Socket("localhost", 8080).close();
            // Jetty is running
            if (printMessage) {
                System.out.println("AUTH8 IS ONLINE");
            }
            return true;
        } catch (IOException e) {
            // Jetty is not running
            if (printMessage) {
                System.out.println("AUTH8 IS OFFLINE");
            }
            return false;
        }
    }

    /**
     * Command line documentation to explain how to start and stop the
     * application.
     */
    private void usage() {
        System.out.println(PS.USAGE_INSTRUCTIONS);
        System.exit(-1);
    }

    /**
     * Enables and disables SSL with the server. If it is enabled it takes care
     * of the configuration.
     *
     * @param serv
     * @param tlsPort
     * @param certificateAlias
     * @return
     */
    private static Server configureTLS(
            Server serv,
            int tlsPort,
            String certificateAlias) {

        // HTTPS Configuration
        HttpConfiguration https = new HttpConfiguration();
        https.setSecureScheme("https");
        https.setSecurePort(tlsPort);
        https.setSendServerVersion(false);
        https.setSendXPoweredBy(false);
        https.setSendDateHeader(false);
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory tlsContextFactory = new SslContextFactory();
        tlsContextFactory.setKeyStorePath(Main.class.getResource(
                "/keystore.jks").toExternalForm());
        tlsContextFactory.setKeyStorePassword(keystorePassword);
        tlsContextFactory.setKeyManagerPassword(keystorePassword);
        // Check if the alias exists. If not default to localhost
        certificateAlias = certificateAlias.replace(".", "-");
        if (keystoreContainsAlias(certificateAlias)) {
            tlsContextFactory.setCertAlias(certificateAlias);
        } else {
            tlsContextFactory.setCertAlias("localhost");
        }
        tlsContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
        ServerConnector tlsConnector = new ServerConnector(
                serv,
                new SslConnectionFactory(tlsContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        tlsConnector.setPort(tlsPort);
        tlsConnector.setName("TLS");

        serv.addConnector(tlsConnector);

        return serv;
    }

    /**
     * Determines if the specified alias exists in the key store.
     *
     * @param alias
     * @return true if the alias exists otherwise false.
     */
    private static boolean keystoreContainsAlias(String alias) {

        InputStream is = null;

        try {
            // Get the keystore
            is = Main.class.getClassLoader().getResourceAsStream("keystore.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = keystorePassword;
            keystore.load(is, password.toCharArray());

            // Create a map to store the certificates
            Map<String, Certificate> aliases = new HashMap<>();

            // Place the certificates into the map
            Enumeration enumeration = keystore.aliases();
            while (enumeration.hasMoreElements()) {
                aliases.put((String) enumeration.nextElement(),
                        keystore.getCertificate(alias));
            }

            return aliases.containsKey(alias);

        } catch (FileNotFoundException e) {
            LOG.error("COULD NOT FIND KEYSTORE", e);
        } catch (KeyStoreException e) {
            LOG.error("KEYSTORE EXCEPTION", e);
        } catch (IOException e) {
            LOG.error("COULD NOT READ KEYSTORE", e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("NO SUCH ALGORYTHM", e);
        } catch (CertificateException e) {
            LOG.error("CERTIFICATE EXCEPTION", e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
        return true;
    }

    /**
     * Checks to see if the persistence storage exists. If it does it retrieves
     * the required data for system initialization. If it doesn't exist it
     * performs an initialization.
     *
     * @return A map of the system keys
     */
    private Map<String, String> init() {

        Map<String, String> keys = new HashMap<>();

        try {
            /*
             Use Flyway to handle DB construction and upgrade. Flyway obtains its 
             SQL details from the src/main/resources/db/migration directory
             */
            Flyway flyway = new Flyway();

            // Point it to the database
            flyway.setDataSource(
                    "jdbc:h2:./h2/auth8;AUTO_SERVER=TRUE",
                    "db20a33e-48e7-4e7d-a3a0-afb4187f9f17",
                    "beb82503-2f48-4907-817b-2a7c2be28464");

            // Start the migration if one exists
            flyway.repair();
            flyway.migrate();
        } catch (FlywayException fe) {
            // Flyway Exception
            LOG.error("FLYWAY MIGRATION ERROR", fe);
        }

        // Get the current version properties
        ResourceBundle current = ResourceBundle.getBundle(PS.VERSION_BUNDLE);

        // Determin if the storage exists by checking the settings
        if (!exists()) {

            // DB does not exist so we need to initialize it.


                // Set the secret keys for the first time
                String sdKey = UUID.randomUUID().toString().replaceAll("-", "");

                // Add the keys to the response
                keys.put(PS.SETTINGS_SHUTDOWN_KEY, sdKey);

                // Insert the required settings into the table
                Setting tls = context.newObject(Setting.class);
                tls.setName(PS.SETTINGS_TLS_KEY);
                tls.setValue("false");

                Setting host = context.newObject(Setting.class);
                host.setName(PS.SETTINGS_HOST_KEY);
                host.setValue("localhost");

                Setting version = context.newObject(Setting.class);
                version.setName(PS.SETTINGS_VERSION_KEY);
                version.setValue(current.getString(PS.BUNDLE_VERSION_KEY));

                Setting build = context.newObject(Setting.class);
                build.setName(PS.SETTINGS_BUILD_KEY);
                build.setValue(current.getString(PS.BUNDLE_BUILD_KEY));

                Setting sd = context.newObject(Setting.class);
                sd.setName(PS.SETTINGS_SHUTDOWN_KEY);
                sd.setValue(sdKey);

                // Create the defult USER,
                User user = context.newObject(User.class);
                user.setPublicKey(PS.DEFAULT_USER_PUBLIC_KEY);
                user.setPrivateKey(PS.DEFAULT_USER_PRIVATE_KEY);

                context.commitChanges();

        } else {
            // The database exists so just update and extract properties.
            // Update the version and build

            Expression versionE = ExpressionFactory.likeExp(Setting.NAME_PROPERTY, PS.SETTINGS_VERSION_KEY);
            SelectQuery versionQ = new SelectQuery(Setting.class, versionE);
            Setting version = (Setting) context.performQuery(versionQ).get(0);
            version.setValue(current.getString(PS.BUNDLE_VERSION_KEY));

            Expression buildE = ExpressionFactory.likeExp(Setting.NAME_PROPERTY, PS.SETTINGS_BUILD_KEY);
            SelectQuery buildQ = new SelectQuery(Setting.class, buildE);
            Setting build = (Setting) context.performQuery(buildQ).get(0);
            build.setValue(current.getString(PS.BUNDLE_BUILD_KEY));

            context.commitChanges();

            // Get the system keys
            try {
                Expression sdKeyE = ExpressionFactory.likeExp(Setting.NAME_PROPERTY, PS.SETTINGS_SHUTDOWN_KEY);
                SelectQuery sdKeyQ = new SelectQuery(Setting.class, sdKeyE);
                Setting sdKey = (Setting) context.performQuery(sdKeyQ).get(0);

                keys.put(PS.SETTINGS_SHUTDOWN_KEY, sdKey.getValue());
            } catch (Exception ex) {
                LOG.error("ERROR SETTING ADMIN AND SHUTDOWNKEY DMS", ex);
                System.exit(-1);
            }

        }

        return keys;
    }

    /**
     * Check for the existence of the persistence layer by checking if the
     * settings table is empty. The actual DB is automatically created at start
     * time or the first time a connection is established so the check can only
     * be performed by checking internal state of the data structure.
     *
     * @return True if the DB exists otherwise false.
     */
    private boolean exists() {

        try {
            SelectQuery query = new SelectQuery(Setting.class);
            List settings = context.performQuery(query);
            return !settings.isEmpty();
        } catch (Exception e) {
            // Exception occured so the settings table doesn't yet exist
            LOG.info("H2 DOES NOT EXIST OR CANNOT BE ACCESSED");
            return false;
        }
    }

    /**
     * The servers real temporary directory has only a very small amount of
     * space and cannot unpack anything greater than 10MB. To get around this we
     * create a tmp working directory in the same directory as the API and hide
     * it.
     *
     * @param context
     * @param currentDir
     */
    private void resetTempDirectory(WebAppContext context, String currentDir) {
        File tmpDir = new File(currentDir, ".tmp");
        tmpDir.deleteOnExit();
        tmpDir.mkdir();
        context.setTempDirectory(tmpDir);
    }

}
