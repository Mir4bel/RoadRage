package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get trip data from intent if provided
        String tripId = getIntent().getStringExtra("trip_id");

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                // TODO: Load trip route from LocationTracker or database
                // For now, just initialize the map
                if (mMap != null) {
                    // Default to Dubai, UAE
                    com.google.android.gms.maps.model.LatLng dubai =
                            new com.google.android.gms.maps.model.LatLng(25.2048, 55.2708);
                    mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(dubai, 12));
                }
            });
        }
    }
}

