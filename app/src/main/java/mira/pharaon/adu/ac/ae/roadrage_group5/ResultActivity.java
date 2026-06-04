package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score      = getIntent().getIntExtra("score", 0);
        String persona = getIntent().getStringExtra("persona");
        String mood    = getIntent().getStringExtra("mood");

        TextView tvScore   = findViewById(R.id.tv_score);
        TextView tvPersona = findViewById(R.id.tv_persona_result);
        TextView tvMessage = findViewById(R.id.tv_persona_message);
        TextView tvEmoji   = findViewById(R.id.tv_persona_emoji);
        FrameLayout circle = findViewById(R.id.persona_circle);
        Button btnDone     = findViewById(R.id.btn_done);

        PersonaManager pm = new PersonaManager();

        tvScore.setText(String.valueOf(score));
        tvPersona.setText(persona);
        tvMessage.setText(pm.getPersonaMessage(persona));
        tvEmoji.setText(pm.getPersonaEmoji(persona));

        // Color the circle with persona's identity color
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(Color.parseColor(pm.getPersonaColor(persona)));
        circle.setBackground(bg);

        // Color the persona name to match
        tvPersona.setTextColor(Color.parseColor(pm.getPersonaColor(persona)));

        // NOTE: insertTrip and updateUserStats were removed from here.
        // TripActivity now handles all DB saves before navigating to this screen.
        // Calling them here caused duplicate trip entries.

        btnDone.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}