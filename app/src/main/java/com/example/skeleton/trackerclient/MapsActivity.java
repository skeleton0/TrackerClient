package com.example.skeleton.trackerclient;

import android.os.Handler;
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
    private boolean mSetMapPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSetMapPosition = true;
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

        Handler handler = new Handler();

        Bundle bundle = new Bundle();
        bundle.putString("serverAddress", "https://192.168.1.100:47000/0");
        MapsActivity currentActivity = this;

        mUpdateRequestTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> getSupportLoaderManager().restartLoader(0, bundle, currentActivity));
            }
        };

        mUpdateTimer.schedule(mUpdateRequestTask, 0, 30000);
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
        if (trackerUpdate == null) {
            return;
        }

        LatLng trackerPos = new LatLng(trackerUpdate.mLatitude, trackerUpdate.mLongitude);

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(trackerPos).title("MT09SP"));

        if (mSetMapPosition) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trackerPos, 16.0f));
            mSetMapPosition = false;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<TrackerUpdate> loader) {
    }
}
