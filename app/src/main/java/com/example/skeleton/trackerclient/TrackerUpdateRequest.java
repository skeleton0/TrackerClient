package com.example.skeleton.trackerclient;

import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

class TrackerUpdateRequest extends AsyncTask<String, Void, TrackerUpdate> {
    private static final String LOG_TAG = TrackerUpdateRequest.class.getSimpleName();

    @Override
    protected TrackerUpdate doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        String serverResponse = null;

        try {
            URL serverUrl = new URL(strings[0]);

            connection = (HttpsURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverResponse = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (serverResponse != null) {
            Log.d(LOG_TAG, "Received response from server: " + serverResponse);
        }

        return null;
    }

    @Override
    protected void onPostExecute(TrackerUpdate trackerUpdate) {
        super.onPostExecute(trackerUpdate);
    }
}
