package com.auth8.jaxrs.client;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author tarka
 */
public class Client {

    public static HttpResponse doPost(final String url, int timeout) {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout * 1000);
            HttpConnectionParams.setSoTimeout(params, timeout * 1000);
            //post.addHeader("content-type", "application/json");
            //StringEntity input = new StringEntity("{}");
            //post.setEntity(input);
            HttpResponse response = client.execute(post);
            return response;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

}
