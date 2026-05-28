package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get everything TripActivity passed us
        int score = getIntent().getIntExtra("score", 0);
        String persona = getIntent().getStringExtra("persona");
        String mood = getIntent().getStringExtra("mood");
        int duration = getIntent().getIntExtra("duration", 0);
        int eventCount = getIntent().getIntExtra("eventCount", 0);
        String date = getIntent().getStringExtra("date");

        // Display results
        TextView tvScore   = findViewById(R.id.tv_score);
        TextView tvPersona = findViewById(R.id.tv_persona_result);
        TextView tvMessage = findViewById(R.id.tv_persona_message);
        Button btnDone     = findViewById(R.id.btn_done);

        tvScore.setText(String.valueOf(score));
        tvPersona.setText(persona);
        tvMessage.setText(new PersonaManager().getPersonaMessage(persona));

        // Save trip to database
        DatabaseManager tripDAO   = new DatabaseManager(this);
        long tripId = tripDAO.insertTrip(date, duration, score, mood, eventCount, persona);
        tripDAO.updateUserStats(this);

        // Save a generic event record for the count
        // (detailed per-event saving would need more data passed from TripActivity)
        if (eventCount > 0) {
            String timestamp = date;
            tripDAO.insertEvent(tripId, "harsh", eventCount, timestamp);
        }

        btnDone.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}