# RoadRage - Safe Driving Companion

A mobile application designed to promote safe driving habits among UAE residents through real-time driving behavior monitoring and personalized feedback.

## Project Overview

RoadRage monitors driving behavior using the device's GPS and accelerometer sensors to detect harsh events (sudden braking, sharp turns, overspeeding) and generates a driving score after each trip. Users can review their driving history, track improvement over time, and understand how their emotional state affects their driving through mood tracking.

## Key Features

### 1. **Real-Time Trip Monitoring**
- GPS-based speed tracking with live updates
- Accelerometer-based harsh event detection (braking, turning)
- Overspeeding detection (threshold: 120 km/h)
- Live event counter during trips

### 2. **Mood Check-In**
- Pre-trip mood selection (Happy, Neutral, Tired, Stressed)
- Tracks correlation between mood and driving behavior
- Raises awareness about emotional state impact on safety

### 3. **Trip Scoring System**
- Dynamic score calculation (0-100 scale)
- Persona classification based on driving score:
  - **Zen Driver** (80+): Smooth and consistent driving
  - **City Cruiser** (60-79): Good driving with minor rough moments
  - **The Rocket** (40-59): Fast and unpredictable driving
  - **Ghost Rider** (0-39): Needs significant improvement

### 4. **Trip History**
- Detailed record of all completed trips
- Information displayed: date, score, persona, mood
- Sortable by date (newest first)

### 5. **Profile & Analytics**
- Total trip count
- Average driving score
- Current persona classification
- Mood impact analysis showing average scores grouped by mood

### 6. **Navigation**
- Bottom tab navigation for easy access
- Three main screens: Home, History, Profile
- Smooth transitions between tabs

## Technical Architecture

### Technologies Used
- **Language**: Java
- **UI Framework**: Android Material3 Design System
- **Database**: SQLite with local storage
- **Sensors**: GPS (LocationManager), Accelerometer (SensorManager)
- **Minimum API**: Level 28 (Android 9.0)
- **Target API**: Level 36 (Android 15)

### Database Schema

#### `trips` Table
```sql
CREATE TABLE trips (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT,
    duration_seconds INTEGER,
    score INTEGER,
    mood TEXT,
    harsh_event_count INTEGER,
    persona TEXT
)
```

#### `events` Table
```sql
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trip_id INTEGER,
    event_type TEXT,
    severity INTEGER,
    timestamp TEXT,
    FOREIGN KEY(trip_id) REFERENCES trips(id)
)
```

#### `user` Table
```sql
CREATE TABLE user (
    id INTEGER PRIMARY KEY,
    name TEXT,
    total_trips INTEGER DEFAULT 0,
    average_score REAL DEFAULT 0,
    current_persona TEXT DEFAULT 'Unknown'
)
```

### App Architecture

#### Activities
- **MainActivity**: Launcher activity that routes to HomeActivity
- **HomeActivity**: Home screen with start trip button and current stats
- **MoodActivity**: Pre-trip mood selection screen
- **TripActivity**: Live trip monitoring with sensor data
- **ResultActivity**: Trip summary and score display
- **HistoryActivity**: Trip history list with RecyclerView
- **ProfileActivity**: User profile with stats and mood analysis

#### Base Classes
- **BaseActivity**: Abstract base class for consistent bottom navigation across main screens

#### Managers
- **DatabaseManager**: SQLite operations (extends SQLiteOpenHelper)
- **AccelerometerManager**: Sensor event listening and harsh event detection
- **LocationTracker**: GPS tracking and speed monitoring
- **TripScorer**: Trip score calculation logic
- **PersonaManager**: Persona classification and messaging

### Sensor Integration

#### GPS Sensor (LocationTracker)
- Updates every 2 seconds or 5 meters (whichever comes first)
- Converts m/s to km/h for user display
- Flags overspeeding at 120 km/h threshold

