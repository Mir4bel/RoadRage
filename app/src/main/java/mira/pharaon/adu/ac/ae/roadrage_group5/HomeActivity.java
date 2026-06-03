package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import androidx.core.content.ContextCompat;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        setupBottomNav(R.id.nav_home);

        TextView tvPersona = findViewById(R.id.tv_persona);
        TextView tvAvgScore = findViewById(R.id.tv_avg_score);
        Button btnStart = findViewById(R.id.btn_start_trip);
        View personaIndicator = findViewById(R.id.persona_color_indicator);

        // Load stats from database
        DatabaseManager tripDAO = new DatabaseManager(this);
        String[] stats = tripDAO.getUserStats();

        tvAvgScore.setText(stats[0]);
        tvPersona.setText(stats[1]);

        // Set persona color indicator
        try {
            int score = Integer.parseInt(stats[0]);
            int personaColor = new PersonaManager().getPersonaColorForScore(score);
            if (personaIndicator != null) {
                personaIndicator.setBackgroundColor(personaColor);
            }
        } catch (Exception e) {
            // Default color
        }

        btnStart.setOnClickListener(v ->
                startActivity(new Intent(this, MoodActivity.class))
        );
    }
}