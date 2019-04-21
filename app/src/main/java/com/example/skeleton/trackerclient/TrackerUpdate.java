package com.example.skeleton.trackerclient;

class TrackerUpdate {
    String mTimestamp;
    double mLatitude;
    double mLongitude;

    TrackerUpdate(String timestamp, double latitude, double longitude) {
        mTimestamp = timestamp;
        mLatitude = latitude;
        mLongitude = longitude;
    }
}
