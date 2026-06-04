package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Locale;

public class DebugActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvAccelLive, tvDbStats, tvGpsLive;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final Handler handler = new Handler();
    private Runnable periodicRefresh;

    private float[] latestAccel = {0f, 0f, 0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_debug);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvAccelLive = findViewById(R.id.tv_accel_live);
        tvDbStats   = findViewById(R.id.tv_db_stats);
        tvGpsLive   = findViewById(R.id.tv_gps_live);

        populateStaticSections();
        setupActionButtons();

        // Live accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        // Periodic refresh for DB + GPS (every 1s)
        periodicRefresh = new Runnable() {
            @Override
            public void run() {
                refreshDbStats();
                refreshGpsStatus();
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(periodicRefresh);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(periodicRefresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    // ─── Static info (set once) ───────────────────────────────────────────────

    private void populateStaticSections() {
        // Device info
        TextView tvDevice = findViewById(R.id.tv_device_info);
        String version = "unknown";
        try { version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
        catch (Exception ignored) {}

        tvDevice.setText(
                "Manufacturer : " + Build.MANUFACTURER + "\n" +
                        "Model        : " + Build.MODEL + "\n" +
                        "Android      : " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n" +
                        "App Version  : " + version
        );

        // Sensor availability
        TextView tvSensors = findViewById(R.id.tv_sensor_info);
        Sensor accel  = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
        Sensor gyro   = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) : null;
        Sensor gravity= sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) : null;

        tvSensors.setText(
                "Accelerometer : " + (accel   != null ? "✓ " + accel.getName()   : "✗ not available") + "\n" +
                        "Gyroscope     : " + (gyro    != null ? "✓ " + gyro.getName()    : "✗ not available") + "\n" +
                        "Gravity       : " + (gravity != null ? "✓ " + gravity.getName() : "✗ not available")
        );

        // Detection thresholds (so you can verify they match AccelerometerManager)
        TextView tvThresh = findViewById(R.id.tv_thresholds);
        tvThresh.setText(
                "Harsh braking  : Y-axis > 15.0 m/s²\n" +
                        "Sharp turn     : X-axis > 12.0 m/s²\n" +
                        "Speeding       : > 120 km/h\n" +
                        "Crash trigger  : combined > 30.0 m/s²\n" +
                        "Event cooldown : 2 s  |  Crash cooldown : 5 s"
        );

        // Permissions
        TextView tvPerms = findViewById(R.id.tv_permissions);
        int fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int sendSms      = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);
        tvPerms.setText(
                "ACCESS_FINE_LOCATION : " + (fineLocation == PackageManager.PERMISSION_GRANTED ? "✓ Granted" : "✗ Denied") + "\n" +
                        "SEND_SMS             : " + (sendSms      == PackageManager.PERMISSION_GRANTED ? "✓ Granted" : "✗ Denied (needed for crash alerts)")
        );
    }

    // ─── Live refreshes ───────────────────────────────────────────────────────

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        latestAccel = event.values.clone();

        float x   = latestAccel[0], y = latestAccel[1], z = latestAccel[2];
        float mag = (float) Math.sqrt(x*x + y*y + z*z);

        String flag = mag > 30 ? "  ⚠ CRASH ZONE" :
                mag > 15 ? "  ⚡ HARSH EVENT ZONE" : "  ✓ normal";

        tvAccelLive.setText(String.format(Locale.getDefault(),
                "X  :  %+6.2f  m/s²\n" +
                        "Y  :  %+6.2f  m/s²\n" +
                        "Z  :  %+6.2f  m/s²\n\n" +
                        "Magnitude  :  %.2f  m/s²%s\n\n" +
                        "Braking threshold  : Y > 15.0\n" +
                        "Turning threshold  : X > 12.0",
                x, y, z, mag, flag));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void refreshDbStats() {
        DatabaseManager db = new DatabaseManager(this);

        // Count trips
        int trips = db.getAllTrips().size();

        // Count locations and events directly
        SQLiteDatabase raw = db.getReadableDatabase();
        int locations = 0, events = 0;
        try {
            Cursor c1 = raw.rawQuery("SELECT COUNT(*) FROM locations", null);
            if (c1.moveToFirst()) locations = c1.getInt(0); c1.close();

            Cursor c2 = raw.rawQuery("SELECT COUNT(*) FROM events", null);
            if (c2.moveToFirst()) events = c2.getInt(0); c2.close();
        } catch (Exception ignored) {}

        // Top score + avg
        String[] stats = db.getUserStats();

        tvDbStats.setText(
                "Trips          : " + trips + "\n" +
                        "Location points: " + locations + "\n" +
                        "Events logged  : " + events + "\n\n" +
                        "Avg score      : " + stats[0] + "\n" +
                        "Current persona: " + stats[1]
        );
    }

    private void refreshGpsStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netEnabled = lm != null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        tvGpsLive.setText(
                "GPS provider     : " + (gpsEnabled ? "✓ enabled" : "✗ disabled") + "\n" +
                        "Network provider : " + (netEnabled ? "✓ enabled" : "✗ disabled") + "\n\n" +
                        "Note: GPS accuracy varies by device.\nGo outside and start a trip to test."
        );
    }

    // ─── Action buttons ───────────────────────────────────────────────────────

    private void setupActionButtons() {
        MaterialButton btnClear = findViewById(R.id.btn_clear_db);
        btnClear.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Clear All Trip Data?")
                        .setMessage("Permanently deletes all trips, location points, and events. Cannot be undone.")
                        .setPositiveButton("Delete everything", (d, w) -> {
                            clearDatabase();
                            refreshDbStats();
                            Toast.makeText(this, "Database cleared.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        MaterialButton btnCopy = findViewById(R.id.btn_copy_stats);
        btnCopy.setOnClickListener(v -> copyStatsToClipboard());

        MaterialButton btnTestCrash = findViewById(R.id.btn_test_crash);
        btnTestCrash.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, CrashAlertActivity.class);
            intent.putExtra("is_test", true);
            startActivity(intent);
        });
    }

    private void clearDatabase() {
        SQLiteDatabase db = new DatabaseManager(this).getWritableDatabase();
        db.execSQL("DELETE FROM locations");
        db.execSQL("DELETE FROM events");
        db.execSQL("DELETE FROM trips");
        db.execSQL("UPDATE user SET total_trips=0, average_score=0, current_persona='Unknown'");
    }

    private void copyStatsToClipboard() {
        refreshDbStats(); // ensure fresh
        String text =
                "=== RoadRage Debug Export ===\n\n" +
                        tvDbStats.getText() + "\n\n" +
                        tvDevice_text() + "\n\n" +
                        tvAccelLive.getText() + "\n\n" +
                        tvGpsLive.getText();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("RoadRage Debug", text));
        Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    private String tvDevice_text() {
        // Rebuild device string inline for the export
        return "Device: " + Build.MANUFACTURER + " " + Build.MODEL +
                "\nAndroid: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
    }
}