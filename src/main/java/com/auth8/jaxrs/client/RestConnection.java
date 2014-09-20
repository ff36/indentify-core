/*
 * Created Jul 11, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.auth8.jaxrs.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Connector for making REST requests.
 *
 * @version 1.0.0
 * @since Build 140708.154216
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class RestConnection {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(RestConnection.class);
    private HttpURLConnection conn;
    private String date;
    
    static {
        //set the identification of the client
        System.setProperty(
                "http.agent",
                System.getProperty("user.name") + " Auth8");
    }
//</editor-fold>

    /**
     * Creates a new instance of RestConnection
     * 
     * @param baseUrl
     */
    public RestConnection(String baseUrl) {
        this(baseUrl, null, null);
    }

    /**
     * Creates a new instance of RestConnection
     * 
     * @param baseUrl
     * @param params
     */
    public RestConnection(String baseUrl, String[][] params) {
        this(baseUrl, null, params);
    }

    /**
     * Creates a new instance of RestConnection
     * 
     * @param baseUrl
     * @param pathParams
     * @param params
     */
    public RestConnection(
            String baseUrl, 
            String[][] pathParams, 
            String[][] params) {
        try {
            String urlStr = baseUrl;
            if (pathParams != null && pathParams.length > 0) {
                urlStr = replaceParameters(baseUrl, pathParams);
            }
            URL url = new URL(encodeUrl(urlStr, params));
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.setAllowUserInteraction(true);
            
            SimpleDateFormat format = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z");
            date = format.format(new Date());
            conn.setRequestProperty("Date", date);
        } catch (IOException ex) {
            LOG.error("REST CONNECTION ERROR", ex);
        }
    }
    
    /**
     * Set authentication method.
     * 
     * @param authenticator 
     */
    public void setAuthenticator(Authenticator authenticator) {
        Authenticator.setDefault(authenticator);
    }
    
    /**
     * Generate date header.
     * 
     * @return 
     */
    public String getDate() {
        return date;
    }
    
    /**
     * Place GET request.
     * 
     * @return
     * @throws IOException 
     */
    public RestResponse get() throws IOException {
        return get(null);
    }
    
    /**
     * Place GET request.
     * 
     * @param headers
     * @return
     * @throws IOException 
     */
    public RestResponse get(String[][] headers) throws IOException {
        conn.setRequestMethod("GET");
        return connect(headers, null);
    }
    
    /**
     * REST HEAD.
     * 
     * @return
     * @throws IOException 
     */
    public RestResponse head() throws IOException {
        return get(null);
    }
    
    /**
     * REST HEAD.
     * 
     * @param headers
     * @return
     * @throws IOException 
     */
    public RestResponse head(String[][] headers) throws IOException {
        conn.setRequestMethod("HEAD");
        return connect(headers, null);
    }
    
    /**
     * REST response for PUT request.
     * 
     * @param headers
     * @return
     * @throws IOException 
     */
    public RestResponse put(String[][] headers) throws IOException {
        return put(headers, (InputStream) null);
    }
    
    /**
     * Place PUT request.
     * 
     * @param headers
     * @param data
     * @return
     * @throws IOException 
     */
    public RestResponse put(String[][] headers, String data) 
            throws IOException {
        InputStream is = null;
        if (data != null) {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        }
        return put(headers, is);
    }
    
    /**
     * Place PUT request.
     * 
     * @param headers
     * @param is
     * @return
     * @throws IOException 
     */
    public RestResponse put(String[][] headers, InputStream is) 
            throws IOException {
        conn.setRequestMethod("PUT");
        return connect(headers, is);
    }
    
    /**
     * Place PUT request.
     * 
     * @param headers
     * @return
     * @throws IOException 
     */
    public RestResponse post(String[][] headers) throws IOException {
        return post(headers, (InputStream) null);
    }
    
    /**
     * Place POST request.
     * 
     * @param headers
     * @param data
     * @return
     * @throws IOException 
     */
    public RestResponse post(String[][] headers, String data) 
            throws IOException {
        InputStream is = null;
        if (data != null) {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        }
        return post(headers, is);
    }
    
    /**
     * Place POST request.
     * 
     * @param headers
     * @param is
     * @return
     * @throws IOException 
     */
    public RestResponse post(String[][] headers, InputStream is) 
            throws IOException {
        conn.setRequestMethod("POST");
        return connect(headers, is);
    }

    /**
     * Used by post method whose contents are like form input
     * 
     * @param headers
     * @param params
     * @return 
     * @throws java.io.IOException
     */
    public RestResponse post(String[][] headers, String[][] params) 
            throws IOException {
        conn.setRequestMethod("POST");
        conn.setRequestProperty(
                "ContentType", 
                "application/x-www-form-urlencoded");
        String data = encodeParams(params);
        return connect(headers, new ByteArrayInputStream(
                data.getBytes("UTF-8")));
    }
    
    /**
     * Place DELETE request.
     * 
     * @param headers
     * @return
     * @throws IOException 
     */
    public RestResponse delete(String[][] headers) throws IOException {
        conn.setRequestMethod("DELETE");
        return connect(headers, null);
    }

    /**
     * Generate the actual REST connection.
     * 
     * @param baseUrl
     * @param params
     * @return response
     */
    private RestResponse connect(String[][] headers,
            InputStream data) throws IOException {
        try {
            // Send data
            setHeaders(headers);
            
            String method = conn.getRequestMethod();
            
            byte[] buffer = new byte[1024];
            int count = 0;
            
            if (method.equals("PUT") || method.equals("POST")) {
                if (data != null) {
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    
                    while ((count = data.read(buffer)) != -1) {
                        os.write(buffer, 0, count);
                    }
                    os.flush();
                }
            }
            
            RestResponse response = new RestResponse();
            InputStream is = conn.getInputStream();
            
            while ((count = is.read(buffer)) != -1) {
                response.write(buffer, 0, count);
            }
            
            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());
            response.setContentType(conn.getContentType());
            response.setContentEncoding(conn.getContentEncoding());
            response.setLastModified(conn.getLastModified());
            
            return response;
        } catch (IOException e) {
            String errMsg = "Cannot connect to :" + conn.getURL();
            try {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()));
                String line;
                StringBuilder buf = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    buf.append(line);
                    buf.append('\n');
                }
                errMsg = buf.toString();
            } finally {
                throw new IOException(errMsg);
            }
        }
    }
    
    /**
     * Sub in the request parameters to the query string.
     * 
     * @param baseUrl
     * @param pathParams
     * @return 
     */
    private String replaceParameters(
            String baseUrl, String[][] pathParams) {
        String url = baseUrl;
        if (pathParams != null) {
            for (int i = 0; i < pathParams.length; i++) {
                String key = pathParams[i][0];
                String value = pathParams[i][1];
                if (value == null) {
                    value = "";
                }
                url = url.replace(key, value);
            }
        }
        return url;
    }
    
    /**
     * Encode the URL
     * 
     * @param baseUrl
     * @param params
     * @return 
     */
    private String encodeUrl(String baseUrl, String[][] params) {
        String encodedParams = encodeParams(params);
        if (encodedParams.length() > 0) {
            encodedParams = "?" + encodedParams;
        }
        return baseUrl + encodedParams;
    }
    
    /**
     * Encode the query parameters.
     * 
     * @param params
     * @return 
     */
    private String encodeParams(String[][] params) {
        String p = "";
        
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                String key = params[i][0];
                String value = params[i][1];
                
                if (value != null) {
                    try {
                        p += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
                    } catch (UnsupportedEncodingException ex) {
                        LOG.error("UNSUPPORTED URL PARAMETER ENCODING", ex);
                    }
                }
            }
            if (p.length() > 0) {
                p = p.substring(0, p.length() - 1);
            }
        }
        
        return p;
    }
    
    /**
     * Set the REST request headers
     * 
     * @param headers 
     */
    private void setHeaders(String[][] headers) {
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                conn.setRequestProperty(headers[i][0], headers[i][1]);
            }
        }
    }
}
