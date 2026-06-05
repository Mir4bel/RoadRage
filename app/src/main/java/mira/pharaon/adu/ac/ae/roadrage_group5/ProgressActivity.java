package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class ProgressActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        setupBottomNav(R.id.nav_progress);

        DatabaseManager db = new DatabaseManager(this);

        buildStreakSection();
        buildWeeklySection(db);
        buildLeaderboard(db);
        buildAchievements();
    }

    // ─── Streak ───────────────────────────────────────────────────────────────

    private void buildStreakSection() {
        int current = StreakManager.getCurrent(this);
        int best    = StreakManager.getBest(this);

        ((TextView) findViewById(R.id.tv_streak_current)).setText(String.valueOf(current));
        ((TextView) findViewById(R.id.tv_streak_best)).setText(String.valueOf(best));
    }

    // ─── Weekly comparison ────────────────────────────────────────────────────

    private void buildWeeklySection(DatabaseManager db) {
        DatabaseManager.WeeklyStats thisWeek = db.getWeeklyStats(true);
        DatabaseManager.WeeklyStats lastWeek = db.getWeeklyStats(false);

        TextView tvAvg   = findViewById(R.id.tv_week_avg);
        TextView tvTrips = findViewById(R.id.tv_week_trips);
        TextView tvVs    = findViewById(R.id.tv_week_vs);

        if (thisWeek.tripCount == 0) {
            tvAvg.setText("—");
            tvTrips.setText("0");
            tvVs.setText("No trips yet this week");
            tvVs.setTextColor(getColor(android.R.color.darker_gray));
            return;
        }

        tvAvg.setText(String.valueOf(thisWeek.avgScore));
        tvTrips.setText(String.valueOf(thisWeek.tripCount));

        if (lastWeek.tripCount == 0) {
            tvVs.setText("First week with data 🎉");
            tvVs.setTextColor(Color.parseColor("#00695C"));
        } else {
            int diff = thisWeek.avgScore - lastWeek.avgScore;
            if (diff > 0) {
                tvVs.setText("↑ " + diff + " pts vs last week");
                tvVs.setTextColor(Color.parseColor("#00695C"));
            } else if (diff < 0) {
                tvVs.setText("↓ " + Math.abs(diff) + " pts vs last week");
                tvVs.setTextColor(Color.parseColor("#B71C1C"));
            } else {
                tvVs.setText("Same as last week");
                tvVs.setTextColor(getColor(android.R.color.darker_gray));
            }
        }
    }

    // ─── Leaderboard ──────────────────────────────────────────────────────────

    private void buildLeaderboard(DatabaseManager db) {
        LinearLayout container = findViewById(R.id.ll_leaderboard);
        List<String[]> top = db.getTopTrips(10);
        PersonaManager pm = new PersonaManager();

        if (top.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Complete trips to see your personal records here.");
            empty.setTextColor(getColor(android.R.color.darker_gray));
            empty.setTextSize(13f);
            container.addView(empty);
            return;
        }

        for (int i = 0; i < top.size(); i++) {
            String[] trip   = top.get(i);
            int      rank   = i + 1;
            int      score  = Integer.parseInt(trip[1]);
            String   date   = trip[0];
            String   persona = trip[2];

            View row = LayoutInflater.from(this)
                    .inflate(R.layout.item_leaderboard_row, container, false);

            TextView tvRank    = row.findViewById(R.id.tv_rank);
            TextView tvScore   = row.findViewById(R.id.tv_lb_score);
            TextView tvDate    = row.findViewById(R.id.tv_lb_date);
            TextView tvPersona = row.findViewById(R.id.tv_lb_persona);

            // Medal colors for top 3
            tvRank.setText(rankLabel(rank));
            if (rank == 1) tvRank.setTextColor(Color.parseColor("#FFD700")); // gold
            else if (rank == 2) tvRank.setTextColor(Color.parseColor("#A8A9AD")); // silver
            else if (rank == 3) tvRank.setTextColor(Color.parseColor("#CD7F32")); // bronze
            else tvRank.setTextColor(Color.parseColor("#9E9E9E"));

            tvScore.setText(String.valueOf(score));
            tvScore.setTextColor(Color.parseColor(pm.getPersonaColor(persona)));
            tvDate.setText(date);
            tvPersona.setText(pm.getPersonaEmoji(persona));

            container.addView(row);

            // Divider between rows (not after last)
            if (i < top.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                lp.setMargins(0, dpToPx(4), 0, dpToPx(4));
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(Color.argb(40, 128, 128, 128));
                container.addView(divider);
            }
        }
    }

    private String rankLabel(int rank) {
        if (rank == 1) return "🥇";
        if (rank == 2) return "🥈";
        if (rank == 3) return "🥉";
        return rank + ".";
    }

    // ─── Achievements grid ────────────────────────────────────────────────────

    private void buildAchievements() {
        LinearLayout gridContainer = findViewById(R.id.ll_achievements_grid);
        List<AchievementManager.Achievement> achievements = AchievementManager.getAll(this);
        int unlocked = AchievementManager.getUnlockedCount(this);

        ((TextView) findViewById(R.id.tv_achievement_count))
                .setText(unlocked + " / " + achievements.size());

        // Build 3-column rows
        LinearLayout currentRow = null;
        for (int i = 0; i < achievements.size(); i++) {
            if (i % 3 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, dpToPx(8));
                currentRow.setLayoutParams(rowParams);
                gridContainer.addView(currentRow);
            }

            View badge = LayoutInflater.from(this)
                    .inflate(R.layout.item_achievement, currentRow, false);

            AchievementManager.Achievement a = achievements.get(i);
            FrameLayout circle = badge.findViewById(R.id.badge_circle);
            TextView tvEmoji   = badge.findViewById(R.id.badge_emoji);
            TextView tvTitle   = badge.findViewById(R.id.badge_title);

            tvEmoji.setText(a.emoji);
            tvTitle.setText(a.title);

            // Circle background
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);

            if (a.unlocked) {
                bg.setColor(Color.parseColor("#E8F5E9")); // light green
                circle.setAlpha(1f);
            } else {
                bg.setColor(Color.parseColor("#F5F5F5")); // grey
                circle.setAlpha(0.45f);
                tvTitle.setAlpha(0.45f);
            }
            circle.setBackground(bg);

            // Tooltip on long press
            badge.setOnLongClickListener(v -> {
                android.widget.Toast.makeText(this,
                        a.title + ": " + a.description, android.widget.Toast.LENGTH_SHORT).show();
                return true;
            });

            currentRow.addView(badge);
        }

        // Fill incomplete last row with empty spacers
        if (achievements.size() % 3 != 0 && currentRow != null) {
            int remaining = 3 - (achievements.size() % 3);
            for (int i = 0; i < remaining; i++) {
                View spacer = new View(this);
                LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                spacer.setLayoutParams(sp);
                currentRow.addView(spacer);
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}