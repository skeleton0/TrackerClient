package com.example.skeleton.trackerclient;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class TrackerUpdateRequest extends AsyncTaskLoader<TrackerUpdate> {
    private static final String LOG_TAG = TrackerUpdateRequest.class.getSimpleName();
    private URL mServerAddress;

    TrackerUpdateRequest(Context context, URL serverAddress) {
        super(context);

        mServerAddress = serverAddress;
    }

    @Override
    public TrackerUpdate loadInBackground() {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        String serverResponse = null;

        try {
            connection = (HttpsURLConnection) mServerAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverResponse = reader.readLine();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Connection request threw exception: " + e.getMessage());
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

        TrackerUpdate update = null;

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);

            update = new TrackerUpdate(jsonObject.getString("t"),
                                       jsonObject.getDouble("la"),
                                       jsonObject.getDouble("lo"));
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Caught JSONException while parsing server response: " + e.getMessage());
        }

        return update;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }
}
