# RoadRage - Quick Start Guide for Developers

## Getting Started (5 minutes)

### Prerequisites
- Android Studio (2023.1 or later)
- JDK 11+
- Android SDK with API 28+ installed
- Physical device or emulator with GPS/Accelerometer

### Step 1: Open Project
```bash
# Navigate to project root
cd RoadRageGroup5

# Open in Android Studio
# File > Open > Select RoadRageGroup5 folder
```

### Step 2: Sync Gradle
- Android Studio will automatically prompt to sync
- Click "Sync Now" if not automatic
- Wait for gradle build to complete

### Step 3: Build & Run
```bash
# Build project
./gradlew clean build

# Run on connected device/emulator
./gradlew installDebug
```

Or simply:
- Click green "Run" button in Android Studio toolbar
- Select target device
- Click OK

---

## Project Structure

```
RoadRageGroup5/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/mira/pharaon/adu/ac/ae/roadrage_group5/
│   │   │   │   ├── MainActivity.java              (Launcher)
│   │   │   │   ├── BaseActivity.java              (Navigation base)
│   │   │   │   ├── HomeActivity.java              (Home tab)
│   │   │   │   ├── MoodActivity.java              (Mood selection)
│   │   │   │   ├── TripActivity.java              (Trip monitoring)
│   │   │   │   ├── ResultActivity.java            (Trip results)
│   │   │   │   ├── HistoryActivity.java           (History tab)
│   │   │   │   ├── ProfileActivity.java           (Profile tab)
│   │   │   │   ├── DatabaseManager.java           (SQLite)
│   │   │   │   ├── AccelerometerManager.java      (Accel sensor)
│   │   │   │   ├── LocationTracker.java           (GPS sensor)
│   │   │   │   ├── TripScorer.java                (Scoring)
│   │   │   │   └── PersonaManager.java            (Persona logic)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_home.xml
│   │   │   │   │   ├── activity_mood.xml
│   │   │   │   │   ├── activity_trip.xml
│   │   │   │   │   ├── activity_result.xml
│   │   │   │   │   ├── activity_history.xml
│   │   │   │   │   ├── activity_profile.xml
│   │   │   │   │   └── item_trip.xml              (RecyclerView item)
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_nav_menu.xml        (Navigation menu)
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml                 (Material3 palette)
│   │   │   │   │   ├── strings.xml                (String resources)
│   │   │   │   │   └── themes.xml                 (Material3 theme)
│   │   │   │   └── drawable/, mipmap/             (Icons & images)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/java/                             (Unit tests)
│   │   └── androidTest/java/                      (Instrumented tests)
│   ├── build.gradle.kts                           (App dependencies)
│   └── proguard-rules.pro                         (Obfuscation rules)
├── build.gradle.kts                               (Root config)
├── settings.gradle.kts                            (Project settings)
├── gradle/wrapper/                                (Gradle wrapper)
├── README.md                                      (Project documentation)
├── DESIGN_SYSTEM.md                               (UI/UX guidelines)
├── TESTING_CHECKLIST.md                           (QA test cases)
├── FIXES_APPLIED.md                               (Bug fixes applied)
├── PROJECT_SUMMARY.md                             (Complete summary)
└── local.properties                               (SDK path - auto-generated)
```

---

## Key Classes Overview

### Core Activities

#### MainActivity
**Purpose**: Application launcher  
**Responsibilities**: 
- Start HomeActivity immediately
- No UI rendering

**Key Methods**: `onCreate()`

---

#### BaseActivity
**Purpose**: Abstract base class for main screens  
**Responsibilities**:
- Manage bottom navigation for Home, History, Profile
- Handle tab switching without animation

**Key Methods**: `setupBottomNav(int selectedItemId)`

---

#### HomeActivity (extends BaseActivity)
**Purpose**: Home screen / main hub  
**Responsibilities**:
- Display current persona and average score
- Show "Start Trip" button
- Load stats from database

**Key Methods**: `onCreate()`, load stats from DatabaseManager

**Layout**: `activity_home.xml`  
**Database**: Queries from trips table

---

#### MoodActivity
**Purpose**: Pre-trip mood selection  
**Responsibilities**:
- Let user select emotional state
- Highlight selected mood
- Pass selection to TripActivity

**Key Methods**: `selectMood(MaterialButton button, String mood)`

**Layout**: `activity_mood.xml`  
**Intent Data**: Passes mood string

---

#### TripActivity (implements sensor listeners)
**Purpose**: Real-time trip monitoring  
**Responsibilities**:
- Listen to GPS updates
- Listen to accelerometer events
- Track and display metrics
- Calculate trip score
- Save trip to database

**Implements**:
- `AccelerometerManager.AccelerometerEventListener`
- `LocationTracker.LocationEventListener`

**Key Methods**:
- `onHarshEvent(String type, float severity)` - From accelerometer
- `onSpeedUpdate(float speedKmh)` - From GPS
- `onSpeedingDetected(float speedKmh)` - From GPS
- `stopTrip()` - End trip and calculate score

