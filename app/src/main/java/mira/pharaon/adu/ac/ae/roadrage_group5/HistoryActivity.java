package mira.pharaon.adu.ac.ae.roadrage_group5;

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
        View emptyLayout          = findViewById(R.id.layout_empty);

        DatabaseManager tripDAO = new DatabaseManager(this);
        List<String[]> trips = tripDAO.getAllTrips();

        if (trips.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new TripAdapter(trips));
        }
    }

    // Adapter defined inside HistoryActivity for simplicity
    static class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

        private List<String[]> trips;

        TripAdapter(List<String[]> trips) {
            this.trips = trips;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvPersona, tvMood, tvScore;
            ViewHolder(View v) {
                super(v);
                tvDate    = v.findViewById(R.id.tv_trip_date);
                tvPersona = v.findViewById(R.id.tv_trip_persona);
                tvMood    = v.findViewById(R.id.tv_trip_mood);
                tvScore   = v.findViewById(R.id.tv_trip_score);
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
            // trip[0]=date, trip[1]=score, trip[2]=persona, trip[3]=mood
            holder.tvDate.setText(trip[0]);
            holder.tvScore.setText(trip[1]);
            holder.tvPersona.setText(trip[2]);
            holder.tvMood.setText(trip[3]);
        }

        @Override
        public int getItemCount() { return trips.size(); }
    }
}