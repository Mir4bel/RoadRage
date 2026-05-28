package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

public class LocationTracker implements LocationListener {

    // Threshold — flag as harsh event if over 120 km/h
    private static final float SPEED_THRESHOLD_KMH = 120f;

    private LocationManager locationManager;
    private Context context;
    private LocationEventListener eventListener;
    private float currentSpeedKmh = 0f;

    // This interface is how LocationTracker talks back to TripActivity
    // TripActivity will implement it
    public interface LocationEventListener {
        void onSpeedUpdate(float speedKmh);
        void onSpeedingDetected(float speedKmh);
    }

    // Constructor — call this from TripActivity:
    // locationTracker = new LocationTracker(this, this);
    public LocationTracker(Context context, LocationEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // Call this when user taps Start Trip
    public void startTracking() {
        // Check permission before doing anything — this is why it silently fails
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return; // permission not granted, do nothing
        }
        // Update every 2 seconds or every 5 meters — whichever comes first
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2000, 5f, this);
    }

    // Call this when user taps Stop Trip
    public void stopTracking() {
        locationManager.removeUpdates(this);
    }

    public float getCurrentSpeed() {
        return currentSpeedKmh;
    }

    // This fires automatically every time GPS gets a new position
    @Override
    public void onLocationChanged(Location location) {
        if (location.hasSpeed()) {
            currentSpeedKmh = location.getSpeed() * 3.6f; // convert m/s to km/h
            eventListener.onSpeedUpdate(currentSpeedKmh);

            if (currentSpeedKmh > SPEED_THRESHOLD_KMH) {
                eventListener.onSpeedingDetected(currentSpeedKmh);
            }
        }
    }

    // These must be here — LocationListener requires them even if empty
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onProviderDisabled(String provider) {}
}