package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNav(R.id.nav_profile);

        TextView tvTotalTrips  = findViewById(R.id.tv_total_trips);
        TextView tvAvg         = findViewById(R.id.tv_profile_avg);
        TextView tvPersona     = findViewById(R.id.tv_profile_persona);
        LinearLayout moodContainer = findViewById(R.id.ll_mood_impact);

        DatabaseManager tripDAO = new DatabaseManager(this);

        // Basic stats
        String[] stats = tripDAO.getUserStats();
        tvAvg.setText(stats[0]);     // average score
        tvPersona.setText(stats[1]); // persona

        // Total trip count
        List<String[]> allTrips = tripDAO.getAllTrips();
        tvTotalTrips.setText(String.valueOf(allTrips.size()));

        // Mood impact rows
        List<String[]> moodStats = tripDAO.getAverageScoreByMood();

        if (moodStats.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Complete some trips to see mood impact");
            tv.setTextSize(13);
            tv.setTextColor(getColor(android.R.color.darker_gray));
            moodContainer.addView(tv);
        } else {
            for (String[] row : moodStats) {
                // Each row gets a card
                MaterialCardView card = new MaterialCardView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12);
                card.setLayoutParams(params);
                card.setRadius(16);
                card.setCardElevation(0);
                card.setStrokeWidth(2);

                LinearLayout inner = new LinearLayout(this);
                inner.setOrientation(LinearLayout.HORIZONTAL);
                inner.setPadding(40, 32, 40, 32);

                TextView tvMood = new TextView(this);
                tvMood.setText(row[0]); // mood name
                tvMood.setTextSize(15);
                tvMood.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView tvScore = new TextView(this);
                tvScore.setText("avg " + row[1]); // avg score
                tvScore.setTextSize(15);
                tvScore.setTextColor(getColor(android.R.color.holo_blue_dark));

                inner.addView(tvMood);
                inner.addView(tvScore);
                card.addView(inner);
                moodContainer.addView(card);
            }
        }
    }
}