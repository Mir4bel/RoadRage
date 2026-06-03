package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Every activity that should show the bottom nav extends this instead of AppCompatActivity
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Call this after setContentView() in each activity
    // Pass the id of the current screen so the correct tab is highlighted
    protected void setupBottomNav(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav == null) return;

        // Apply color state list to text and icons
        bottomNav.setItemTextColor(
            androidx.core.content.ContextCompat.getColorStateList(this, R.color.nav_item_color));
        bottomNav.setItemIconTintList(
            androidx.core.content.ContextCompat.getColorStateList(this, R.color.nav_item_color));

        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) return true; // already here

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            overridePendingTransition(0, 0); // no animation between tabs
            finish();
            return true;
        });
    }
}