package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("score", 0);
        String persona = getIntent().getStringExtra("persona");
        String mood = getIntent().getStringExtra("mood");
        int duration = getIntent().getIntExtra("duration", 0);
        int eventCount = getIntent().getIntExtra("eventCount", 0);
        String date = getIntent().getStringExtra("date");
        long tripId = getIntent().getLongExtra("tripId", -1);

        PersonaManager pm = new PersonaManager();

        ((TextView) findViewById(R.id.tv_score)).setText(String.valueOf(score));
        ((TextView) findViewById(R.id.tv_persona_result)).setText(persona);
        ((TextView) findViewById(R.id.tv_persona_message)).setText(pm.getPersonaMessage(persona));
        ((TextView) findViewById(R.id.tv_persona_emoji)).setText(pm.getPersonaEmoji(persona));

        FrameLayout circle = findViewById(R.id.persona_circle);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(Color.parseColor(pm.getPersonaColor(persona)));
        circle.setBackground(bg);

        ((TextView) findViewById(R.id.tv_persona_result))
                .setTextColor(Color.parseColor(pm.getPersonaColor(persona)));

        int  min = duration / 60, sec = duration % 60;
        ((TextView) findViewById(R.id.tv_duration))
                .setText(String.format(Locale.getDefault(), "%d:%02d", min, sec));
        ((TextView) findViewById(R.id.tv_events_result))
                .setText(String.valueOf(eventCount));

        int newStreak = StreakManager.update(this, score);


        DatabaseManager db = new DatabaseManager(this);
        List<AchievementManager.Achievement> unlocked =
                AchievementManager.checkAndUnlock(this, db, score, mood, eventCount, newStreak);


        ((TextView) findViewById(R.id.tv_coaching))
                .setText(coachingMessage(score, eventCount));


        TextView tvStreak = findViewById(R.id.tv_streak_badge);
        if (score >= StreakManager.SAFE_THRESHOLD) {
            if (newStreak == 1) {
                tvStreak.setText("Streak started — keep it going!");
            } else {
                tvStreak.setText("🔥 " + newStreak + "-trip streak");
            }
            tvStreak.setTextColor(Color.parseColor("#E65100")); // warm orange
        } else {
            if (newStreak == 0 && StreakManager.getBest(this) > 0) {
                tvStreak.setText("Streak ended. Score 60+ to start a new one.");
                tvStreak.setTextColor(Color.parseColor("#9E9E9E"));
            } else {
                tvStreak.setVisibility(View.GONE);
            }
        }


        if (!unlocked.isEmpty()) {
            View cardAch = findViewById(R.id.card_achievements);
            LinearLayout ll = findViewById(R.id.ll_new_achievements);
            cardAch.setVisibility(View.VISIBLE);

            for (AchievementManager.Achievement a : unlocked) {
                View row = LayoutInflater.from(this)
                        .inflate(R.layout.item_achievement_unlock_row, ll, false);
                ((TextView) row.findViewById(R.id.tv_ach_emoji)).setText(a.emoji);
                ((TextView) row.findViewById(R.id.tv_ach_title)).setText(a.title);
                ((TextView) row.findViewById(R.id.tv_ach_desc)).setText(a.description);
                ll.addView(row);
            }
        }


        View btnRoute = findViewById(R.id.btn_view_route);
        if (tripId == -1) {
            btnRoute.setVisibility(View.GONE);
        } else {
            btnRoute.setOnClickListener(v -> {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("trip_id", tripId);
                intent.putExtra("trip_date", date);
                intent.putExtra("trip_score", String.valueOf(score));
                startActivity(intent);
            });
        }

        findViewById(R.id.btn_done).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private String coachingMessage(int score, int eventCount) {
        if (score == 100) return "Flawless. Zero events, total control. Set this as the standard.";
        if (eventCount == 0) return "Clean trip. Smooth all the way through.";

        String ev = eventCount + " harsh event" + (eventCount == 1 ? "" : "s") + " detected. ";

        if (score >= 80) return ev + "Strong drive overall — small rough patches but mostly clean.";
        if (score >= 60) return ev + "Each event costs 8 points. More following distance prevents most sudden braking.";
        if (score >= 40) return ev + "Scan further ahead — most events happen when drivers react late to traffic.";
        return ev + "Focus on smooth inputs: gentle braking, early turns, steady speed.";
    }
}
