package mira.pharaon.adu.ac.ae.roadrage_group5;

public class PersonaManager {

    public static final String CHEETAH  = "Cheetah";
    public static final String BEAVER   = "Beaver";
    public static final String KANGAROO = "Kangaroo";
    public static final String RHINO    = "Rhino";

    // ─── Core scoring ─────────────────────────────────────────────────────────

    public String getPersona(int score) {
        if (score >= 80) return CHEETAH;
        if (score >= 60) return BEAVER;
        if (score >= 40) return KANGAROO;
        return RHINO;
    }

    // ─── Backward compatibility with old persona names ────────────────────────
    // Old DB records still use "The Surgeon" etc. — map them transparently.
    private String canonical(String persona) {
        switch (persona) {
            case "The Cheetah":   return CHEETAH;
            case "The Beaver": return BEAVER;
            case "The Kangaroo":  return KANGAROO;
            case "The Rhino":  return RHINO;
            default:              return persona;
        }
    }

    // ─── Display helpers ──────────────────────────────────────────────────────

    public String getPersonaEmoji(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:  return "🐆";
            case BEAVER:   return "🦫";
            case KANGAROO: return "🦘";
            default:       return "🦏";
        }
    }

    public String getPersonaColor(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:  return "#00695C"; // deep teal-green: precision
            case BEAVER:   return "#1565C0"; // deep blue: calm, reliable
            case KANGAROO: return "#E65100"; // deep orange: erratic, energetic
            default:       return "#B71C1C"; // deep red: aggressive
        }
    }

    public String getPersonaRange(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:  return "80 – 100";
            case BEAVER:   return "60 – 79";
            case KANGAROO: return "40 – 59";
            default:       return "0 – 39";
        }
    }

    /** Short message shown on the result screen */
    public String getPersonaMessage(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:  return "Flawless execution. You read the road before it happens.";
            case BEAVER:   return "Steady, reliable, and predictable. The road is safer with you on it.";
            case KANGAROO: return "You get there — but the ride is always a bit bumpy.";
            default:       return "The road isn't a personal challenge. Others exist out there.";
        }
    }

    // ─── Detail screen content ────────────────────────────────────────────────

    public String getPersonaOverview(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:
                return "You are a master of precision, focus, and efficiency. " +
                        "Every maneuver is deliberate. Every decision is calculated. " +
                        "Fast when necessary — but always completely in control.";
            case BEAVER:
                return "You value safety, predictability, and defensive driving above all else. " +
                        "You're the driver other people feel comfortable riding with. " +
                        "The road is calmer because you're on it.";
            case KANGAROO:
                return "Your driving style is a bit jumpy and inconsistent. " +
                        "You're not reckless, just... impulsive. You get there — " +
                        "but the people in the back seat notice every lurch.";
            default:
                return "You treat the road like a battleground and ignore the rules. " +
                        "Everyone else moves out of your way to avoid a crash. " +
                        "Your score reflects that.";
        }
    }

    public String getPersonaTraits(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:
                return "•  Flawless execution\n•  Perfect braking\n•  Highly alert at all times";
            case BEAVER:
                return "•  Calm under pressure\n•  Observant\n•  Completely risk-averse";
            case KANGAROO:
                return "•  Impatient\n•  Easily distracted\n•  Slightly erratic";
            default:
                return "•  Aggressive\n•  Heavy-footed\n•  Domineering";
        }
    }

    public String getPersonaHabits(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:
                return "Anticipates traffic changes before they happen. " +
                        "Brakes smoothly and early. Never wastes fuel.";
            case BEAVER:
                return "Maintains excellent following distance at all times. " +
                        "Keeps a steady, predictable pace that other drivers can read easily.";
            case KANGAROO:
                return "Sudden braking. Quick bursts of acceleration. " +
                        "Frequent and sometimes unnecessary lane changes.";
            default:
                return "Hard cornering. Persistent tailgating. " +
                        "Speeds through yellow lights and forces merges.";
        }
    }

    public String getPersonaVibe(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:  return "Fast when necessary, but always completely in control.";
            case BEAVER:   return "The most reliable driver on the road.";
            case KANGAROO: return "You get there, but the ride is always bumpy.";
            default:       return "Everyone else moves out of your way to avoid a crash.";
        }
    }

    public String getPersonaProfileA(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:
                return "Smooth and methodical. They naturally accelerate gently, maintain wide " +
                        "following distances, and always prioritize overall safety over getting " +
                        "somewhere a few minutes faster. Their score stays high because they " +
                        "never have to react — they anticipate.";
            case BEAVER:
                return "Cooperative, highly predictable, and defensive. They occasionally get " +
                        "minor dings for hard braking in unexpected city traffic, but generally " +
                        "remain incredibly safe and follow all rules to the letter.";
            case KANGAROO:
                return "Easily distracted and somewhat erratic. Kangaroos generally don't " +
                        "speed excessively, but they frequently lose points due to late braking, " +
                        "sudden stops, and moments of inattention.";
            default:
                return "Dominant, fast, and highly impatient. Rhinos see the road as a " +
                        "competition. Their scores suffer heavily from chronic speeding, " +
                        "tailgating, and forcing their way through intersections.";
        }
    }

    public String getPersonaProfileB(String persona) {
        switch (canonical(persona)) {
            case CHEETAH:
                return "Calm, highly predictable, and cautious. They maintain a flawless " +
                        "score by reading traffic patterns early, never speeding, and " +
                        "completely eliminating harsh braking events from their driving.";
            case BEAVER:
                return "Highly structured and methodical. They strictly adhere to the speed " +
                        "limit and match the exact flow of traffic, though they can get slightly " +
                        "dinged for tight cornering when taking familiar routes at pace.";
            case KANGAROO:
                return "Impatient and hyper-alert. They constantly switch lanes looking for " +
                        "the fastest path, which triggers frequent penalties for aggressive " +
                        "cornering and rapid acceleration.";
            default:
                return "Pure speed with zero impulse control. They treat everyday highways " +
                        "like a racetrack and consistently trigger extreme alerts for rapid " +
                        "acceleration and dangerous, heavy braking events.";
        }
    }
}