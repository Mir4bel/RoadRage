package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;

public class PersonaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_detail);

        String persona = getIntent().getStringExtra("persona");
        if (persona == null) { finish(); return; }

        PersonaManager pm = new PersonaManager();

        // Back button
        findViewById(R.id.btn_back_persona)
                .setOnClickListener(v -> finish());

        // Colored header background
        FrameLayout header = findViewById(R.id.fl_persona_header);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(pm.getPersonaColor(persona)));
        header.setBackground(bg);

        // Header content
        ((TextView) findViewById(R.id.tv_detail_emoji)).setText(pm.getPersonaEmoji(persona));
        ((TextView) findViewById(R.id.tv_detail_name)).setText(persona);

        Chip chipRange = findViewById(R.id.chip_score_range);
        chipRange.setText(pm.getPersonaRange(persona) + "  pts");

        // Content sections
        ((TextView) findViewById(R.id.tv_overview)).setText(pm.getPersonaOverview(persona));
        ((TextView) findViewById(R.id.tv_traits)).setText(pm.getPersonaTraits(persona));
        ((TextView) findViewById(R.id.tv_habits)).setText(pm.getPersonaHabits(persona));
        ((TextView) findViewById(R.id.tv_vibe)).setText(pm.getPersonaVibe(persona));
        ((TextView) findViewById(R.id.tv_profile_a)).setText(pm.getPersonaProfileA(persona));
        ((TextView) findViewById(R.id.tv_profile_b)).setText(pm.getPersonaProfileB(persona));
    }
}