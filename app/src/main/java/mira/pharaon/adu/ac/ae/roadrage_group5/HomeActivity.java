package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNav(R.id.nav_home);

        TextView tvPersona  = findViewById(R.id.tv_persona);
        TextView tvAvgScore = findViewById(R.id.tv_avg_score);
        Button   btnStart   = findViewById(R.id.btn_start_trip);

        DatabaseManager db = new DatabaseManager(this);
        String[] stats = db.getUserStats(); // [0]=avgScore, [1]=persona

        tvAvgScore.setText(stats[0]);

        if (!stats[1].equals("Unknown") && !stats[1].isEmpty()) {
            PersonaManager pm = new PersonaManager();
            tvPersona.setText(pm.getPersonaEmoji(stats[1]) + "  " + stats[1]);
            tvPersona.setTextColor(Color.parseColor(pm.getPersonaColor(stats[1])));
        } else {
            tvPersona.setText("—");
        }

        btnStart.setOnClickListener(v ->
                startActivity(new Intent(this, MoodActivity.class)));
    }
}