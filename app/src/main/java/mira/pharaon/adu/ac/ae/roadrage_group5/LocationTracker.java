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

    private final LocationManager locationManager;
    private final Context context;
    private final LocationEventListener eventListener;
    private float currentSpeedKmh = 0f;

    public interface LocationEventListener {
        void onSpeedUpdate(float speedKmh);
        void onSpeedingDetected(float speedKmh);
        // New: fires every GPS update with full coordinates for map recording
        void onLocationUpdate(double lat, double lng, float speedKmh);
    }

    public LocationTracker(Context context, LocationEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking() {
        Log.d(TAG, "startTracking() called");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission NOT granted");
            return;
        }

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "GPS enabled=" + gpsEnabled + "  Network enabled=" + networkEnabled);

        try {
            if (gpsEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 0, this);
                Log.d(TAG, "GPS updates requested");
            }
            if (networkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                Log.d(TAG, "Network updates requested");
            }

            // Seed UI with last known speed immediately
            Location lastLocation = null;
            if (gpsEnabled)
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation == null && networkEnabled)
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastLocation != null && lastLocation.hasSpeed()) {
                currentSpeedKmh = lastLocation.getSpeed() * 3.6f;
                eventListener.onSpeedUpdate(currentSpeedKmh);
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
        Log.d(TAG, "Location update — lat=" + location.getLatitude()
                + " lng=" + location.getLongitude()
                + " hasSpeed=" + location.hasSpeed());

        // Guard: only use speed if the fix actually has one
        if (location.hasSpeed()) {
            currentSpeedKmh = location.getSpeed() * 3.6f;
        } else {
            currentSpeedKmh = 0f;
        }

        Log.d(TAG, "Speed km/h=" + currentSpeedKmh);

        // Existing speed callback (still used by TripActivity UI)
        eventListener.onSpeedUpdate(currentSpeedKmh);

        // New: full-coordinate callback for map recording
        eventListener.onLocationUpdate(
                location.getLatitude(), location.getLongitude(), currentSpeedKmh);

        if (currentSpeedKmh > SPEED_THRESHOLD_KMH) {
            Log.d(TAG, "Speeding detected");
            eventListener.onSpeedingDetected(currentSpeedKmh);
        }
    }

    @Override public void onProviderEnabled(String provider)  { Log.d(TAG, "Provider enabled: " + provider); }
    @Override public void onProviderDisabled(String provider) { Log.d(TAG, "Provider disabled: " + provider); }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
}