package com.example.skeleton.trackerclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<TrackerUpdate> {
    private static final String LOG_TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private Timer mUpdateTimer = new Timer();
    private TimerTask mUpdateRequestTask = null;

    @Override
    protected void onPause() {
        super.onPause();

        mUpdateRequestTask.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = new Bundle();
        bundle.putString("serverAddress", getString(R.string.tracker_server_url));
        getSupportLoaderManager().restartLoader(0, bundle, this);

        mUpdateRequestTask = new TimerTask() {
            @Override
            public void run() {

            }
        };

        mUpdateTimer.schedule(mUpdateRequestTask, 0, 30000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @NonNull
    @Override
    public Loader<TrackerUpdate> onCreateLoader(int i, @Nullable Bundle bundle) {
        URL serverAddress = null;

        if (bundle != null) {
            try {
                serverAddress = new URL(bundle.getString("serverAddress"));
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Caught exception trying to parse URL: " + e.getMessage());
            }
        }

        return new TrackerUpdateRequest(this, serverAddress);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<TrackerUpdate> loader, TrackerUpdate trackerUpdate) {
        if (mMap == null || trackerUpdate == null) {
            return;
        }

        LatLng trackerPos = new LatLng(trackerUpdate.mLatitude, trackerUpdate.mLongitude);

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(trackerPos).title("MT09SP"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(trackerPos));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<TrackerUpdate> loader) {
    }
}
