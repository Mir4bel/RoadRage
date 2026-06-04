package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNav(R.id.nav_profile);

        DatabaseManager db = new DatabaseManager(this);

        // ─── Stats ───────────────────────────────────────────────────────────
        TextView tvTrips   = findViewById(R.id.tv_total_trips);
        TextView tvAvg     = findViewById(R.id.tv_profile_avg);
        TextView tvPersona = findViewById(R.id.tv_profile_persona);

        List<String[]> trips = db.getAllTrips();
        tvTrips.setText(String.valueOf(trips.size()));

        String[] stats = db.getUserStats();
        tvAvg.setText(stats[0]);

        PersonaManager pm = new PersonaManager();
        if (!stats[1].equals("Unknown")) {
            tvPersona.setText(pm.getPersonaEmoji(stats[1]) + "  " + stats[1]);
            tvPersona.setTextColor(Color.parseColor(pm.getPersonaColor(stats[1])));
        } else {
            tvPersona.setText("—  No trips yet");
        }

        // Make the persona card tappable — opens the detail screen
        tvPersona.setOnClickListener(v -> {
            String currentPersona = stats[1];
            if (!currentPersona.equals("Unknown")) {
                Intent intent = new Intent(this, PersonaDetailActivity.class);
                intent.putExtra("persona", currentPersona);
                startActivity(intent);
            }
        });


        // Visual hint: underline + clickable indicator
        tvPersona.setPaintFlags(tvPersona.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        // ─── Score trend chart ────────────────────────────────────────────────
        ScoreBarChartView chart = findViewById(R.id.score_chart);
        List<Integer> scores = new ArrayList<>();
        // getAllTrips returns DESC order — chart wants oldest first (ASC)
        for (int i = trips.size() - 1; i >= 0 && i >= trips.size() - 10; i--) {
            scores.add(Integer.parseInt(trips.get(i)[1]));
        }
        chart.setScores(scores);

        // ─── Mood impact ──────────────────────────────────────────────────────
        LinearLayout llMood = findViewById(R.id.ll_mood_impact);
        List<String[]> moodData = db.getAverageScoreByMood();
        if (moodData.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No data yet — complete a trip to see mood impact.");
            empty.setTextColor(getColor(android.R.color.darker_gray));
            llMood.addView(empty);
        } else {
            for (String[] row : moodData) {
                View item = LayoutInflater.from(this)
                        .inflate(R.layout.item_mood_impact, llMood, false);
                ((TextView) item.findViewById(R.id.tv_mood_name)).setText(row[0]);
                ((TextView) item.findViewById(R.id.tv_mood_avg)).setText(row[1]);
                llMood.addView(item);
            }
        }

        // ─── Emergency contact ────────────────────────────────────────────────
        SharedPreferences prefs = getSharedPreferences("roadrage_prefs", MODE_PRIVATE);
        EditText etContact  = findViewById(R.id.et_emergency_contact);
        MaterialButton btnSaveContact = findViewById(R.id.btn_save_contact);

        String existing = prefs.getString("emergency_contact", "");
        if (!existing.isEmpty()) etContact.setText(existing);

        btnSaveContact.setOnClickListener(v -> {
            String number = etContact.getText().toString().trim();
            prefs.edit().putString("emergency_contact", number).apply();
            Toast.makeText(this,
                    number.isEmpty() ? "Emergency contact cleared." : "Saved: " + number,
                    Toast.LENGTH_SHORT).show();
        });

        // ─── Debug button ─────────────────────────────────────────────────────
        MaterialButton btnDebug = findViewById(R.id.btn_debug);
        btnDebug.setOnClickListener(v ->
                startActivity(new Intent(this, DebugActivity.class)));
    }
}