package com.coderacer.algo;

import org.springframework.stereotype.Component;

/**
 * K-factor is a concept from elo rating systems in games
 */
@Component
public class KFactorRatingAlgorithm implements RatingAlgorithm {

    private static final double K_LOW    = 20;   // for ratings < 200
    private static final double K_MEDIUM = 10;   // for ratings 200-400
    private static final double K_HIGH   = 5;    // for ratings > 400

    /**
     * @param currentRating  the player’s current MMR
     * @param perfScore      game performance (avgCpm * accuracyPct * difficultyMultiplier)
     * @return               delta to apply to MMR
     */
    @Override
    public int calculateDelta(int currentRating, int perfScore) {
        // pick K based on current rating
        double k = currentRating < 200
                ? K_LOW
                : (currentRating < 400 ? K_MEDIUM : K_HIGH);

        // simple scaled delta: higher perfScore yields larger fraction of K
        // e.g. perfScore=1000 => delta ~= K (but capped at K)
        double rawDelta = Math.round(perfScore / 500.0 * k);

        // cap so nobody gains more than K
        return (int) Math.min(rawDelta, k);
    }
}