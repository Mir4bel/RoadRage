# RoadRage - Testing & Validation Checklist

## Pre-Launch Verification

### Build & Compilation
- [ ] Project builds without errors: `./gradlew clean build`
- [ ] No warnings in Gradle output
- [ ] All dependencies resolve correctly
- [ ] Minimum SDK (28) and Target SDK (36) configured

### Code Quality
- [ ] No unused imports
- [ ] Consistent code style
- [ ] Proper null checks in database queries
- [ ] No hardcoded strings (all in strings.xml)

---

## Navigation Testing

### App Launch
- [ ] App launches successfully
- [ ] MainActivity shows no visible screen (transparent)
- [ ] Immediately transitions to HomeActivity
- [ ] No blank screen or lag

### Bottom Navigation
- [ ] All three tabs visible (Home, History, Profile)
- [ ] Home tab selected by default
- [ ] Tab switching has no animation
- [ ] Tab icons display correctly

### Tab Navigation Integrity
- [ ] Switching Home → History → Home preserves data
- [ ] All tabs maintain their own UI state
- [ ] No data loss when navigating between tabs
- [ ] Pressing same tab twice doesn't restart activity

---

## Home Screen (HomeActivity)

### UI Elements
- [ ] Title "Road Rage" displays
- [ ] Subtitle "Drive smarter, not harder." visible
- [ ] "YOUR PERSONA" card shows
- [ ] "AVERAGE SCORE" card shows
- [ ] "Start Trip" button is prominent and tappable
- [ ] Bottom navigation bar is fixed at bottom
- [ ] Content scrolls when needed on small screens

### Data Display
- [ ] Default score is "0" before any trips
- [ ] Default persona is "Unknown" before any trips
- [ ] After first trip, score and persona update correctly
- [ ] Score updates after completing multiple trips (showing average)

### User Interactions
- [ ] Tapping "Start Trip" navigates to MoodActivity
- [ ] No errors when tapping button
- [ ] Activity transitions smoothly

---

## Mood Selection (MoodActivity)

### UI Elements
- [ ] Title "Before you drive" visible
- [ ] Subtitle "How are you feeling right now?" shows
- [ ] Four mood buttons display with emojis:
  - [ ] 😊 Happy
  - [ ] 😐 Neutral
  - [ ] 😴 Tired
  - [ ] 😣 Stressed
- [ ] "Start Tracking" button is visible
- [ ] Selected mood has visual highlight (thicker stroke)

### User Interactions
- [ ] Tapping a mood button selects it (stroke thickens)
- [ ] Tapping another mood deselects previous one
- [ ] Default selection is "Neutral"
- [ ] Tapping "Start Tracking" navigates to TripActivity
- [ ] Selected mood is passed to TripActivity correctly

---

## Trip Monitoring (TripActivity)

### Permissions
- [ ] App requests location permission on first trip
- [ ] Permission dialog appears with clear text
- [ ] Granting permission allows GPS to start
- [ ] Denying permission shows graceful handling

### UI Elements
- [ ] "Trip in Progress" title displays
- [ ] Speed card shows (with "SPEED" label)
- [ ] Harsh events card shows (with "HARSH EVENTS" label)
- [ ] "Stop Trip" button is visible
- [ ] Both cards have appropriate color coding
- [ ] Cards don't overlap or misalign

### Real-Time Data
- [ ] Speed updates every 2 seconds (or when GPS location changes)
- [ ] Speed displays as "X km/h" format
- [ ] Speed shows "0 km/h" initially (waiting for GPS lock)
- [ ] Harsh event counter increments when:
  - [ ] Sudden braking detected (Y-axis acceleration)
  - [ ] Sharp turn detected (X-axis acceleration)
  - [ ] Overspeeding detected (>120 km/h)
- [ ] Events increment correctly without double-counting

### Trip Duration
- [ ] Timer accurately counts trip duration
- [ ] Duration includes full length of trip (from start to stop)

### User Interactions
- [ ] Tapping "Stop Trip" ends the trip
- [ ] Trip ends gracefully (no crashes)
- [ ] Activity transitions to ResultActivity smoothly
- [ ] If app pauses/resumes, sensors restart correctly

### Sensor Behavior
- [ ] GPS continues to work with screen on
- [ ] Accelerometer continues to work with screen on
- [ ] No sensor crashes during long trips (10+ minutes)
- [ ] Battery usage is reasonable

---

## Trip Results (ResultActivity)

### UI Elements
- [ ] "Trip Complete" header displays
- [ ] Score shows prominently (0-100)
- [ ] "out of 100" subtitle visible
- [ ] Persona name displays below score
- [ ] Persona message displays and is appropriate for score:
  - [ ] Zen Driver (80+): "Smooth and consistent..."
  - [ ] City Cruiser (60-79): "Pretty good driving..."
  - [ ] The Rocket (40-59): "Fast and unpredictable..."
  - [ ] Ghost Rider (0-39): "Multiple harsh events..."
- [ ] "Back to Home" button visible

### Score Calculation
- [ ] Score = 100 - (eventCount × 8)
- [ ] Score minimum is 0 (no negatives)
- [ ] Perfect trip (0 events, 5+ minutes) = 100
- [ ] Score formula produces reasonable values:
  - [ ] 1 event = 92
  - [ ] 5 events = 60
  - [ ] 10+ events = 20-0

### Data Persistence
- [ ] Trip data saves to database
- [ ] Trip appears in History tab immediately after
- [ ] Profile stats update with new trip data
- [ ] Persona is correctly saved
- [ ] Mood is correctly saved