**Layout**: `activity_trip.xml`  
**Sensors**: GPS, Accelerometer  
**Permissions**: Location (runtime)

---

#### ResultActivity
**Purpose**: Trip summary and results  
**Responsibilities**:
- Display trip score
- Show persona and message
- Save trip to database
- Update user statistics

**Key Methods**: `onCreate()`

**Layout**: `activity_result.xml`  
**Database**: Inserts to trips table, updates user stats

---

#### HistoryActivity (extends BaseActivity)
**Purpose**: Trip history list  
**Responsibilities**:
- Display all completed trips
- Show empty state if no trips
- Sort trips newest first

**Key Methods**: `onCreate()`

**Layout**: `activity_history.xml`  
**RecyclerView**: TripAdapter (inner class)  
**Database**: Queries from trips table

---

#### ProfileActivity (extends BaseActivity)
**Purpose**: User stats and mood analysis  
**Responsibilities**:
- Display overall statistics
- Show mood impact on driving
- Dynamic mood cards

**Key Methods**: `onCreate()`

**Layout**: `activity_profile.xml`  
**Database**: Queries trips and mood analysis

---

### Manager Classes

#### DatabaseManager (extends SQLiteOpenHelper)
**Purpose**: SQLite database operations  
**Database Name**: `roadrage.db`  
**Tables**: `trips`, `events`, `user`

**Key Methods**:
```java
// Insert operations
long insertTrip(...)              // Save completed trip
void insertEvent(...)             // Save harsh event

// Query operations
List<String[]> getAllTrips()      // Get all trips for history
String[] getUserStats()           // Get avg score and persona
List<String[]> getAverageScoreByMood()  // Get mood analysis

// Update operations
void updateUserStats(Context)     // Recalculate user stats
```

---

#### AccelerometerManager (implements SensorEventListener)
**Purpose**: Detect harsh driving events  
**Sensor Type**: Accelerometer  
**Thresholds**:
- Braking: Y-axis > 15.0 m/s²
- Turning: X-axis > 12.0 m/s²
- Cooldown: 2 seconds between events

**Listener Interface**:
```java
public interface AccelerometerEventListener {
    void onHarshEvent(String type, float severity);
}
```

**Key Methods**:
```java
void startListening()    // Begin sampling (usually in TripActivity)
void stopListening()     // Stop sampling
void onSensorChanged()   // Fired by Android framework
```

---

#### LocationTracker (implements LocationListener)
**Purpose**: Monitor speed and detect overspeeding  
**Sensor Type**: GPS (LocationManager)  
**Update Frequency**: Every 2 seconds or 5 meters  
**Speeding Threshold**: 120 km/h

**Listener Interface**:
```java
public interface LocationEventListener {
    void onSpeedUpdate(float speedKmh);
    void onSpeedingDetected(float speedKmh);
}
```

**Key Methods**:
```java
void startTracking()     // Begin tracking
void stopTracking()      // Stop tracking
float getCurrentSpeed()  // Get last known speed
void onLocationChanged() // Fired by Android framework
```

---

#### TripScorer
**Purpose**: Calculate driving score  
**Formula**: 100 - (harsh_events × 8)  
**Range**: 0-100

```java
public int calculateScore(int eventCount, int durationSeconds) {
    int score = 100;
    score -= eventCount * 8;
    if (eventCount == 0 && durationSeconds >= 300) score = 100;  // Perfect trip bonus
    return Math.max(0, score);
}
```

---

#### PersonaManager
**Purpose**: Classify driving persona  
**Personas**:
- Zen Driver (80+): "Smooth and consistent..."
- City Cruiser (60-79): "Pretty good driving..."
- The Rocket (40-59): "Fast and unpredictable..."
- Ghost Rider (0-39): "Multiple harsh events..."

**Key Methods**:
```java
String getPersona(int score)              // Get persona name
String getPersonaMessage(String persona)  // Get persona message
```

---

## Common Development Tasks

### Adding a New Screen
1. Create `NewActivity.java` (extend BaseActivity if needs bottom nav)
2. Create `activity_new.xml` layout
3. Add activity to `AndroidManifest.xml`
4. Update navigation if needed

### Modifying Database Schema
1. Edit `onCreate()` in DatabaseManager
2. **Important**: Increment `DB_VERSION`
3. `onUpgrade()` will be called automatically

### Adding a New String
1. Open `res/values/strings.xml`
2. Add new `<string>` element with unique name
3. Reference as `getString(R.string.name)` in code
4. Never hardcode strings in Java files

### Adjusting Sensor Thresholds
- **Braking**: Edit `BRAKE_THRESHOLD` in AccelerometerManager
- **Turning**: Edit `TURN_THRESHOLD` in AccelerometerManager
- **Overspeeding**: Edit `SPEED_THRESHOLD_KMH` in LocationTracker

### Changing Colors
1. Edit hex values in `res/values/colors.xml`
2. Update theme in `res/values/themes.xml`
3. Dark mode updates automatically in `res/values-night/`

