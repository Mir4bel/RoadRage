package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        TextView debugInfo = findViewById(R.id.tv_debug_info);

        // Gather device and app info
        StringBuilder info = new StringBuilder();
        info.append("=== DEBUG INFORMATION ===\n\n");

        // Device info
        info.append("Device: ").append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append("\n");
        info.append("Android: ").append(Build.VERSION.RELEASE)
                .append(" (API ").append(Build.VERSION.SDK_INT).append(")\n\n");

        // App version
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            info.append("App Version: ").append(version).append("\n");
        } catch (Exception e) {
            info.append("App Version: Error\n");
        }

        // Database info
        DatabaseManager db = new DatabaseManager(this);
        int tripCount = db.getAllTrips().size();
        info.append("Total Trips: ").append(tripCount).append("\n\n");

        // Sensor availability
        info.append("=== SENSORS ===\n");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            info.append("Accelerometer: ").append(accel != null ? "✓ Available" : "✗ Not Available").append("\n");

            Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            info.append("Gyroscope: ").append(gyro != null ? "✓ Available" : "✗ Not Available").append("\n");
        }

        // Location Services check
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        info.append("GPS Enabled: ").append(gpsEnabled ? "✓ Yes" : "✗ No").append("\n");

        // Permissions
        info.append("\n=== PERMISSIONS ===\n");
        int fineLocationPerm = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        info.append("Fine Location: ").append(fineLocationPerm == PackageManager.PERMISSION_GRANTED ? "✓ Granted" : "✗ Denied").append("\n");

        debugInfo.setText(info.toString());
    }
}
