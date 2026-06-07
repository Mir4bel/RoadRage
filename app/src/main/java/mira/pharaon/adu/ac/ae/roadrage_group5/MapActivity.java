package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";

    private GoogleMap mMap;

    private static final LatLng DEFAULT_UAE = new LatLng(25.2048, 55.2708);
    private static final int DEFAULT_ZOOM = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        MaterialToolbar toolbar = findViewById(R.id.toolbar_map);
        toolbar.setNavigationOnClickListener(v -> finish());


        long tripId = getIntent().getLongExtra("trip_id", -1);
        String tripDate = getIntent().getStringExtra("trip_date");
        String tripScore = getIntent().getStringExtra("trip_score");

        if (tripDate != null && tripScore != null) {
            toolbar.setSubtitle(tripDate + "   Score: " + tripScore);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            Log.e(TAG, "Map fragment not found in activity_map.xml");
            return;
        }

        mapFragment.getMapAsync(googleMap -> {
            Log.d(TAG, "GoogleMap is ready for tripId=" + tripId);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if (tripId != -1) {
                loadTripRoute(tripId);
            } else {
                showNoData();
            }
        });
    }


    private void loadTripRoute(long tripId) {
        findViewById(R.id.card_no_route).setVisibility(View.GONE);
        findViewById(R.id.card_legend).setVisibility(View.GONE);

        DatabaseManager db = new DatabaseManager(this);
        List<double[]> points = db.getLocationsForTrip(tripId);
        Log.d(TAG, "Loaded " + points.size() + " route points for tripId=" + tripId);

        if (points.isEmpty()) {
            Log.w(TAG, "No route points found for tripId=" + tripId);
            showNoData();
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();



        for (int i = 0; i < points.size() - 1; i++) {
            LatLng from  = new LatLng(points.get(i)[0], points.get(i)[1]);
            LatLng to    = new LatLng(points.get(i + 1)[0], points.get(i + 1)[1]);
            float  speed = (float) points.get(i)[2];

            mMap.addPolyline(new PolylineOptions()
                    .add(from, to)
                    .width(12f)
                    .color(speedToColor(speed))
                    .geodesic(true));

            boundsBuilder.include(from);
        }


        LatLng lastPoint = new LatLng(
                points.get(points.size() - 1)[0],
                points.get(points.size() - 1)[1]);
        boundsBuilder.include(lastPoint);


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(points.get(0)[0], points.get(0)[1]))
                .title("Trip Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        mMap.addMarker(new MarkerOptions()
                .position(lastPoint)
                .title("Trip End")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        LatLngBounds bounds = boundsBuilder.build();
        findViewById(R.id.map).post(() -> {
            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
            } catch (IllegalStateException e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(points.get(0)[0], points.get(0)[1]), 17));
            }
        });

        findViewById(R.id.card_legend).setVisibility(View.VISIBLE);
    }


    private int speedToColor(float speedKmh) {
        if (speedKmh > 100) return Color.parseColor("#F44336"); // red
        if (speedKmh > 60)  return Color.parseColor("#FF9800"); // orange
        if (speedKmh > 30)  return Color.parseColor("#4CAF50"); // green
        return                     Color.parseColor("#2196F3"); // blue
    }

    private void showNoData() {
        findViewById(R.id.card_legend).setVisibility(View.GONE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_UAE, DEFAULT_ZOOM));
        findViewById(R.id.card_no_route).setVisibility(View.VISIBLE);
    }
}
