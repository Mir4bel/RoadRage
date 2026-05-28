package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MoodActivity extends AppCompatActivity {

    private String selectedMood = "Neutral"; // default
    private MaterialButton lastSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        MaterialButton btnHappy    = findViewById(R.id.btn_mood_happy);
        MaterialButton btnNeutral  = findViewById(R.id.btn_mood_neutral);
        MaterialButton btnTired    = findViewById(R.id.btn_mood_tired);
        MaterialButton btnStressed = findViewById(R.id.btn_mood_stressed);
        Button btnConfirm          = findViewById(R.id.btn_confirm_mood);

        // When a mood is tapped, highlight it and remember the selection
        btnHappy.setOnClickListener(v    -> selectMood(btnHappy, "Happy"));
        btnNeutral.setOnClickListener(v  -> selectMood(btnNeutral, "Neutral"));
        btnTired.setOnClickListener(v    -> selectMood(btnTired, "Tired"));
        btnStressed.setOnClickListener(v -> selectMood(btnStressed, "Stressed"));

        btnConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(this, TripActivity.class);
            intent.putExtra("mood", selectedMood);
            startActivity(intent);
            finish(); // prevent going back to mood screen mid-trip
        });
    }

    private void selectMood(MaterialButton button, String mood) {
        // Deselect previous button
        if (lastSelected != null) {
            lastSelected.setStrokeWidth(2);
        }
        // Highlight new selection with a thicker stroke
        button.setStrokeWidth(6);
        selectedMood = mood;
        lastSelected = button;
    }
}