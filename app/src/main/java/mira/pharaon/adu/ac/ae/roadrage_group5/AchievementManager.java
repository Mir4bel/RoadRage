package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class AchievementManager {

    private static final String PREFS = "roadrage_achievements";

    public static final String FIRST_TRIP = "first_trip";
    public static final String TRIPS_10 = "trips_10";
    public static final String TRIPS_25 = "trips_25";
    public static final String TRIPS_50 = "trips_50";
    public static final String SCORE_60 = "score_60";
    public static final String SCORE_80 = "score_80";
    public static final String PERFECT = "perfect_100";
    public static final String CLEAN_TRIP = "clean_trip";
    public static final String STREAK_3 = "streak_3";
    public static final String STREAK_7 = "streak_7";
    public static final String STREAK_20 = "streak_20";
    public static final String HAPPY_HIGH = "happy_high";
    public static final String CHEETAH_X3 = "cheetah_x3";
    public static final String CLEAN_X3 = "clean_x3";
    public static final String IMPROVED = "improved_10";

    public static class Achievement {
        public final String id;
        public final String emoji;
        public final String title;
        public final String description;
        public boolean unlocked;

        Achievement(String id, String emoji, String title, String description) {
            this.id = id; this.emoji = emoji;
            this.title = title; this.description = description;
        }
    }

    /** Returns all achievements with their current locked/unlocked state. */
    public static List<Achievement> getAll(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        Achievement[] defs = {
                new Achievement(FIRST_TRIP, "🛞", "First Ride",       "Complete your first trip"),
                new Achievement(TRIPS_10,   "🔟", "Road Regular",     "Complete 10 trips"),
                new Achievement(TRIPS_25,   "📍", "Explorer",         "Complete 25 trips"),
                new Achievement(TRIPS_50,   "🏁", "Road Warrior",     "Complete 50 trips"),
                new Achievement(SCORE_60,   "⭐", "Rising Star",      "Score 60 or above on a trip"),
                new Achievement(SCORE_80,   "🥇", "Gold Standard",    "Score 80 or above on a trip"),
                new Achievement(PERFECT,    "💎", "Perfect Run",      "Score 100 on a trip"),
                new Achievement(CLEAN_TRIP, "🧘", "Zen Commute",      "Complete a trip with zero harsh events"),
                new Achievement(STREAK_3,   "🔥", "On a Roll",        "3-trip streak (score ≥ 60 each)"),
                new Achievement(STREAK_7,   "🚀", "Hot Streak",       "7-trip streak"),
                new Achievement(STREAK_20,  "👑", "Legend",           "20-trip streak"),
                new Achievement(HAPPY_HIGH, "😊", "Good Vibes",       "Score 75+ while in a Happy mood"),
                new Achievement(CHEETAH_X3, "🐆", "Cheetah Mode",     "Score 80+ on 3 consecutive trips"),
                new Achievement(CLEAN_X3,   "🌟", "Three Peat",       "Zero harsh events, 3 trips in a row"),
                new Achievement(IMPROVED,   "📈", "Getting Better",   "Improve average score by 10+ points"),
        };

        List<Achievement> list = new ArrayList<>();
        for (Achievement a : defs) {
            a.unlocked = p.getBoolean(a.id, false);
            list.add(a);
        }
        return list;
    }


    public static List<Achievement> checkAndUnlock(Context ctx, DatabaseManager db,
                                                   int score, String mood,
                                                   int eventCount, int newStreak) {
        SharedPreferences p  = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        List<Achievement> newlyUnlocked = new ArrayList<>();

        int totalTrips = db.getAllTrips().size();

        boolean[] conditions = {
                totalTrips >= 1,
                totalTrips >= 10,
                totalTrips >= 25,
                totalTrips >= 50,
                score >= 60,
                score >= 80,
                score == 100,
                eventCount == 0,
                newStreak >= 3,
                newStreak >= 7,
                newStreak >= 20,
                "Happy".equals(mood) && score >= 75,
                db.lastNTripsAllAboveScore(3, 80),
                db.lastNTripsAllClean(3),
                db.hasImprovedByPoints(10),
        };

        List<Achievement> all = getAll(ctx);
        for (int i = 0; i < all.size() && i < conditions.length; i++) {
            Achievement a = all.get(i);
            if (!a.unlocked && conditions[i]) {
                editor.putBoolean(a.id, true);
                a.unlocked = true;
                newlyUnlocked.add(a);
            }
        }

        editor.apply();
        return newlyUnlocked;
    }

    public static int getUnlockedCount(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int count = 0;
        String[] allIds = {FIRST_TRIP, TRIPS_10, TRIPS_25, TRIPS_50, SCORE_60, SCORE_80,
                PERFECT, CLEAN_TRIP, STREAK_3, STREAK_7, STREAK_20, HAPPY_HIGH,
                CHEETAH_X3, CLEAN_X3, IMPROVED};
        for (String id : allIds) {
            if (p.getBoolean(id, false)) count++;
        }
        return count;
    }
}