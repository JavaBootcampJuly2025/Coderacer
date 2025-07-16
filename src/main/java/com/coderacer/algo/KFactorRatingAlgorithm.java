package com.coderacer.algo;

import org.springframework.stereotype.Component;

// TODO tweak constants. As it is now, in my opinion the gain rate is too slow (50 games of perfect performance for K_MEDIUM bracket??)
// TODO also "double rawDelta = perfScore / 1000.0 * k;" has a magical number (extract the const!), check out what's a typical score, adjust the "1000"
/**
 * K-factor is a concept from elo rating systems in games
 */
@Component
public class KFactorRatingAlgorithm implements RatingAlgorithm {

    private static final double K_LOW    = 20;   // for ratings < 1000
    private static final double K_MEDIUM = 10;   // for ratings 1k–2k
    private static final double K_HIGH   = 5;    // for ratings >2k

    /**
     * @param currentRating  the player’s current MMR
     * @param perfScore      game performance (avgCpm * accuracyPct / 100 * difficultyMultiplier)
     * @return               delta to apply to MMR
     */
    @Override
    public int calculateDelta(int currentRating, int perfScore) {
        // pick K based on current rating
        double k = currentRating < 1000
                ? K_LOW
                : (currentRating < 2000 ? K_MEDIUM : K_HIGH);

        // simple scaled delta: higher perfScore yields larger fraction of K
        // e.g. perfScore=1000 => delta ~= K (but capped at K)
        double rawDelta = perfScore / 1000.0 * k;

        // cap so nobody gains more than K
        return (int) Math.min(rawDelta, k);
    }
}