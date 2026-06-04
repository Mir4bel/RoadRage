package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripActivity extends AppCompatActivity
        implements AccelerometerManager.AccelerometerEventListener,
        LocationTracker.LocationEventListener {

    private AccelerometerManager accelManager;
    private LocationTracker locationTracker;

    private TextView tvEventCount, tvSpeed;
    private int eventCount = 0;
    private long tripStartTime;
    private String selectedMood;

    // GPS points collected during the trip — flushed to DB in stopTrip()
    private final List<double[]> locationPoints = new ArrayList<>(); // [lat, lng, speed_kmh]

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        selectedMood = getIntent().getStringExtra("mood");

        tvEventCount = findViewById(R.id.tv_event_count);
        tvSpeed      = findViewById(R.id.tv_speed);
        Button btnStop = findViewById(R.id.btn_stop_trip);

        accelManager    = new AccelerometerManager(this, this);
        locationTracker = new LocationTracker(this, this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            locationTracker.startTracking();
        }

        accelManager.startListening();
        tripStartTime = SystemClock.elapsedRealtime();

        btnStop.setOnClickListener(v -> stopTrip());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking();
        }
    }

    // ─── AccelerometerManager callbacks ──────────────────────────────────────

    @Override
    public void onHarshEvent(String type, float severity) {
        eventCount++;
        runOnUiThread(() -> tvEventCount.setText(String.valueOf(eventCount)));
    }

    // ─── LocationTracker callbacks ────────────────────────────────────────────

    @Override
    public void onSpeedUpdate(float speedKmh) {
        // Speed UI is now handled in onLocationUpdate; this satisfies the interface.
    }

    @Override
    public void onLocationUpdate(double lat, double lng, float speedKmh) {
        // Collect every GPS fix for the map
        locationPoints.add(new double[]{lat, lng, speedKmh});
        runOnUiThread(() ->
                tvSpeed.setText(String.format(Locale.getDefault(), "%.0f km/h", speedKmh)));
    }

    @Override
    public void onSpeedingDetected(float speedKmh) {
        eventCount++;
        runOnUiThread(() -> tvEventCount.setText(String.valueOf(eventCount)));
    }

    // ─── Trip lifecycle ───────────────────────────────────────────────────────

    private void stopTrip() {
        accelManager.stopListening();
        locationTracker.stopTracking();

        int durationSeconds = (int) ((SystemClock.elapsedRealtime() - tripStartTime) / 1000);
        int score    = calculateScore(eventCount, durationSeconds);
        String persona = new PersonaManager().getPersona(score);

        // Date now includes time so History shows when exactly each trip happened
        String date = new SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
                .format(new Date());

        // Save trip and all its GPS points to the database
        // NOTE: remove db.insertTrip() and db.updateUserStats() from ResultActivity
        //       — they are now handled here to avoid double-saving.
        DatabaseManager db = new DatabaseManager(this);
        long tripId = db.insertTrip(date, durationSeconds, score, selectedMood, eventCount, persona);

        for (double[] point : locationPoints) {
            db.insertLocation(tripId, point[0], point[1], (float) point[2]);
        }

        db.updateUserStats(this);

        // Pass tripId so ResultActivity can show a "View on Map" button if desired
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score",      score);
        intent.putExtra("persona",    persona);
        intent.putExtra("mood",       selectedMood);
        intent.putExtra("duration",   durationSeconds);
        intent.putExtra("eventCount", eventCount);
        intent.putExtra("date",       date);
        intent.putExtra("tripId",     tripId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelManager.stopListening();
        locationTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelManager.startListening();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking();
        }
    }

    public int calculateScore(int eventCount, int durationSeconds) {
        int score = 100;
        score -= eventCount * 8;
        if (eventCount == 0 && durationSeconds >= 300) score = 100;
        return Math.max(0, score);
    }
}