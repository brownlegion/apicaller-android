package com.kdawg.apicaller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Krishna N. Deoram on 2018-07-13 at 8:24 AM.
 * Gumi is love. Gumi is life.
 */

class PostObject { //Much easier to send this to the AsyncTask

    private JSONObject parameters;
    private JSONArray parametersArray;
    private String url/*, authenticationHash*/;
    private ApiCaller.ResponseCallback callback;
    private boolean secure;
    private int timeout;
    private HashMap<String, String> headers;

    PostObject(JSONObject parameters, String url, ApiCaller.ResponseCallback callback, /*String username, String password,*/ boolean secure, int timeout, HashMap<String, String> headers) {
        this.parameters = parameters;
        this.url = url;
        this.callback = callback;
        //authenticationHash = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
        this.secure = secure;
        this.timeout = timeout;
        this.headers = headers;
    }

    void setArrayOfParameters(JSONArray array) {
        this.parametersArray = array;
    }

    JSONArray getArray() {
        return parametersArray;
    }

    JSONObject getParameters() {
        return parameters;
    }

    String getUrl() {
        return url;
    }

    ApiCaller.ResponseCallback getCallback() {
        return callback;
    }

    boolean isSecure() {
        return secure;
    }

    int getTimeout() {
        return timeout;
    }

    HashMap<String, String> getHeaders() {
        return headers;
    }
}
