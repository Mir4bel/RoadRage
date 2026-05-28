# RoadRage UI/UX Design System & Guidelines

## Design Philosophy

RoadRage follows Google's **Material Design 3 (Material You)** principles to create a modern, accessible, and intuitive user interface. The design emphasizes clarity, safety, and user engagement while maintaining consistency across all screens.

### Core Design Principles
1. **Safety First**: Clear visual hierarchy highlights important driving metrics
2. **Real-Time Feedback**: Instant visual updates for sensor data
3. **Minimalism**: Remove unnecessary elements to reduce cognitive load
4. **Accessibility**: High contrast colors, large tap targets (64dp minimum)
5. **Consistency**: Unified design language across all screens

---

## Color System

### Primary Palette
Used for main navigation, buttons, and emphasis elements.

| Color | Usage | Hex Code | RGB |
|-------|-------|----------|-----|
| Primary | App bar, primary buttons, highlights | #1B1B1F | 27, 27, 31 |
| On Primary | Text on primary backgrounds | #FFFFFF | 255, 255, 255 |
| Primary Container | Card backgrounds, subtle emphasis | #E8DEF8 | 232, 222, 248 |
| On Primary Container | Text on container backgrounds | #1B1B1F | 27, 27, 31 |

### Secondary Palette
Used for warnings, alerts, and secondary actions.

| Color | Usage | Hex Code | RGB |
|-------|-------|----------|-----|
| Secondary | Warning icons, alert states | #FF6B35 | 255, 107, 53 |
| On Secondary | Text on secondary backgrounds | #FFFFFF | 255, 255, 255 |
| Secondary Container | Warning card backgrounds | #FFE0CC | 255, 224, 204 |
| On Secondary Container | Text on secondary containers | #652B00 | 101, 43, 0 |

### Tertiary Palette
Used for accents and alternative highlighting.

| Color | Usage | Hex Code | RGB |
|-------|-------|----------|-----|
| Tertiary | Accent buttons, secondary highlights | #006A6C | 0, 106, 108 |
| Tertiary Container | Accent backgrounds | #A0F0F2 | 160, 240, 242 |

### Error Palette
Used for errors, overseeding, and critical alerts.

| Color | Usage | Hex Code | RGB |
|-------|-------|----------|-----|
| Error | Error text, critical alerts | #B3261E | 179, 38, 30 |
| Error Container | Error backgrounds | #F9DEDC | 249, 222, 220 |
| On Error Container | Error text | #410E0B | 65, 14, 11 |

### Neutral Palette
Used for surfaces, dividers, and disabled states.

| Color | Usage | Hex Code | RGB |
|-------|-------|----------|-----|
| Surface | Main background | #FDFBFF | 253, 251, 255 |
| On Surface | Primary text | #1C1B1F | 28, 27, 31 |
| Surface Variant | Card borders, subtle backgrounds | #E7E0EC | 231, 224, 236 |
| On Surface Variant | Secondary text | #49454F | 73, 69, 79 |
| Outline | Borders, dividers | #79747E | 121, 116, 126 |

---

## Typography System

### Font Family
- **Primary Font**: Roboto (Material3 default)
- **Fallback**: Sans-serif system font

### Type Scale

| Usage | Size | Weight | Line Height | Letter Spacing |
|-------|------|--------|-------------|----------------|
| Display Large | 57sp | Medium | 64sp | -0.25sp |
| Display Medium | 45sp | Medium | 52sp | 0sp |
| Display Small | 36sp | Medium | 44sp | 0sp |
| Headline Large | 32sp | Medium | 40sp | 0sp |
| Headline Medium | 28sp | Medium | 36sp | 0sp |
| **Headline Small** | 24sp | Medium | 32sp | 0sp |
| Title Large | 22sp | Medium | 28sp | 0sp |
| **Title Medium** | 16sp | Medium | 24sp | 0.15sp |
| **Title Small** | 14sp | Medium | 20sp | 0.1sp |
| **Body Large** | 16sp | Regular | 24sp | 0.5sp |
| **Body Medium** | 14sp | Regular | 20sp | 0.25sp |
| **Body Small** | 12sp | Regular | 16sp | 0.4sp |
| **Label Large** | 14sp | Medium | 20sp | 0.1sp |
| **Label Medium** | 12sp | Medium | 16sp | 0.5sp |
| Label Small | 11sp | Medium | 16sp | 0.5sp |

