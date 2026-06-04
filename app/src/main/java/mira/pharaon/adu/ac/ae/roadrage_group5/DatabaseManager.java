package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "roadrage.db";
    private static final int DB_VERSION = 2; // bumped: added locations table

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
                "persona TEXT)");

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

        // Stores GPS coordinates captured during each trip for the map view
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
        // Non-destructive migration: only add what's new.
        // The original drop-all approach wipes test data every time you change the schema.
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS locations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "trip_id INTEGER, " +
                    "latitude REAL, " +
                    "longitude REAL, " +
                    "speed_kmh REAL, " +
                    "FOREIGN KEY(trip_id) REFERENCES trips(id))");
        }
        // For future schema changes: add else-if (oldVersion < 3) { ... } blocks here
    }


    // ─── Trip methods ─────────────────────────────────────────────────────────

    public long insertTrip(String date, int duration, int score, String mood,
                           int eventCount, String persona) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("duration_seconds", duration);
        values.put("score", score);
        values.put("mood", mood);
        values.put("harsh_event_count", eventCount);
        values.put("persona", persona);
        return db.insert("trips", null, values);
    }

    // [0]=date, [1]=score, [2]=persona, [3]=mood, [4]=id  ← id added for map navigation
    public List<String[]> getAllTrips() {
        List<String[]> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM trips ORDER BY id DESC", null);
        while (cursor.moveToNext()) {
            trips.add(new String[]{
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("score"))),
                    cursor.getString(cursor.getColumnIndexOrThrow("persona")),
                    cursor.getString(cursor.getColumnIndexOrThrow("mood")),
                    String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("id")))
            });
        }
        cursor.close();
        return trips;
    }

    public List<String[]> getAverageScoreByMood() {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT mood, AVG(score) as avg_score FROM trips GROUP BY mood", null);
        while (cursor.moveToNext()) {
            result.add(new String[]{
                    cursor.getString(0),
                    String.format("%.0f", cursor.getDouble(1))
            });
        }
        cursor.close();
        return result;
    }

    public String[] getUserStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(score) FROM trips LIMIT 1", null);
        if (cursor.moveToFirst()) {
            double avg = cursor.getDouble(0);
            cursor.close();
            Cursor personaCursor = db.rawQuery(
                    "SELECT persona FROM trips ORDER BY id DESC LIMIT 1", null);
            String persona = "Unknown";
            if (personaCursor.moveToFirst()) persona = personaCursor.getString(0);
            personaCursor.close();
            return new String[]{String.format("%.0f", avg), persona != null ? persona : "Unknown"};
        }
        cursor.close();
        return new String[]{"0", "Unknown"};
    }

    public void updateUserStats(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(score) FROM trips", null);
        double avg = 0;
        if (cursor.moveToFirst()) avg = cursor.getDouble(0);
        cursor.close();

        Cursor cursor2 = db.rawQuery(
                "SELECT persona FROM trips ORDER BY id DESC LIMIT 1", null);
        String persona = "Unknown";
        if (cursor2.moveToFirst()) persona = cursor2.getString(0);
        cursor2.close();

        ContentValues values = new ContentValues();
        values.put("average_score", avg);
        values.put("current_persona", persona);
        db.update("user", values, "id = 1", null);
    }


    // ─── Event methods ────────────────────────────────────────────────────────

    public void insertEvent(long tripId, String eventType, int severity, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trip_id", tripId);
        values.put("event_type", eventType);
        values.put("severity", severity);
        values.put("timestamp", timestamp);
        db.insert("events", null, values);
    }


    // ─── Location methods ─────────────────────────────────────────────────────

    // Call once per GPS update during a trip
    public void insertLocation(long tripId, double lat, double lng, float speedKmh) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trip_id", tripId);
        values.put("latitude", lat);
        values.put("longitude", lng);
        values.put("speed_kmh", speedKmh);
        db.insert("locations", null, values);
    }

    // Returns ordered list of [lat, lng, speed_kmh] for drawing the route polyline
    public List<double[]> getLocationsForTrip(long tripId) {
        List<double[]> points = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT latitude, longitude, speed_kmh FROM locations " +
                        "WHERE trip_id = ? ORDER BY id ASC",
                new String[]{String.valueOf(tripId)});
        while (cursor.moveToNext()) {
            points.add(new double[]{
                    cursor.getDouble(0), // lat
                    cursor.getDouble(1), // lng
                    cursor.getDouble(2)  // speed
            });
        }
        cursor.close();
        return points;
    }
}