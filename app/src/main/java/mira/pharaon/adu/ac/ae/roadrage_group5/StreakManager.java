package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.content.SharedPreferences;

public class StreakManager {

    private static final String PREFS       = "roadrage_streaks";
    private static final String KEY_CURRENT = "streak_current";
    private static final String KEY_BEST    = "streak_best";

    /** Minimum score to continue or start a streak */
    public static final int SAFE_THRESHOLD = 60;

    public static int getCurrent(Context ctx) {
        return prefs(ctx).getInt(KEY_CURRENT, 0);
    }

    public static int getBest(Context ctx) {
        return prefs(ctx).getInt(KEY_BEST, 0);
    }

    /**
     * Call this once after every trip completes.
     * Returns the new current streak value.
     */
    public static int update(Context ctx, int score) {
        int current = prefs(ctx).getInt(KEY_CURRENT, 0);
        int best    = prefs(ctx).getInt(KEY_BEST, 0);

        current = (score >= SAFE_THRESHOLD) ? current + 1 : 0;
        if (current > best) best = current;

        prefs(ctx).edit()
                .putInt(KEY_CURRENT, current)
                .putInt(KEY_BEST, best)
                .apply();
        return current;
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}