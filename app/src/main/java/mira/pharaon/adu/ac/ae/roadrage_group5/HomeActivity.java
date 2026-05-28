package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNav(R.id.nav_home);

        TextView tvPersona = findViewById(R.id.tv_persona);
        TextView tvAvgScore = findViewById(R.id.tv_avg_score);
        Button btnStart = findViewById(R.id.btn_start_trip);

        // Load stats from database
        DatabaseManager tripDAO = new DatabaseManager(this);
        String[] stats = tripDAO.getUserStats();
        // stats[0] = average score, stats[1] = persona
        tvAvgScore.setText(stats[0]);
        tvPersona.setText(stats[1]);

        btnStart.setOnClickListener(v ->
                startActivity(new Intent(this, MoodActivity.class))
        );
    }
}