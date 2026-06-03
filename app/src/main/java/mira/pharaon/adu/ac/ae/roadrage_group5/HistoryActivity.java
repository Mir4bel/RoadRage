package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.app.AlertDialog;
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
            showEncouragementDialog();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new TripAdapter(trips));

            // Analyze trips and show helpful suggestion
            analyzeTripsAndSuggest(trips);
        }
    }

    private void analyzeTripsAndSuggest(List<String[]> trips) {
        // Analyze driving patterns
        int totalTrips = trips.size();
        int safeTrips = 0;
        int recklessTrips = 0;

        for (String[] trip : trips) {
            try {
                int score = Integer.parseInt(trip[1]);
                if (score >= 80) safeTrips++;
                else if (score < 40) recklessTrips++;
            } catch (Exception e) {
                // Skip parse error
            }
        }

        String suggestion = "";
        if (safeTrips >= totalTrips / 2) {
            suggestion = "🏆 Great job! You're driving safely most of the time.\n\nChallenge: Try to maintain this for 5 consecutive trips!";
        } else if (recklessTrips >= totalTrips / 2) {
            suggestion = "⚠️ We've noticed some risky driving patterns.\n\nTip: Focus on smoother turns and gradual acceleration. You've got this!";
        } else {
            suggestion = "📈 Your driving is improving! Keep pushing for those Guardian Angel ratings.\n\nHint: Smooth braking = fewer harsh events = better score!";
        }

        new AlertDialog.Builder(this)
            .setTitle("Your Driving Analysis")
            .setMessage(suggestion)
            .setPositiveButton("Got it!", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void showEncouragementDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Start Your Journey!")
            .setMessage("You haven't taken any trips yet.\n\nGo to the Home screen and start your first trip to begin improving your driving!\n\n🚗 Remember: Safe driving is a skill to master.")
            .setPositiveButton("Let's Go!", (dialog, which) -> {
                dialog.dismiss();
                // Navigate to home
            })
            .show();
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