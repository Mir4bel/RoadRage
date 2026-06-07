package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNav(R.id.nav_home);


        findViewById(R.id.btn_start_trip).setOnClickListener(v ->
                startActivity(new Intent(this, MoodActivity.class)));


        SharedPreferences prefs = getSharedPreferences("roadrage_prefs", MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        MaterialCardView cardTip   = findViewById(R.id.card_tip);
        MaterialCardView cardStats = findViewById(R.id.card_stats);

        cardTip.setVisibility(View.VISIBLE);
        cardStats.setVisibility(View.GONE);

        TextView tvTip    = findViewById(R.id.tv_tip_text);
        tvTip.setText(DrivingTips.getTodaysTip());

        ImageButton btnDismiss = findViewById(R.id.btn_dismiss_tip);
        btnDismiss.setOnClickListener(v -> {
            prefs.edit().putString("tip_dismissed_date", today).apply();
            cardTip.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                cardTip.setVisibility(View.GONE);
                cardStats.setAlpha(0f);
                cardStats.setVisibility(View.VISIBLE);
                cardStats.animate().alpha(1f).setDuration(200).start();
            }).start();
        });


        loadStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        TextView tvPersona  = findViewById(R.id.tv_persona);
        TextView tvAvgScore = findViewById(R.id.tv_avg_score);
        TextView tvNextTier = findViewById(R.id.tv_next_tier);
        TextView tvStreak   = findViewById(R.id.tv_home_streak);

        DatabaseManager db = new DatabaseManager(this);
        String[] stats     = db.getUserStats();
        PersonaManager pm  = new PersonaManager();

        tvAvgScore.setText(stats[0]);

        if (!stats[1].equals("Unknown") && !stats[1].isEmpty()) {
            tvPersona.setText(pm.getPersonaEmoji(stats[1]) + "  " + stats[1]);
            tvPersona.setTextColor(Color.parseColor(pm.getPersonaColor(stats[1])));
            try {
                int avg = Integer.parseInt(stats[0]);
                tvNextTier.setText(progressMessage(avg));
                tvNextTier.setVisibility(View.VISIBLE);
            } catch (NumberFormatException e) {
                tvNextTier.setVisibility(View.GONE);
            }
        } else {
            tvPersona.setText("—");
            tvNextTier.setVisibility(View.GONE);
        }

        int streak = StreakManager.getCurrent(this);
        if (streak > 0) {
            tvStreak.setText("🔥 " + streak + " trip streak");
            tvStreak.setVisibility(View.VISIBLE);
        } else {
            tvStreak.setVisibility(View.GONE);
        }
    }

    private String progressMessage(int score) {
        if (score < 40) return (40 - score) + " pts to Kangaroo 🦘";
        if (score < 60) return (60 - score) + " pts to Beaver 🦫";
        if (score < 80) return (80 - score) + " pts to Cheetah 🐆";
        return "🏆 Peak performance!";
    }
}