**Bolded rows** = Used in RoadRage app

### Actual App Usage

| Component | Size | Weight | Notes |
|-----------|------|--------|-------|
| Screen Titles (e.g., "Road Rage") | 28sp | Medium | High contrast, attention-grabbing |
| Subtitles (e.g., "Drive smarter...") | 16sp | Regular | Secondary information |
| Card Labels (e.g., "AVERAGE SCORE") | 11sp | Medium | Uppercase, letter-spacing |
| Card Values (e.g., Score number) | 48-80sp | Medium | Prominent data display |
| Trip Item Date | 14sp | Medium | Primary trip info |
| Trip Item Meta | 12-13sp | Regular | Secondary trip info |
| Button Text | 16-18sp | Medium | Call-to-action emphasis |

---

## Component Design

### Buttons

#### Material Button (Filled)
- **Background**: Primary color or Secondary for warnings
- **Text Color**: On-Primary color
- **Height**: 64dp (standard), 48dp (compact)
- **Corner Radius**: 16dp
- **Padding**: 24dp horizontal, 8dp vertical
- **State**: Pressed = 12% darker, Disabled = 38% opacity
- **Usage**: Primary actions (Start Trip, Start Tracking, Back to Home)

#### Outlined Button
- **Border**: 1.5dp, primary color
- **Background**: Transparent
- **Text Color**: Primary color
- **Height**: 64dp
- **Corner Radius**: 16dp
- **Usage**: Secondary actions (mood selection)
- **Selected State**: Thicker stroke (6dp), darker background

### Cards

#### Stat Cards
- **Corner Radius**: 16-20dp
- **Elevation**: 0dp (flat design)
- **Padding**: 16-24dp
- **Border**: 1dp stroke for outlined cards
- **Background**: Primary/Secondary/Tertiary container colors
- **Text Color**: On-Container colors

Examples:
- Persona card: Secondary container
- Average score card: Primary container
- Speed card: Primary container
- Events card: Error container
- Trip item: Surface with outline stroke

### Text Fields
- **Height**: 56dp
- **Corner Radius**: 8dp
- **Border**: 1dp outline, tertiary color
- **Label**: Floating, 12sp
- **Cursor**: Primary color

### Navigation Elements

#### Bottom Navigation
- **Height**: 80dp (with padding)
- **Background**: Surface color
- **Item Height**: 56dp
- **Icon Size**: 24dp
- **Label**: 12sp (below icon)
- **Item Width**: Equal distribution
- **Active Indicator**: Primary color, rounded background
- **Inactive**: On-surface variant color

#### List Items (RecyclerView)
- **Height**: Minimum 56dp (touch target)
- **Padding**: 16dp horizontal, varies vertical
- **Divider**: None (card-based design)
- **Card Wrapper**: 16dp margin, 16dp corner radius

---

## Spacing System

All spacing follows an 4dp base unit grid:

| Multiple | Size | Usage |
|----------|------|-------|
| 0.5x | 8dp | Small padding inside components |
| 1x | 16dp | Standard component padding |
| 1.5x | 24dp | Screen-level padding |
| 2x | 32dp | Large spacing between sections |
| 3x | 48dp | Very large gaps |

### Padding Standards
- **Screen**: 24dp top/bottom, 24dp sides
- **Card**: 16-24dp internal
- **Button**: 24dp horizontal, 8dp vertical
- **List Item**: 16dp sides, 12dp vertical

### Margin Standards
- **Cards**: 16dp margin-bottom between cards
- **Buttons**: 16dp margin-bottom
- **Section Gap**: 32dp between major sections

---

## Icon System

### System Icons
Using Android built-in drawable resources:
- Home: `ic_menu_compass`
- History: `ic_menu_recent_history`
- Profile: `ic_menu_myplaces`

### Custom Icons (Emojis in Buttons)
- 😊 Happy mood
- 😐 Neutral mood
- 😴 Tired mood
- 😣 Stressed mood

### Icon Sizing
- **Navigation**: 24dp
- **Buttons**: Inline with text (system default)
- **Cards**: 32-48dp when used

---

## Screen Layouts

### Home Screen
```
┌─────────────────┐
│ Road Rage       │
│ Drive smarter...│
├─────────────────┤
│   PERSONA CARD  │
│   Zen Driver    │
├─────────────────┤
│   AVG SCORE     │
│       85        │
├─────────────────┤
│   START TRIP    │
│      BUTTON     │
├─────────────────┤ (Scrollable above)
│  Home | Hist... │ (Fixed bottom nav)
└─────────────────┘
```

