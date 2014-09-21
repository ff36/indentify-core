package com.auth8.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tarka
 */
public class LazySingleton {

    private static volatile LazySingleton instance = null;

    // private constructor
    private LazySingleton() {
        request = new HashMap<>();
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                // Double check
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }

    private Map<String, AuthRequest> request;

    public Map<String, AuthRequest> getRequest() {
        return request;
    }

    public void setRequest(Map<String, AuthRequest> request) {
        this.request = request;
    }
    
    
    
}
