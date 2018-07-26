package com.kdawg.apicaller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Krishna N. Deoram on 2018-07-13 at 8:25 AM.
 * Gumi is love. Gumi is life.
 */

class SendGet extends AsyncTask<GetObject, Void, String> {

    private final String TAG = "SendGet";

    private GetObject getObject;
    private Timer timer;
    private HttpURLConnection connection = null;

    @SuppressLint({"AllowAllHostnameVerifier", "SSLCertificateSocketFactoryGetInsecure"})
    @Override
    protected String doInBackground(GetObject... getObjects) {

        getObject = getObjects[0];

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (connection != null)
                    connection.disconnect();
                onPostExecute("error: Request timeout for " + getObject.getUrl());
            }
        }, getObject.getTimeout());

        try {
            URL url = new URL(getObject.getUrl());
            if (getObject.isSecure()) {
                connection = (HttpsURLConnection) url.openConnection();
                if (!ApiCaller.trustCertificate) { //The server side is encrypted with an insecure SSL certificate, but make the request anyway
                    trustAllHosts(); //New way. Actually works
                    ((HttpsURLConnection) connection).setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                }
            } else
                connection = (HttpURLConnection)url.openConnection();
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : getObject.getHeaders().entrySet())
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            StringBuilder result = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
                result.append(line);
            rd.close();
            Log.e(TAG, "Response: " + result.toString());
            Log.e(TAG, "response code: " + connection.getResponseCode()); //200 = it went through fine.
            Log.e(TAG, "response message: " + connection.getResponseMessage());
            return result.toString(); //Give the response back to the callback originally set.

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
            getObject.getCallback().onResponse(s);
        } else
            getObject.getCallback().onError(s);
    }

    private static void trustAllHosts() { //Need this for untrusted SSL connections
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
