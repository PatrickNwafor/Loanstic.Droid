package com.icubed.loansticdroid.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class LocationProviderUtil {

    Context context;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    public static LocationManager mLocationManager;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    boolean isSingleUpdate = false;

    public LocationProviderUtil(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public interface LocationCallback {
        void onNewLocationAvailable(GPSCoordinates location);
    }

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    LocationCallback locationCallback;
    public void requestSingleUpdate(final LocationCallback callback) {
        isSingleUpdate = true;
        locationCallback = callback;
        getCurrentLocation();
    }

    public void requestContinousUpdate(final LocationCallback callback) {
        isSingleUpdate = false;
        locationCallback = callback;
        getCurrentLocation();
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d(TAG, "getCurrentLocation: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                locationCallback.onNewLocationAvailable(new GPSCoordinates(location));
                if(isSingleUpdate) {
                    mLocationManager.removeUpdates(mLocationListener);
                }
            } else {
                Log.d(TAG, "onLocationChanged: Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /************Requesting Location Permission**********/
    public void getLocationPermission(){
        Log.d(TAG, "checking for permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(context, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission already granted");
        }else{
            Log.d(TAG, "No permission yet");
            ActivityCompat.requestPermissions((Activity) context, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocation(){
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGPSEnabled || isNetworkEnabled)) AndroidUtils.gpsDisabledMessage(context);
        else {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
                return;
            }

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            }

            if(isGPSEnabled){
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            }
        }
    }

    public static class GPSCoordinates {
        public Location getLocation;

        public GPSCoordinates(Location theLocation) {
            getLocation = theLocation;
        }
    }
}