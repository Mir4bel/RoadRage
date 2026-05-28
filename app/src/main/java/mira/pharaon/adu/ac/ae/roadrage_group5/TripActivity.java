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

// TripActivity implements both listener interfaces
public class TripActivity extends AppCompatActivity
        implements AccelerometerManager.AccelerometerEventListener,
        LocationTracker.LocationEventListener {

    private AccelerometerManager accelManager;
    private LocationTracker locationTracker;

    private TextView tvEventCount, tvSpeed;
    private int eventCount = 0;
    private long tripStartTime;
    private String selectedMood;

    // We collect events here during the trip, save them to DB on stop
    private List<float[]> harshEvents = new ArrayList<>();
    // float[] = {severity, type_as_number} — simple enough for now

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        // Get mood passed from MoodActivity
        selectedMood = getIntent().getStringExtra("mood");

        tvEventCount = findViewById(R.id.tv_event_count);
        tvSpeed      = findViewById(R.id.tv_speed);
        Button btnStop = findViewById(R.id.btn_stop_trip);

        // Create sensor managers — 'this' works because we implement both interfaces
        accelManager    = new AccelerometerManager(this, this);
        locationTracker = new LocationTracker(this, this);

        // Request location permission if not already granted
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

    // Fires when Android shows the permission dialog and user responds
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking();
        }
    }

    // Called by AccelerometerManager when a harsh event is detected
    @Override
    public void onHarshEvent(String type, float severity) {
        eventCount++;
        harshEvents.add(new float[]{severity});
        runOnUiThread(() ->
                tvEventCount.setText(String.valueOf(eventCount))
        );
    }

    // Called by LocationTracker every 2 seconds
    @Override
    public void onSpeedUpdate(float speedKmh) {
        runOnUiThread(() ->
                tvSpeed.setText(String.format(Locale.getDefault(), "%.0f km/h", speedKmh))
        );
    }

    // Called by LocationTracker if speed exceeds threshold
    @Override
    public void onSpeedingDetected(float speedKmh) {
        eventCount++;
        runOnUiThread(() ->
                tvEventCount.setText(String.valueOf(eventCount))
        );
    }

    private void stopTrip() {
        accelManager.stopListening();
        locationTracker.stopTracking();

        int durationSeconds = (int)((SystemClock.elapsedRealtime() - tripStartTime) / 1000);
        int score = new TripScorer().calculateScore(eventCount, durationSeconds);
        String persona = new PersonaManager().getPersona(score);
        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        // Pass results to ResultActivity
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("persona", persona);
        intent.putExtra("mood", selectedMood);
        intent.putExtra("duration", durationSeconds);
        intent.putExtra("eventCount", eventCount);
        intent.putExtra("date", date);
        startActivity(intent);
        finish();
    }

    // IMPORTANT: stop sensors if the app goes to background
    @Override
    protected void onPause() {
        super.onPause();
        accelManager.stopListening();
        locationTracker.stopTracking();
    }
}