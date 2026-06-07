package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerManager implements SensorEventListener {

    private static final float BRAKE_THRESHOLD = 15.0f;
    private static final float TURN_THRESHOLD  = 12.0f;
    private static final float CRASH_THRESHOLD_M_S2 = 30.0f;

    // one event max every 2 seconds
    private static final long COOLDOWN_MS = 2000;
    private long lastEventTime = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private AccelerometerEventListener eventListener;

    public interface CrashListener { void onCrashDetected(float magnitude); }
    private CrashListener crashListener;
    private boolean crashCooldown = false;
    private final android.os.Handler crashHandler = new android.os.Handler();

    public void setCrashListener(CrashListener l) { this.crashListener = l; }

    public interface AccelerometerEventListener {
        void onHarshEvent(String type, float severity);
    }

    public AccelerometerManager(Context context, AccelerometerEventListener listener) {
        this.eventListener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void startListening() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long now = System.currentTimeMillis();
        if (now - lastEventTime < COOLDOWN_MS) return;

        if (Math.abs(y) > BRAKE_THRESHOLD) {
            lastEventTime = now;
            eventListener.onHarshEvent("brake", Math.abs(y));
        } else if (Math.abs(x) > TURN_THRESHOLD) {
            lastEventTime = now;
            eventListener.onHarshEvent("turn", Math.abs(x));
        }

        float mag = (float) Math.sqrt(x*x + y*y + z*z);
        if (mag > CRASH_THRESHOLD_M_S2 && !crashCooldown && crashListener != null) {
            crashCooldown = true;
            crashListener.onCrashDetected(mag);
            crashHandler.postDelayed(() -> crashCooldown = false, 5000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}