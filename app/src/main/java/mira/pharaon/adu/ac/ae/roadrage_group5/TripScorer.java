package mira.pharaon.adu.ac.ae.roadrage_group5;


public class TripScorer {
    public int calculateScore(int eventCount, int durationSeconds) {
        int score = 100;
        score -= eventCount * 8; // lose 8 points per harsh event
        // Bonus: perfect trip over 5 minutes
        if (eventCount == 0 && durationSeconds >= 300) score = 100;
        return Math.max(0, score);
    }
}