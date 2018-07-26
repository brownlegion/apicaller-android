package com.kdawg.apicaller;

import android.annotation.SuppressLint;
import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Krishna N. Deoram on 2018-07-13 at 8:35 AM.
 * Gumi is love. Gumi is life.
 */

class SendPost extends AsyncTask<PostObject, Void, String> { //Must make the request on a separate thread.

    private final String TAG = "SendPost";

    private PostObject postObject;
    private Timer timer;
    private HttpURLConnection connection = null;

    @SuppressLint({"SSLCertificateSocketFactoryGetInsecure", "AllowAllHostnameVerifier"}) //To compensate for untrusted certificates
    @Override
    protected String doInBackground(PostObject... postObjects) {

        postObject = postObjects[0];

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (connection != null)
                    connection.disconnect();
                onPostExecute("error: Request timeout for " + postObject.getUrl());
            }
        }, postObject.getTimeout());
        try {
            URL url = new URL(postObject.getUrl());
            if (postObject.isSecure()) {
                connection = (HttpsURLConnection) url.openConnection();
                if (!ApiCaller.trustCertificate) { //The server side is encrypted with an insecure SSL certificate, but make the request anyway
                    ((HttpsURLConnection) connection).setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                    ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
                }
            } else
                connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");
            //connection.setConnectTimeout(postObject.getTimeout()); //Apparently this is a thing. Maybe we don't need the timer task
            for (Map.Entry<String, String> entry : postObject.getHeaders().entrySet())
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            if (postObject.getParameters() == null)
                writer.write(postObject.getArray().toString());
            else
                writer.write(postObject.getParameters().toString()); //Make the post.
            writer.flush();
            writer.close();
            os.close();
            connection.connect();
            Log.e(TAG, "Made connection, getting responses");
            Log.e(TAG, "response code: " + connection.getResponseCode()); //200 = it went through fine.
            Log.e(TAG, "response message: " + connection.getResponseMessage());
            String line;
            StringBuilder response = new StringBuilder();
            BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream())); //Read the response.
            while ((line=br.readLine()) != null) {
                response.append(line);
            }
            Log.e(TAG, "response: " + response.toString());
            return response.toString(); //Give the response back to the callback originally set.

        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        timer.cancel();
        if (!s.startsWith("error")) {
            postObject.getCallback().onResponse(s);
        } else
            postObject.getCallback().onError(s);
    }
}
