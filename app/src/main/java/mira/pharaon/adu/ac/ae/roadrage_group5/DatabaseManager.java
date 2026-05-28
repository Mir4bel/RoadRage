package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class DatabaseManager extends SQLiteOpenHelper {

    // DB name and version — change version number if you alter the schema
    private static final String DB_NAME = "roadrage.db";
    private static final int DB_VERSION = 1;

    // Constructor — context comes from whatever Activity/Fragment calls it
    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This runs ONCE when the app is first installed
        // It creates all tables
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

        // Insert one default user row so it always exists
        db.execSQL("INSERT INTO user (id, name) VALUES (1, 'Driver')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Runs if you increment DB_VERSION — drops and recreates tables
        db.execSQL("DROP TABLE IF EXISTS trips");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }


    // Call this when a trip ends to save it
    // Returns the new trip's ID so you can attach events to it
    public long insertTrip(String date, int duration, int score, String mood, int eventCount, String persona) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("duration_seconds", duration);
        values.put("score", score);
        values.put("mood", mood);
        values.put("harsh_event_count", eventCount);
        values.put("persona", persona);
        return db.insert("trips", null, values); // returns the new row ID
    }

    // Call this in HistoryFragment to load the list
    public List<String[]> getAllTrips() {
        List<String[]> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM trips ORDER BY id DESC", null);
        while (cursor.moveToNext()) {
            trips.add(new String[]{
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("score"))),
                    cursor.getString(cursor.getColumnIndexOrThrow("persona")),
                    cursor.getString(cursor.getColumnIndexOrThrow("mood"))
            });
        }
        cursor.close();
        return trips;
    }

    // For the mood impact insight — gets average score grouped by mood
    public List<String[]> getAverageScoreByMood() {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT mood, AVG(score) as avg_score FROM trips GROUP BY mood", null);
        while (cursor.moveToNext()) {
            result.add(new String[]{
                    cursor.getString(0),              // mood label
                    String.format("%.0f", cursor.getDouble(1)) // avg score, no decimals
            });
        }
        cursor.close();
        return result;
    }

    // Call this every time AccelerometerManager detects a harsh event
    // tripId comes from TripDAO.insertTrip()'s return value
    public void insertEvent(long tripId, String eventType, int severity, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trip_id", tripId);
        values.put("event_type", eventType);
        values.put("severity", severity);
        values.put("timestamp", timestamp);
        db.insert("events", null, values);
    }

    // Called by HomeActivity and ProfileActivity to show avg score + persona
    // Returns String[] where [0] = avg score as string, [1] = persona name
    public String[] getUserStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(score) FROM trips LIMIT 1", null);
        if (cursor.moveToFirst()) {
            double avg = cursor.getDouble(0);
            cursor.close();

            // Get latest persona from most recent trip
            Cursor personaCursor = db.rawQuery(
                    "SELECT persona FROM trips ORDER BY id DESC LIMIT 1", null);
            String persona = "Unknown";
            if (personaCursor.moveToFirst()) {
                persona = personaCursor.getString(0);
            }
            personaCursor.close();

            return new String[]{String.format("%.0f", avg), persona != null ? persona : "Unknown"};
        }
        cursor.close();
        return new String[]{"0", "Unknown"};
    }

    // Called by ResultActivity after saving a trip — recalculates and updates user row
    public void updateUserStats(android.content.Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Recalculate average score across all trips
        Cursor cursor = db.rawQuery("SELECT AVG(score) FROM trips", null);
        double avg = 0;
        if (cursor.moveToFirst()) avg = cursor.getDouble(0);
        cursor.close();

        // Get latest persona (from most recent trip)
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
}
