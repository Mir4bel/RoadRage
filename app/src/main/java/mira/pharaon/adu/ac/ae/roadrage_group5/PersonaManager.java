package mira.pharaon.adu.ac.ae.roadrage_group5;

public class PersonaManager {
    public String getPersona(int score) {
        if (score >= 80) return "Zen Driver";
        if (score >= 60) return "City Cruiser";
        if (score >= 40) return "The Rocket";
        return "Ghost Rider";
    }

    public String getPersonaMessage(String persona) {
        switch (persona) {
            case "Zen Driver":    return "Smooth and consistent. You're a natural.";
            case "City Cruiser":  return "Pretty good driving with a few rough moments.";
            case "The Rocket":    return "Fast and unpredictable. Slow it down.";
            default:              return "Multiple harsh events detected. Drive safer.";
        }
    }
}
