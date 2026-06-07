package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME    = "roadrage.db";
    private static final int    DB_VERSION = 3; // needed to add timestamp_ms to trips

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE trips (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "duration_seconds INTEGER, " +
                "score INTEGER, " +
                "mood TEXT, " +
                "harsh_event_count INTEGER, " +
                "persona TEXT, " +
                "timestamp_ms INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trip_id INTEGER, " +
                "event_type TEXT, " +
                "severity REAL, " +
                "timestamp TEXT, " +
                "FOREIGN KEY(trip_id) REFERENCES trips(id))");

        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "total_trips INTEGER DEFAULT 0, " +
                "average_score REAL DEFAULT 0, " +
                "current_persona TEXT DEFAULT 'Unknown')");

        db.execSQL("CREATE TABLE locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trip_id INTEGER, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "speed_kmh REAL, " +
                "FOREIGN KEY(trip_id) REFERENCES trips(id))");

        db.execSQL("INSERT INTO user (id, name) VALUES (1, 'Driver')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS locations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "trip_id INTEGER, latitude REAL, longitude REAL, speed_kmh REAL, " +
                    "FOREIGN KEY(trip_id) REFERENCES trips(id))");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE trips ADD COLUMN timestamp_ms INTEGER DEFAULT 0");
        }
    }


    public long insertTrip(String date, int duration, int score, String mood,
                           int eventCount, String persona) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("date", date);
        v.put("duration_seconds", duration);
        v.put("score", score);
        v.put("mood", mood);
        v.put("harsh_event_count", eventCount);
        v.put("persona", persona);
        v.put("timestamp_ms", System.currentTimeMillis());
        return db.insert("trips", null, v);
    }

    public List<String[]> getAllTrips() {
        List<String[]> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM trips ORDER BY id DESC", null);
        while (c.moveToNext()) {
            trips.add(new String[]{
                    c.getString(c.getColumnIndexOrThrow("date")),
                    String.valueOf(c.getInt(c.getColumnIndexOrThrow("score"))),
                    c.getString(c.getColumnIndexOrThrow("persona")),
                    c.getString(c.getColumnIndexOrThrow("mood")),
                    String.valueOf(c.getLong(c.getColumnIndexOrThrow("id")))
            });
        }
        c.close();
        return trips;
    }

    public List<String[]> getTopTrips(int limit) {
        List<String[]> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT date, score, persona, mood, id FROM trips ORDER BY score DESC LIMIT ?",
                new String[]{String.valueOf(limit)});
        while (c.moveToNext()) {
            trips.add(new String[]{
                    c.getString(0),
                    String.valueOf(c.getInt(1)),
                    c.getString(2),
                    c.getString(3),
                    String.valueOf(c.getLong(4))
            });
        }
        c.close();
        return trips;
    }

    public List<String[]> getAverageScoreByMood() {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT mood, AVG(score) as avg_score FROM trips GROUP BY mood", null);
        while (c.moveToNext()) {
            result.add(new String[]{c.getString(0),
                    String.format("%.0f", c.getDouble(1))});
        }
        c.close();
        return result;
    }

    public String[] getUserStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(score) FROM trips", null);
        if (c.moveToFirst()) {
            double avg = c.getDouble(0);
            c.close();
            Cursor c2 = db.rawQuery(
                    "SELECT persona FROM trips ORDER BY id DESC LIMIT 1", null);
            String persona = "Unknown";
            if (c2.moveToFirst()) persona = c2.getString(0);
            c2.close();
            return new String[]{String.format("%.0f", avg),
                    persona != null ? persona : "Unknown"};
        }
        c.close();
        return new String[]{"0", "Unknown"};
    }

    public void updateUserStats(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(score) FROM trips", null);
        double avg = 0;
        if (c.moveToFirst()) avg = c.getDouble(0);
        c.close();
        Cursor c2 = db.rawQuery(
                "SELECT persona FROM trips ORDER BY id DESC LIMIT 1", null);
        String persona = "Unknown";
        if (c2.moveToFirst()) persona = c2.getString(0);
        c2.close();
        ContentValues v = new ContentValues();
        v.put("average_score", avg);
        v.put("current_persona", persona);
        db.update("user", v, "id = 1", null);
    }


    public boolean lastNTripsAllAboveScore(int n, int threshold) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM (SELECT score FROM trips ORDER BY id DESC LIMIT ?) " +
                        "WHERE score >= ?",
                new String[]{String.valueOf(n), String.valueOf(threshold)});
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            return getAllTrips().size() >= n && count >= n;
        }
        c.close();
        return false;
    }


    public boolean lastNTripsAllClean(int n) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM (SELECT harsh_event_count FROM trips ORDER BY id DESC LIMIT ?) " +
                        "WHERE harsh_event_count = 0",
                new String[]{String.valueOf(n)});
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            return getAllTrips().size() >= n && count >= n;
        }
        c.close();
        return false;
    }


    public boolean hasImprovedByPoints(int points) {
        if (getAllTrips().size() < 10) return false;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c1 = db.rawQuery(
                "SELECT AVG(score) FROM (SELECT score FROM trips ORDER BY id ASC LIMIT 5)", null);
        double firstAvg = 0;
        if (c1.moveToFirst()) firstAvg = c1.getDouble(0);
        c1.close();

        Cursor c2 = db.rawQuery(
                "SELECT AVG(score) FROM (SELECT score FROM trips ORDER BY id DESC LIMIT 5)", null);
        double lastAvg = 0;
        if (c2.moveToFirst()) lastAvg = c2.getDouble(0);
        c2.close();

        return (lastAvg - firstAvg) >= points;
    }


    public static class WeeklyStats {
        public final int avgScore;
        public final int tripCount;
        WeeklyStats(int avg, int count) { avgScore = avg; tripCount = count; }
    }


    public WeeklyStats getWeeklyStats(boolean thisWeek) {
        long now      = System.currentTimeMillis();
        long weekMs   = 7L * 24 * 60 * 60 * 1000;
        long rangeEnd   = thisWeek ? now       : now - weekMs;
        long rangeStart = thisWeek ? now - weekMs : now - 2 * weekMs;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT AVG(score), COUNT(*) FROM trips " +
                        "WHERE timestamp_ms > ? AND timestamp_ms <= ?",
                new String[]{String.valueOf(rangeStart), String.valueOf(rangeEnd)});

        if (c.moveToFirst()) {
            int avg   = (int) Math.round(c.getDouble(0));
            int count = c.getInt(1);
            c.close();
            return new WeeklyStats(avg, count);
        }
        c.close();
        return new WeeklyStats(0, 0);
    }


    public void insertLocation(long tripId, double lat, double lng, float speedKmh) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("trip_id", tripId);
        v.put("latitude", lat);
        v.put("longitude", lng);
        v.put("speed_kmh", speedKmh);
        db.insert("locations", null, v);
    }

    public List<double[]> getLocationsForTrip(long tripId) {
        List<double[]> points = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT latitude, longitude, speed_kmh FROM locations " +
                        "WHERE trip_id = ? ORDER BY id ASC",
                new String[]{String.valueOf(tripId)});
        while (c.moveToNext()) {
            points.add(new double[]{c.getDouble(0), c.getDouble(1), c.getDouble(2)});
        }
        c.close();
        return points;
    }


    public void insertEvent(long tripId, String eventType, int severity, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("trip_id", tripId);
        v.put("event_type", eventType);
        v.put("severity", severity);
        v.put("timestamp", timestamp);
        db.insert("events", null, v);
    }
}