package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class MoodActivity extends AppCompatActivity {

    private String currentMood  = MoodWheelView.MOOD_NAMES[0]; // "Happy" default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_mood);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvTitle    = findViewById(R.id.tv_mood_title);
        TextView tvEmoji    = findViewById(R.id.tv_selected_emoji);
        TextView tvLabel    = findViewById(R.id.tv_mood_label);
        MoodWheelView wheel = findViewById(R.id.mood_wheel);
        MaterialButton btnSave = findViewById(R.id.btn_save_mood);

        // "How are you feeling right now?" — highlight the word "feeling"
        String full = "How are you feeling right now?";
        SpannableString ss = new SpannableString(full);
        int s = full.indexOf("feeling"), e = s + 4;
        int accentColor = getColor(R.color.md_theme_tertiary);
        ss.setSpan(new ForegroundColorSpan(accentColor), s, e, 0);
        ss.setSpan(new StyleSpan(Typeface.BOLD), s, e, 0);
        tvTitle.setText(ss);

        // Set initial display state
        tvEmoji.setText(MoodWheelView.EMOJIS[0]);
        tvLabel.setText("I'm Feeling " + MoodWheelView.MOOD_NAMES[0]);

        // Update display whenever the wheel moves
        wheel.setOnMoodSelectedCallback((index, moodName, emoji) -> {
            currentMood = moodName;
            tvEmoji.setText(emoji);
            tvLabel.setText("I'm Feeling " + moodName);
        });

        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(this, TripActivity.class);
            intent.putExtra("mood", currentMood);
            startActivity(intent);
            finish();
        });
    }
}