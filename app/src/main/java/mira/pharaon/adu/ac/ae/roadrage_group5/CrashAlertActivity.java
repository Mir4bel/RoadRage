package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CrashAlertActivity extends AppCompatActivity {

    private static final int COUNTDOWN_SECONDS = 10;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_alert);

        boolean isTest   = getIntent().getBooleanExtra("is_test", false);
        float magnitude  = getIntent().getFloatExtra("magnitude", 0f);

        TextView tvCountdown = findViewById(R.id.tv_countdown);
        TextView tvSub       = findViewById(R.id.tv_crash_subtitle);
        Button   btnOk       = findViewById(R.id.btn_im_ok);

        if (isTest) {
            tvSub.setText("Test mode — no message will be sent.\nMagnitude simulated.");
        } else {
            tvSub.setText(String.format("Impact detected: %.1f m/s²\nTap below if you're okay.", magnitude));
        }

        btnOk.setOnClickListener(v -> {
            timer.cancel();
            finish();
        });

        timer = new CountDownTimer(COUNTDOWN_SECONDS * 1000L, 1000) {
            @Override
            public void onTick(long ms) {
                tvCountdown.setText(String.valueOf((int)(ms / 1000)));
            }
            @Override
            public void onFinish() {
                tvCountdown.setText("0");
                if (!isTest) sendEmergencyAlert();
                else {
                    Toast.makeText(CrashAlertActivity.this,
                            "Test complete — alert would have been sent.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }.start();
    }

    private void sendEmergencyAlert() {
        SharedPreferences prefs = getSharedPreferences("roadrage_prefs", MODE_PRIVATE);
        String contact = prefs.getString("emergency_contact", "").trim();

        if (contact.isEmpty()) {
            Toast.makeText(this,
                    "No emergency contact set — add one in Profile.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            SmsManager.getDefault().sendTextMessage(
                    contact, null,
                    "⚠️ CRASH ALERT from RoadRage: A possible crash was detected. " +
                            "If this is an emergency, please call immediately. Reply SAFE if okay.",
                    null, null);
            Toast.makeText(this, "Emergency alert sent to " + contact, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}