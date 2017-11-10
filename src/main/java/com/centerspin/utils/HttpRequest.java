package com.centerspin.utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class HttpRequest {
    
    
    private enum HTTP_METHOD {
        GET, POST, PUT, DELETE
    }
    
    // Request Properties
    private String authorization;
    private final Map<String, String> headers;
    
    private int connectionTimeout = 1000;
    private int readTimeout = 1000;
    
    
    // Request Data
    private final String address;
    private String requestBody;
    private final Map<String,String> queryParameters;
    
    
    
    public HttpRequest(String url) {
        this.address = url;
        
        this.headers = new HashMap<>();
        this.queryParameters = new HashMap<>();
    }
    
    public String getAddress() {
        return address;
    }
    
    // ==== Http Method Invocation ====
    public HttpResponse get() throws IOException {
        return submitRequest(HTTP_METHOD.GET, address, queryParameters, requestBody, connectionTimeout, readTimeout);
    }
    public HttpResponse post() throws IOException {
        return submitRequest(HTTP_METHOD.POST, address, queryParameters, requestBody, connectionTimeout, readTimeout);
    }
    public HttpResponse put() throws IOException {
        return submitRequest(HTTP_METHOD.PUT, address, queryParameters, requestBody, connectionTimeout, readTimeout);
    }
    public HttpResponse delete() throws IOException {
        return submitRequest(HTTP_METHOD.DELETE, address, queryParameters, requestBody, connectionTimeout, readTimeout);
    }

    
    // ==== Request Property Setters ====
    
    public HttpRequest setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }
    
    public HttpRequest setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }
    
    public HttpRequest setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    
    public HttpRequest setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
    
    // ==== Request Data Setters ====
    
    
    public HttpRequest setQueryParameter(String key, Object value) {
        queryParameters.put(key, String.valueOf(value));
        return this;
    }
    
    public HttpRequest requestBody(String body) {
        this.requestBody = body;
        return this;
    }
    
    
    // ==== Private Low-Level Methods ====
    
    private HttpResponse submitRequest(HTTP_METHOD method, String address, Map<String,String> queryParameters, String requestBody, int connectionTimeout, int readTimeout) throws IOException {

        
        String queryString = "";
        
        int i = 0;
        for (Map.Entry<String,String> queryParameter : queryParameters.entrySet()) {
            if (i > 0) queryString += "&";
            queryString += queryParameter.getKey() + "=" + queryParameter.getValue();
            i++;
        }
        
        if (queryString.isEmpty() == false) {
            address += "?" + queryString;
        }
        
        URL url = new URL(address);
            
        HttpURLConnection conn = null;
        HttpResponse httpResponse = null;
        
        try {
            
            conn = (HttpURLConnection)url.openConnection();
           
            conn.setRequestMethod(method.toString());
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);

            for (String name : headers.keySet()) {
                conn.addRequestProperty(name, headers.get(name));
            }
            
            if (authorization != null && authorization.isEmpty() == false) {
                conn.setRequestProperty("Authorization", authorization);
            }

            if (method.equals(HTTP_METHOD.POST) || method.equals(HTTP_METHOD.POST)) {
                conn.setDoOutput(true);
                try (OutputStream output = conn.getOutputStream()) {
                    output.write(requestBody.getBytes());
                }
            }

            InputStream inStream = (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) ? conn.getErrorStream() : conn.getInputStream();

            // if we have received a compressed reponse, decompress it
            String encoding = conn.getContentEncoding();
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                inStream = new GZIPInputStream(inStream);
            }

            byte[] responseBytes = readFully(inStream);

            httpResponse = new HttpResponse(this,
                    conn.getResponseCode(),
                    conn.getResponseMessage(),
                    conn.getHeaderFields(),
                    responseBytes
            );

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return httpResponse;
    }

    private static byte[] readFully(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8 * 1024];         // a buffer to store the bytes from each read operation
        int nBytes;                                 // number of bytes read in one read operation
        while ((nBytes = input.read(buffer)) != -1) {
            output.write(buffer, 0, nBytes);
        }
        return output.toByteArray();
    }

}
