package com.kdawg.apicaller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Krishna N. Deoram on 2018-01-17 at 1:16 PM.
 * Gumi is love. Gumi is life.
 */

public class ApiCaller { //Uses an HttpsUrlConnection to send the POST data and make GETs

    public interface ResponseCallback {

        void onResponse(String response);
        void onError(String error);
    }

    private final static String TAG = "ApiCaller";
    private static ApiCaller instance;
    static boolean trustCertificate = false; //If we want to make an https request, but the certificate on the server side is untrusted
    private static final int TIMEOUT_DELAY = 10000; //1000 = 1 second

    private ApiCaller(){
    }

    public static synchronized ApiCaller getInstance(){ //Singleton because #baylife.
        if(instance == null)
            instance = new ApiCaller();
        return instance;
    }

    public void sendPostArray(String sendTo, JSONArray array, boolean secure, final ResponseCallback callback) { //Array of JSON objects
        sendPostArray(sendTo, array, new HashMap<String, String>(), secure, callback);
    }

    public void sendPostArray(String sendTo, JSONArray array, HashMap<String, String> headers, boolean secure, ResponseCallback callback) {
        sendPostArray(sendTo, array, headers, TIMEOUT_DELAY, secure, callback);
    }

    public void sendPostArray(String sendTo, JSONArray array, HashMap<String, String> headers, int timeOut, boolean secure, ResponseCallback callback) {
        PostObject postObject = new PostObject(null, sendTo, callback, secure, timeOut, headers);
        postObject.setArrayOfParameters(array);
        Log.e(TAG, "Sending a POST to " + sendTo);
        Log.e(TAG, "With these headers: " + headers.toString());
        Log.e(TAG, "With this data: " + array.toString());
        new SendPost().execute(postObject);
    }

    public void sendPost(String sendTo, JSONObject parameters, boolean secure, final ResponseCallback callback) { //No headers, default timeout
        sendPost(sendTo,parameters, new HashMap<String, String>(), secure, callback);
    }

    public void sendPost(String sendTo, JSONObject parameters, HashMap<String, String> headers, boolean secure, final ResponseCallback callback) { //With headers, default timeout
        sendPost(sendTo, parameters, headers, TIMEOUT_DELAY, secure, callback);
    }

    public void sendPost(String sendTo, JSONObject parameters, HashMap<String, String> headers, int timeout, boolean secure, final ResponseCallback callback) { //With headers, custom timeout
        if (parameters == null)
            parameters = new JSONObject();
        PostObject postObject = new PostObject(parameters, sendTo, callback/*, username, password*/, secure, timeout, headers);
        Log.e(TAG, "Sending a POST to " + sendTo);
        Log.e(TAG, "With these headers: " + headers.toString());
        Log.e(TAG, "With this data: " + parameters.toString());
        new SendPost().execute(postObject);
    }

    public void sendGet(String sendTo, boolean secure, final ResponseCallback callback) { //No headers, default timeout
        sendGet(sendTo, new HashMap<String, String>(), secure, callback);
    }

    public void sendGet(String sendTo, HashMap<String, String> headers, boolean secure, final ResponseCallback callback) { //With headers, default timeout
        sendGet(sendTo, headers, TIMEOUT_DELAY, secure, callback);
    }

    public void sendGet(String sendTo, HashMap<String, String> headers, int timeout, boolean secure, final ResponseCallback callback) { //With headers, with custom timeout
        GetObject getObject = new GetObject(sendTo, callback, secure, timeout, headers);
        Log.e(TAG, "Making a GET to " + sendTo);
        Log.e(TAG, "With these headers: " + headers.toString());
        new SendGet().execute(getObject);
    }
}