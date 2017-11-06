package com.centerspin.utils;
import java.io.UnsupportedEncodingException;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpResponse {

    private HttpRequest request;
    private int statusCode;
    private String statusMessage;
    private Map<String, List<String>> headers;
    private byte[] body;

    public HttpResponse(HttpRequest request, int statusCode, String statusMessage, Map<String, List<String>> headers, byte[] body) {
        this.request = request;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusMessage() {
        return statusMessage;
    }

    public Map<String, List<String>> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public JSONObject toJSONObject() {
        return new JSONObject(getBody());
    }
    
    public JSONArray toJSONArray() {
        return new JSONArray(getBody());
    }
    
    public String getBody() {
        String str = "";
        try {
            str = new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return str;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(0)
                .append(statusCode).append(' ').append(statusMessage)
                .append(" [").append(request.getAddress()).append("]\n\n");

        for (String key : headers.keySet()) {
            if (key != null) {
                buf.append(String.format("%s: %s\n", key, headers.get(key)));
            }
        }
        buf.append('\n').append(body);
        return buf.toString();
    }

}

