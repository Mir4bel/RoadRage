package mira.pharaon.adu.ac.ae.roadrage_group5;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class LocationTracker implements LocationListener {

    private static final String TAG = "LocationTracker";
    private static final float SPEED_THRESHOLD_KMH = 120f;

    private LocationManager locationManager;
    private Context context;
    private LocationEventListener eventListener;
    private float currentSpeedKmh = 0f;

    public interface LocationEventListener {
        void onSpeedUpdate(float speedKmh);
        void onSpeedingDetected(float speedKmh);
    }

    public LocationTracker(Context context, LocationEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking() {

        Log.d(TAG, "startTracking() called");

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Location permission NOT granted");
            return;
        }

        boolean gpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean networkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d(TAG, "GPS enabled = " + gpsEnabled);
        Log.d(TAG, "Network enabled = " + networkEnabled);

        try {

            if (gpsEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0,
                        this
                );

                Log.d(TAG, "GPS updates requested");
            }

            if (networkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        0,
                        this
                );

                Log.d(TAG, "Network updates requested");
            }

            Location lastLocation = null;

            if (gpsEnabled) {
                lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (lastLocation == null && networkEnabled) {
                lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (lastLocation != null) {
                Log.d(TAG, "Last known location found");
                Log.d(TAG, "Last speed = " + lastLocation.getSpeed());

                currentSpeedKmh = lastLocation.getSpeed() * 3.6f;
                eventListener.onSpeedUpdate(currentSpeedKmh);
            } else {
                Log.d(TAG, "No last known location");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates", e);
        }
    }

    public void stopTracking() {
        try {
            locationManager.removeUpdates(this);
            Log.d(TAG, "Tracking stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping tracking", e);
        }
    }

    public float getCurrentSpeed() {
        return currentSpeedKmh;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Location update received");

        Log.d(TAG, "Latitude = " + location.getLatitude());
        Log.d(TAG, "Longitude = " + location.getLongitude());
        Log.d(TAG, "Has speed = " + location.hasSpeed());
        Log.d(TAG, "Raw speed = " + location.getSpeed());

        currentSpeedKmh = location.getSpeed() * 3.6f;

        Log.d(TAG, "Speed km/h = " + currentSpeedKmh);

        eventListener.onSpeedUpdate(currentSpeedKmh);

        if (currentSpeedKmh > SPEED_THRESHOLD_KMH) {
            Log.d(TAG, "Speeding detected");
            eventListener.onSpeedingDetected(currentSpeedKmh);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Provider disabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Status changed: " + provider);
    }
}