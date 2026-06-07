package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupBottomNav(R.id.nav_history);

        RecyclerView recyclerView = findViewById(R.id.rv_trip_history);
        View emptyLayout = findViewById(R.id.layout_empty);

        DatabaseManager tripDAO = new DatabaseManager(this);
        List<String[]> trips = tripDAO.getAllTrips();

        if (trips.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new TripAdapter(trips, trip -> {

                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("trip_id", Long.parseLong(trip[4]));
                intent.putExtra("trip_date", trip[0]);
                intent.putExtra("trip_score", trip[1]);
                startActivity(intent);
            }));
        }
    }


    interface OnTripClickListener {
        void onTripClick(String[] trip);
    }


    static class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

        private final List<String[]> trips;
        private final OnTripClickListener clickListener;

        TripAdapter(List<String[]> trips, OnTripClickListener listener) {
            this.trips = trips;
            this.clickListener = listener;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvPersona, tvMood, tvScore;
            ViewHolder(View v) {
                super(v);
                tvDate = v.findViewById(R.id.tv_trip_date);
                tvPersona = v.findViewById(R.id.tv_trip_persona);
                tvMood = v.findViewById(R.id.tv_trip_mood);
                tvScore = v.findViewById(R.id.tv_trip_score);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_trip, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String[] trip = trips.get(position);
            holder.tvDate.setText(trip[0]);
            holder.tvScore.setText(trip[1]);
            holder.tvPersona.setText(trip[2]);
            holder.tvMood.setText(trip[3]);
            holder.itemView.setOnClickListener(v -> clickListener.onTripClick(trip));
        }

        @Override
        public int getItemCount() { return trips.size(); }
    }
}