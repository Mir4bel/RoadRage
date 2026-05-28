package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Navigate directly to home and finish this activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}