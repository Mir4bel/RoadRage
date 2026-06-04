package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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

    private TextView        tvSpeed, tvEventCount, tvSpeedStatus, tvTimer;
    private SpeedGraphView  speedGraph;
    private MaterialCardView cardSpeedHero;

    private int     eventCount    = 0;
    private long    tripStartTime;
    private String  selectedMood;

    private final List<double[]> locationPoints = new ArrayList<>();
    private final Handler timerHandler          = new Handler();
    private Runnable timerRunnable;

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        selectedMood = getIntent().getStringExtra("mood");

        tvSpeed       = findViewById(R.id.tv_speed);
        tvEventCount  = findViewById(R.id.tv_event_count);
        tvSpeedStatus = findViewById(R.id.tv_speed_status);
        tvTimer       = findViewById(R.id.tv_trip_timer);
        speedGraph    = findViewById(R.id.speed_graph);
        cardSpeedHero = findViewById(R.id.card_speed_hero);
        MaterialButton btnStop = findViewById(R.id.btn_stop_trip);

        accelManager    = new AccelerometerManager(this, this);
        locationTracker = new LocationTracker(this, this);

        // Wire crash detection if AccelerometerManager supports it
        accelManager.setCrashListener(magnitude ->
                runOnUiThread(() -> {
                    Intent crashIntent = new Intent(TripActivity.this, CrashAlertActivity.class);
                    crashIntent.putExtra("magnitude", magnitude);
                    startActivity(crashIntent);
                })
        );

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

        startTimer();
        btnStop.setOnClickListener(v -> stopTrip());
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = (SystemClock.elapsedRealtime() - tripStartTime) / 1000;
                long min = elapsed / 60, sec = elapsed % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%d:%02d", min, sec));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
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

    // ─── Sensor callbacks ────────────────────────────────────────────────────

    @Override
    public void onHarshEvent(String type, float severity) {
        eventCount++;
        runOnUiThread(() -> tvEventCount.setText(String.valueOf(eventCount)));
    }

    @Override
    public void onSpeedUpdate(float speedKmh) { /* handled in onLocationUpdate */ }

    @Override
    public void onLocationUpdate(double lat, double lng, float speedKmh) {
        locationPoints.add(new double[]{lat, lng, speedKmh});
        runOnUiThread(() -> {
            tvSpeed.setText(String.format(Locale.getDefault(), "%.0f", speedKmh));
            speedGraph.addSpeedReading(speedKmh);
            updateHeroCard(speedKmh);
        });
    }

    @Override
    public void onSpeedingDetected(float speedKmh) {
        eventCount++;
        runOnUiThread(() -> tvEventCount.setText(String.valueOf(eventCount)));
    }

    // ─── Hero card color/status update ───────────────────────────────────────

    private void updateHeroCard(float speedKmh) {
        int color;
        String status;
        if (speedKmh > 120) {
            color = Color.parseColor("#C62828"); status = "OVERSPEEDING";
        } else if (speedKmh > 80) {
            color = Color.parseColor("#E65100"); status = "HIGH SPEED";
        } else if (speedKmh > 30) {
            color = Color.parseColor("#2E7D32"); status = "SPEED OK";
        } else {
            color = Color.parseColor("#1565C0"); status = "SLOW / STOPPED";
        }
        cardSpeedHero.setCardBackgroundColor(color);
        tvSpeedStatus.setText(status);
    }

    // ─── End trip ────────────────────────────────────────────────────────────

    private void stopTrip() {
        accelManager.stopListening();
        locationTracker.stopTracking();
        timerHandler.removeCallbacks(timerRunnable);

        int durationSeconds = (int)((SystemClock.elapsedRealtime() - tripStartTime) / 1000);
        int score    = calculateScore(eventCount, durationSeconds);
        String persona = new PersonaManager().getPersona(score);
        String date  = new SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(new Date());

        DatabaseManager db = new DatabaseManager(this);
        long tripId = db.insertTrip(date, durationSeconds, score, selectedMood, eventCount, persona);

        for (double[] p : locationPoints)
            db.insertLocation(tripId, p[0], p[1], (float) p[2]);

        db.updateUserStats(this);

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
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelManager.startListening();
        startTimer();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking();
        }
    }

    public int calculateScore(int eventCount, int durationSeconds) {
        int score = 100 - eventCount * 8;
        if (eventCount == 0 && durationSeconds >= 300) score = 100;
        return Math.max(0, score);
    }
}