---

## Testing During Development

### Test a Single Activity
```bash
./gradlew installDebug
# Then manually navigate to the activity
```

### Test Database Operations
Add temporary logging in DatabaseManager:
```java
Log.d("DB_DEBUG", "Inserting trip: " + date);
```

View logs:
```bash
./gradlew logcat
```

### Test Sensors
- Use Android emulator's extended controls (GPS, accelerometer simulation)
- Or use physical device
- Check sensor availability: Settings > Developer options > Sensor info

### Debug a Crash
```bash
./gradlew logcat
# Look for "Exception" or "Error" in output
# Or use Android Studio's Debugger tab
```

---

## Common Issues & Solutions

### Issue: "JAVA_HOME is not set"
**Solution**: 
```bash
# On Windows (in PowerShell):
$env:JAVA_HOME = "C:\Program Files\Android\Studio\jbr"
```

### Issue: "cannot find symbol" in Java files
**Solution**: 
1. Rebuild: `./gradlew clean build`
2. Check imports: Right-click class name > "Optimize Imports"
3. Invalidate caches: File > Invalidate Caches

### Issue: Layout preview not showing
**Solution**:
1. Click "Design" tab (not "Split")
2. Tools > Android > SDK Manager (check API 28+ installed)
3. Invalidate caches and restart

### Issue: App crashes on startup
**Solution**:
1. Check `AndroidManifest.xml` for typos
2. Run `./gradlew logcat` to see error
3. Check that all activities are declared in manifest

### Issue: GPS not working
**Solution**:
1. On emulator: Extended controls > Location > Set mock location
2. On device: Ensure location permission granted
3. Check AndroidManifest.xml has `ACCESS_FINE_LOCATION`

---

## Code Style Guidelines

### Naming Conventions
- **Classes**: PascalCase (`TripActivity`, `DatabaseManager`)
- **Methods**: camelCase (`insertTrip()`, `onHarshEvent()`)
- **Variables**: camelCase (`tripScore`, `selectedMood`)
- **Constants**: UPPER_SNAKE_CASE (`BRAKE_THRESHOLD`, `DB_VERSION`)
- **Resources**: snake_case (`activity_home.xml`, `ic_menu_compass`)

### Formatting
- **Indentation**: 4 spaces (or use IDE auto-format)
- **Line length**: Keep under 100 characters
- **Comments**: Explain WHY, not WHAT
- **Imports**: Use Android Studio's organize imports

### Code Organization
```java
public class MyActivity extends AppCompatActivity {
    // 1. Constants
    private static final String TAG = "MyActivity";
    
    // 2. Member variables
    private DatabaseManager db;
    
    // 3. Lifecycle methods (in order: onCreate, onStart, onResume, onPause, onStop, onDestroy)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
    }
    
    // 4. Click listeners
    private void setupClickListeners() {
        // ...
    }
    
    // 5. Helper methods (private)
    private void helperMethod() {
        // ...
    }
}
```

---

## Useful Commands

```bash
# Build
./gradlew clean build

# Install on device
./gradlew installDebug

# Run tests
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests

# View logs
./gradlew logcat

# Build APK for distribution
./gradlew assembleRelease

# Format code
./gradlew spotlessApply
```

---

## Debugging Tips

### Android Studio Debugger
1. Set breakpoint (click left margin on line)
2. Click Debug button (instead of Run)
3. Use Debug panel to step through code

### Logcat Viewing
```bash
# Filter by tag
./gradlew logcat | grep "MyActivity"

# Or use Android Studio: View > Tool Windows > Logcat
```

### Database Inspection
```bash
# Pull database from device
adb pull /data/data/mira.pharaon.adu.ac.ae.roadrage_group5/databases/roadrage.db

# View with SQLite browser
sqlite3 roadrage.db ".tables"
sqlite3 roadrage.db "SELECT * FROM trips;"
```

---

## Version Control (if using Git)

### Suggested .gitignore entries
```
/build/
.gradle/
.idea/
*.apk
*.aar
local.properties
```

### Commit messages
```
[FEATURE] Add new feature description
[BUGFIX] Fix issue description
[REFACTOR] Improve code organization
[DOCS] Update documentation
```

---

## Additional Resources

### Official Android Docs
- https://developer.android.com/
- Material Design 3: https://m3.material.io
- SQLite: https://developer.android.com/training/data-storage/sqlite

### Project Documentation
- `README.md` - Full project overview
- `DESIGN_SYSTEM.md` - UI/UX specifications
- `TESTING_CHECKLIST.md` - QA test cases
- `FIXES_APPLIED.md` - Bug fixes and improvements

---

## Getting Help

1. **Check Documentation**: See files listed in "Project Documentation" above
2. **Search Issues**: Look at logcat output for error messages
3. **Debug Mode**: Use Android Studio debugger (F8)
4. **Ask Team**: Post questions in project chat/wiki

---

**Last Updated**: May 28, 2026  
**For Questions**: Refer to README.md or PROJECT_SUMMARY.md

