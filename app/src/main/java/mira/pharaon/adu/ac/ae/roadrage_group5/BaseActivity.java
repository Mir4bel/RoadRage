package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNav(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) return true;

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
            } else if (id == R.id.nav_progress) {
                startActivity(new Intent(this, ProgressActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}