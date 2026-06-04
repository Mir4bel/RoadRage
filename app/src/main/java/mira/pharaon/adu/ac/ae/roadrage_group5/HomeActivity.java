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

        TextView tvTrips    = findViewById(R.id.tv_total_trips_home);
        TextView tvAvgScore = findViewById(R.id.tv_avg_score);
        Button btnStart     = findViewById(R.id.btn_start_trip);

        DatabaseManager db = new DatabaseManager(this);

        // Total trips
        tvTrips.setText(String.valueOf(db.getAllTrips().size()));

        // Average score
        String[] stats = db.getUserStats();
        tvAvgScore.setText(stats[0]);

        btnStart.setOnClickListener(v ->
                startActivity(new Intent(this, MoodActivity.class)));
    }
}