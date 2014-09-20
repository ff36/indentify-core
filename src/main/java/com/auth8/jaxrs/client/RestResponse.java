/*
 * Created Jul 11, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.auth8.jaxrs.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * Basic REST response object.
 *
 * @version 1.0.0
 * @since Build 140708.154216
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class RestResponse {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private ByteArrayOutputStream os;
    private String contentType = "binary/octet-stream";
    private String contentEncoding;
    private int responseCode;
    private String responseMsg;
    private long lastModified;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public RestResponse() {
        os = new ByteArrayOutputStream();
    }
    
    public RestResponse(byte[] bytes) throws IOException {
        this();
        
        byte[] buffer = new byte[1024];
        int count = 0;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        while ((count = bis.read(buffer)) != -1) {
            write(buffer, 0, count);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of contentType.
     *
     * @return the value of contentType
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Get the value of responseMsg.
     *
     * @return the value of responseMsg
     */
    public String getResponseMessage() {
        return responseMsg;
    }

    /**
     * Get the value of responseCode.
     *
     * @return the value of responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }
    
    /**
     * Get the value of lastModified.
     *
     * @return the value of lastModified
     */
    public long getLastModified() {
        return lastModified;
    }
    
    /**
     * Get the value of output stream (os).
     *
     * @return the value of output stream (os)
     */
    public OutputStream getOutputStream() {
        return os;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of contentType.
     *
     * @param contentType new value of contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * Set the value of contentEncoding.
     *
     * @param contentEncoding new value of contentEncoding
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
    
    /**
     * Set the value of responseMsg.
     *
     * @param responseMsg new value of responseMsg
     */
    public void setResponseMessage(String responseMsg) {
        this.responseMsg = responseMsg;
    }
    
    /**
     * Set the value of responseCode.
     *
     * @param responseCode new value of responseCode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    /**
     * Set the value of lastModified.
     *
     * @param lastModified new value of lastModified
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
//</editor-fold>
    
    
    /**
     * Response object data as a byte array.
     *
     * @return the value of index
     */
    public byte[] getDataAsByteArray() {
        return os.toByteArray();
    }
    
    /**
     * Writes the output stream.
     * 
     * @param bytes
     * @param start
     * @param length 
     */
    public void write(byte[] bytes, int start, int length) {
        os.write(bytes, start, length);
    }

    /**
     * Response object data as a string representation.
     * 
     * @return UTF-8 encoded string representation of the response data.
     */
    public String getDataAsString() {
        try {
            return os.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RestConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    /**
     * Binds the response to the specified class object.
     * 
     * @param <T>
     * @param jaxbClass
     * @return An instance of the specified object with the populated values
     * from the response object.
     * @throws JAXBException 
     */
    public <T> T getDataAsObject(Class<T> jaxbClass) throws JAXBException {
        return getDataAsObject(jaxbClass, jaxbClass.getPackage().getName());
    }
    
    /**
     * Binds the response to the specified class object.
     * 
     * @param <T>
     * @param clazz
     * @param packageName
     * @return An instance of the specified object with the populated values
     * from the response object.
     * @throws JAXBException 
     */
    public <T> T getDataAsObject(
            Class<T> clazz, String packageName) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(new StreamSource(new StringReader(getDataAsString())));
        
        if (obj instanceof JAXBElement) {
            return (T) ((JAXBElement) obj).getValue();
        } else {
            return (T) obj;
        }        
    }
}
