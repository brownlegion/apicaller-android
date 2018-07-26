package com.kdawg.apicaller;

import java.util.HashMap;

/**
 * Created by Krishna N. Deoram on 2018-07-13 at 8:24 AM.
 * Gumi is love. Gumi is life.
 */

class GetObject {

    private String url;
    private ApiCaller.ResponseCallback callback;
    private boolean secure;
    private int timeout;
    private HashMap<String, String> headers;

    public GetObject(String url, ApiCaller.ResponseCallback callback, boolean secure, int timeout, HashMap<String, String> headers) {
        this.url = url;
        this.callback = callback;
        this.secure = secure;
        this.timeout = timeout;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public ApiCaller.ResponseCallback getCallback() {
        return callback;
    }

    public boolean isSecure() {
        return secure;
    }

    public int getTimeout() {
        return timeout;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}