#### Accelerometer (AccelerometerManager)
- Samples at SENSOR_DELAY_NORMAL (~5 readings/second)
- Detects harsh braking: Y-axis > 15.0 m/s²
- Detects sharp turns: X-axis > 12.0 m/s²
- Cooldown period: 2 seconds between events to prevent double-counting

## Permissions Required

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
```

Location permission is requested at runtime during trip start.

## User Flow

1. **App Launch**
   - MainActivity starts and routes to HomeActivity
   
2. **Home Screen**
   - View current persona and average score
   - Tap "Start Trip" to begin

3. **Mood Selection**
   - Select emotional state before driving
   - Tap "Start Tracking" to begin trip

4. **Trip Monitoring**
   - Real-time speed display
   - Harsh event counter
   - Tap "Stop Trip" to end

5. **Results**
   - View trip score and persona
   - See persona message
   - Data saved to database
   - Tap "Back to Home" to return

6. **History View**
   - Scroll through all past trips
   - See details of each trip

7. **Profile View**
   - View overall statistics
   - Analyze mood impact on driving

## Design System

### Material3 Color Scheme
- **Primary**: Dark purple (#1B1B1F) - Main theme
- **Secondary**: Orange (#FF6B35) - Alerts/warnings
- **Tertiary**: Teal (#006A6C) - Accents
- **Error**: Red (#B3261E) - Danger states
- **Surface**: Off-white (#FDFBFF) - Background

### Typography
- Headlines: Roboto Medium
- Body: Roboto Regular
- Sizes range from 11sp (labels) to 80sp (score display)

### Component Styles
- Rounded corners: 16dp-28dp
- Card elevation: Minimal (0dp) for clean look
- Stroke width: 1dp-2dp for outlined buttons
- Padding: 16dp-32dp for consistent spacing

## Installation & Setup

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Android SDK 28+ 
- Gradle 8.0+

### Build Steps
```bash
# Clone the project
cd RoadRageGroup5

# Build the project
./gradlew clean build

# Run on emulator/device
./gradlew installDebug
```

### Configuration
No additional configuration needed. Database initializes automatically on first run.

## Future Enhancement Opportunities

1. **Leaderboard System**
   - User profiles with names
   - Competitive scoring
   - Friend connections

2. **Advanced Analytics**
   - Driving habit charts over time
   - Seasonal trend analysis
   - Peak dangerous hours

3. **Sensor Calibration**
   - Device-specific tuning
   - Weather condition adjustments
   - Road type detection

4. **Notifications**
   - Trip reminders
   - Achievement milestones
   - Safety alerts

5. **Data Export**
   - CSV export of trips
   - Integration with fitness apps
   - Cloud backup option

6. **Vehicle Profiles**
   - Different vehicles tracked separately
   - Vehicle-specific scoring
   - Maintenance alerts

## Testing Notes

### Manual Test Cases
- ✓ Navigation between tabs doesn't lose data
- ✓ Trip data persists after app restart
- ✓ Mood selection carries through to trip
- ✓ Sensor data updates in real-time
- ✓ History shows trips in correct order
- ✓ Mood impact calculation is accurate

### Known Limitations
- GPS accuracy depends on device and signal
- Accelerometer calibration varies by device
- 120 km/h threshold suitable for UAE but may need adjustment for other regions

## Course Learning Outcomes

This project demonstrates:
- ✅ SQLite database design and CRUD operations
- ✅ Android sensor integration (GPS, Accelerometer)
- ✅ Material3 UI design principles
- ✅ Activity lifecycle and navigation patterns
- ✅ Real-time data processing and callbacks
- ✅ Permission handling (runtime permissions)
- ✅ RecyclerView adapter implementation
- ✅ Data persistence and retrieval

## Team Contributors
Group 5

## License
Educational Project - UAE University Course

---

**Last Updated**: May 28, 2026
**Version**: 1.0
**Status**: Ready for Deployment

