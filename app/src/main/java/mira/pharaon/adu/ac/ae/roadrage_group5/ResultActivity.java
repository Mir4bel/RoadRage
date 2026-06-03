package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends ModalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Trip Results");
        }

        // Get everything TripActivity passed us
        int score = getIntent().getIntExtra("score", 0);
        String persona = getIntent().getStringExtra("persona");
        String mood = getIntent().getStringExtra("mood");
        int duration = getIntent().getIntExtra("duration", 0);
        int eventCount = getIntent().getIntExtra("eventCount", 0);
        String date = getIntent().getStringExtra("date");
        boolean crashDetected = getIntent().getBooleanExtra("crashDetected", false);
        long[][] speedSamples = (long[][]) getIntent().getSerializableExtra("speedSamples");

        // Display results
        TextView tvScore   = findViewById(R.id.tv_score);
        TextView tvPersona = findViewById(R.id.tv_persona_result);
        TextView tvMessage = findViewById(R.id.tv_persona_message);
        Button btnDone     = findViewById(R.id.btn_done);
        LineChart speedChart = findViewById(R.id.chart_speed);

        tvScore.setText(String.valueOf(score));
        tvPersona.setText(persona);
        tvPersona.setTextColor(new PersonaManager().getPersonaColorForScore(score));
        tvMessage.setText(new PersonaManager().getPersonaMessage(persona));

        // Plot speed chart if data available
        if (speedSamples != null && speedSamples.length > 0) {
            plotSpeedChart(speedChart, speedSamples);
        }

        // Save trip to database
        DatabaseManager tripDAO   = new DatabaseManager(this);
        long tripId = tripDAO.insertTrip(date, duration, score, mood, eventCount, persona);
        tripDAO.updateUserStats(this);

        // Save a generic event record for the count
        if (eventCount > 0) {
            String timestamp = date;
            tripDAO.insertEvent(tripId, "harsh", eventCount, timestamp);
        }

        // Show crash alert if detected
        if (crashDetected) {
            tvMessage.setText(tvMessage.getText() + "\n\n⚠️ CRASH DETECTED during this trip");
        }

        btnDone.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void plotSpeedChart(LineChart chart, long[][] speedSamples) {
        List<Entry> entries = new ArrayList<>();
        long startTime = speedSamples.length > 0 ? speedSamples[0][0] : 0;

        for (int i = 0; i < speedSamples.length; i++) {
            long elapsedSecs = (speedSamples[i][0] - startTime) / 1000;
            long speed = speedSamples[i][1];
            entries.add(new Entry(elapsedSecs, speed));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Speed (km/h)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark, getTheme()));
        dataSet.setValueTextSize(10f);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setText("Speed over time");
        chart.invalidate();
    }
}