### Trip Screen
```
┌─────────────────┐
│Trip in Progress │
├─────────────────┤
│  SPEED CARD     │
│    75 km/h      │
├─────────────────┤
│  EVENTS CARD    │
│      2          │
├─────────────────┤
│  STOP TRIP BTN  │
├─────────────────┤
│  Home | Hist... │ (Fixed bottom nav)
└─────────────────┘
```

### History Screen
```
┌─────────────────┐
│  Trip History   │
├─────────────────┤
│ ┌─────────────┐ │
│ │May 27  |100 │ │
│ │Zen Driver   │ │
│ │😊 Happy     │ │
│ └─────────────┘ │
│ ┌─────────────┐ │
│ │May 26   | 92│ │
│ │Zen Driver   │ │
│ │😐 Neutral   │ │
│ └─────────────┘ │
│ (Scrollable)    │
├─────────────────┤
│  Home | Hist... │ (Fixed bottom nav)
└─────────────────┘
```

---

## Animation Guidelines

### Transition Animations
- **Between tabs**: No animation (0ms)
- **Between activities**: System default cross-fade
- **Button press**: 100ms ripple effect (Material3 default)

### State Animations
- **Mood button selection**: 200ms stroke animation
- **Speed updates**: Instant (real-time data)
- **Event counter**: Instant increment

---

## Accessibility Considerations

### Touch Targets
- **Minimum size**: 48dp × 48dp (standard), 64dp for primary buttons
- **Spacing**: 8dp minimum between touchable elements
- **Buttons**: Full-width on mobile for easy tapping

### Color Contrast
- **Text on Primary**: #FFFFFF on #1B1B1F = 17.5:1 ✓ (AAA)
- **Text on Secondary**: #FFFFFF on #FF6B35 = 5.1:1 ✓ (AA)
- **Text on Surface**: #1C1B1F on #FDFBFF = 17.1:1 ✓ (AAA)

### Text & Readability
- **Minimum text size**: 12sp
- **Line height**: 1.5x font size minimum
- **Letter spacing**: Added to labels for clarity

### Semantic Labels
- All buttons have descriptive text labels
- Card headers use uppercase for visual distinction
- Icons accompanied by text (no icon-only buttons)

---

## Dark Mode (values-night/)

The app uses Material3 theme which automatically handles dark mode:
- **Primary**: Lighter shade (#E8DEF8)
- **Surface**: Dark gray (#121216)
- **Text**: Light color (#F5EFF7)

Dark mode is automatically applied when system dark mode is enabled.

---

## Responsive Design

### Phone Layout (Vertical)
- Full width cards (24dp padding)
- Single column layout
- Bottom navigation always visible
- Scrollable content area

### Tablet Layout (Landscape)
- Same card-based layout
- May use split-view for future multi-pane design
- Bottom navigation can be side navigation (future)

### Screen Size Breakpoints
- **Small** (< 480dp): Compact spacing (16dp)
- **Medium** (480-720dp): Standard spacing (24dp)
- **Large** (> 720dp): Standard spacing with max width constraints

---

## Motion & Interaction Guidelines

### Gesture Support
- **Tap**: All buttons and navigation items
- **Scroll**: Content areas (Home, History, Profile)
- **Swipe**: Not used (could be added for tab switching in future)
- **Long-press**: Not used

### Feedback
- **Visual**: Color change, button ripple
- **Audio**: System click sound (if enabled)
- **Haptic**: Optional vibration on button press

---

## Design System Extension Points

For future features, maintain these principles:

### New Screens
- Use same color palette
- Apply type scale consistently
- Maintain 16dp grid spacing
- Include bottom navigation or app bar

### New Components
- Minimum 48dp touch target
- Card-based layouts preferred
- Consistent corner radius (8-16dp)
- Use existing color system

### Custom Assets
- SVG format preferred (scalable)
- Icon set: 24dp standard size
- Color: Use palette colors, not custom colors
- Stroke width: 2dp for outlines

---

## References

- Material3 Design System: https://m3.material.io
- Android Design Guidelines: https://developer.android.com/design
- Color Tool: https://m3.material.io/resources/color-tools-and-libraries

---

**Document Version**: 1.0
**Last Updated**: May 28, 2026
**Author**: RoadRage Design Team

