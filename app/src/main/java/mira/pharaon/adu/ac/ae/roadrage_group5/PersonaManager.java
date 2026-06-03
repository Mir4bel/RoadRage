package mira.pharaon.adu.ac.ae.roadrage_group5;

public class PersonaManager {

    public String getPersona(int score) {
        if (score >= 80) return "Guardian Angel";
        if (score >= 60) return "Road Master";
        if (score >= 40) return "Speed Demon";
        return "Crash Test Dummy";
    }

    public String getPersonaMessage(String persona) {
        switch (persona) {
            case "Guardian Angel":
                return "You're the safest driver on the road. Your attention to detail and smooth driving prevent accidents. Keep it up!";
            case "Road Master":
                return "You've got solid driving skills. A few adjustments and you'll be a Guardian Angel. Focus on smoother turns and gentle braking.";
            case "Speed Demon":
                return "You're driving with aggression. Slow down, be mindful of your speed and sudden maneuvers. Safety first!";
            default:
                return "Your driving needs immediate attention. Multiple harsh events and unsafe speeds detected. Please drive more carefully.";
        }
    }

    public int getPersonaColor(String persona) {
        switch (persona) {
            case "Guardian Angel":    return 0xFF4CAF50; // Green
            case "Road Master":       return 0xFF2196F3; // Blue
            case "Speed Demon":       return 0xFFFFC107; // Amber
            default:                  return 0xFFF44336; // Red
        }
    }

    public int getPersonaColorForScore(int score) {
        return getPersonaColor(getPersona(score));
    }
}


