package mira.pharaon.adu.ac.ae.roadrage_group5;

public class PersonaManager {

    public static final String SURGEON   = "The Surgeon";
    public static final String NAVIGATOR = "The Navigator";
    public static final String MAVERICK  = "The Maverick";
    public static final String CHAOS     = "Chaos Theory";

    public String getPersona(int score) {
        if (score >= 80) return SURGEON;
        if (score >= 60) return NAVIGATOR;
        if (score >= 40) return MAVERICK;
        return CHAOS;
    }

    /** Full message shown on the result screen */
    public String getPersonaMessage(String persona) {
        switch (persona) {
            case SURGEON:   return "Clinical precision. You read the road before it happens.";
            case NAVIGATOR: return "Steady hands, sound instincts. A few rough patches, always on course.";
            case MAVERICK:  return "You live fast. The road has notes.";
            default:        return "The road isn't a personal challenge. Others exist out there.";
        }
    }

    /** Short badge for History cards */
    public String getPersonaTagline(String persona) {
        switch (persona) {
            case SURGEON:   return "Pristine";
            case NAVIGATOR: return "Solid";
            case MAVERICK:  return "Risky";
            default:        return "Dangerous";
        }
    }

    /** Identity color — used for circle background + persona name text */
    public String getPersonaColor(String persona) {
        switch (persona) {
            case SURGEON:   return "#006A6C"; // teal
            case NAVIGATOR: return "#1565C0"; // deep blue
            case MAVERICK:  return "#E65100"; // deep orange
            default:        return "#B71C1C"; // deep red
        }
    }

    /** Emoji shown inside the persona circle */
    public String getPersonaEmoji(String persona) {
        switch (persona) {
            case SURGEON:   return "🎯";
            case NAVIGATOR: return "🧭";
            case MAVERICK:  return "⚡";
            default:        return "🌀";
        }
    }

    /** Score range label */
    public String getPersonaRange(String persona) {
        switch (persona) {
            case SURGEON:   return "80–100";
            case NAVIGATOR: return "60–79";
            case MAVERICK:  return "40–59";
            default:        return "0–39";
        }
    }
}