### User Interactions
- [ ] Tapping "Back to Home" navigates to HomeActivity
- [ ] HomeActivity shows updated stats

---

## Trip History (HistoryActivity)

### UI Elements
- [ ] "Trip History" title visible at top
- [ ] Bottom navigation bar at bottom
- [ ] RecyclerView displays trips in list
- [ ] List scrolls smoothly if many trips
- [ ] Bottom nav doesn't overlap content

### Empty State
- [ ] When no trips exist:
  - [ ] RecyclerView is hidden
  - [ ] "No trips yet" message shows
  - [ ] "Start your first trip from the home screen" subtitle shows
  - [ ] Bottom navigation is still visible

### Trip Display
- [ ] Each trip shows:
  - [ ] Date in format "MMM dd, yyyy" (e.g., "May 27, 2026")
  - [ ] Trip persona (e.g., "Zen Driver")
  - [ ] Mood with emoji (e.g., "😊 Happy")
  - [ ] Score prominently (right side, large font)
- [ ] Trips are sorted newest first
- [ ] All trips from database appear in list

### Sorting & Ordering
- [ ] Most recent trip appears at top
- [ ] Oldest trips appear at bottom
- [ ] Order persists when leaving and returning to tab

### User Interactions
- [ ] Scrolling works smoothly
- [ ] No crashes when scrolling

---

## Profile & Analytics (ProfileActivity)

### UI Elements
- [ ] "Profile" title visible
- [ ] Two stat cards at top:
  - [ ] "TRIPS" card with trip count
  - [ ] "AVG SCORE" card with average score
- [ ] "CURRENT PERSONA" card displays current persona
- [ ] "Mood Impact" section with subtitle
- [ ] Mood impact cards display for each mood
- [ ] Bottom navigation bar at bottom
- [ ] Content scrolls smoothly on small screens

### Empty State
- [ ] When no trips exist:
  - [ ] Trip count shows "0"
  - [ ] Avg score shows "0"
  - [ ] Current persona shows "Unknown"
  - [ ] Mood Impact shows "Complete some trips to see mood impact"

### Stats Display
- [ ] Total trips count is accurate
- [ ] Average score is correctly calculated
  - [ ] Formula: Sum of all trip scores / Number of trips
  - [ ] No decimals displayed
- [ ] Current persona matches latest completed trip

### Mood Impact Analysis
- [ ] Displays one card per mood with trips
- [ ] Each card shows:
  - [ ] Mood name (Happy, Neutral, Tired, Stressed)
  - [ ] Average score for that mood
  - [ ] Format: "avg XY" where XY is rounded average
- [ ] Moods appear in cards (not inline text)
- [ ] Cards have proper spacing and styling

### Data Accuracy
- [ ] If Happy mood trips average 85, shows "avg 85"
- [ ] If no trips with a mood, that mood doesn't appear
- [ ] Mood data updates after new trip completion

---

## Database Testing

### Data Persistence
- [ ] Complete a trip and close app
- [ ] Reopen app and trip still exists in history
- [ ] Stats are preserved
- [ ] Database persists across app restarts

### Multiple Trips
- [ ] Complete 3+ trips with different moods
- [ ] All trips appear in history
- [ ] Average score is correctly recalculated
- [ ] Mood impact shows all moods used

### Data Integrity
- [ ] No duplicate trip entries
- [ ] No data corruption on crashes
- [ ] Database file location is app-specific (not external)

---

## Performance & Stability

### Memory Usage
- [ ] App doesn't consume excessive RAM
- [ ] No memory leaks during long use
- [ ] History tab doesn't lag with 100+ trips

### Responsiveness
- [ ] UI responds quickly to taps (<200ms)
- [ ] Smooth scrolling in RecyclerView
- [ ] Trip monitoring is real-time (not delayed)

### Crash Testing
- [ ] No crashes during navigation
- [ ] No crashes during GPS/Accel sampling
- [ ] Graceful handling of permission denial
- [ ] No crashes if trip >1 hour long
- [ ] No crashes if device goes to sleep during trip

---

## Compatibility Testing

### Screen Sizes
- [ ] Layouts work on small phones (5")
- [ ] Layouts work on large phones (6.7"+)
- [ ] Layouts work on tablets (landscape mode)
- [ ] No text cutoff on any screen size
- [ ] Bottom nav visible on all sizes

### Android Versions
- [ ] Builds on API 28 (Android 9.0)
- [ ] Builds on API 36 (Android 15)
- [ ] Location permissions work on all versions
- [ ] Material3 theme applies correctly

---

## Final Checklist

### Before Submission
- [ ] All activities tested and working
- [ ] All database operations tested
- [ ] All sensors working correctly
- [ ] No unused code or imports
- [ ] Strings extracted to strings.xml
- [ ] Colors defined in colors.xml
- [ ] Comments added to complex logic
- [ ] README.md is complete
- [ ] No hardcoded debugging text

### Code Quality
- [ ] No TODO/FIXME comments left
- [ ] Proper error handling implemented
- [ ] Null checks where needed
- [ ] No deprecated APIs used
- [ ] Consistent naming conventions

### Documentation
- [ ] README includes project overview
- [ ] README includes architecture diagram
- [ ] README includes user flow
- [ ] Comments explain sensor calibration thresholds
- [ ] Database schema documented

---

## Notes for Testers

1. **Location Testing**: Use mock location if GPS unavailable
2. **Sensor Testing**: Test on real device for accurate accelerometer data
3. **Long Trip Testing**: Test with 20+ minute trips to find edge cases
4. **Database Reset**: Clear app data to test fresh install scenario

---

**Document Version**: 1.0
**Last Updated**: May 28, 2026

