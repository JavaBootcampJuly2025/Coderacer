package com.coderacer.algo;

public interface RatingAlgorithm {
    /**
     * Compute how much to add (or subtract) from currentRating.
     *
     * @param currentRating  the player’s current MMR
     * @param perfScore      game performance (avgCpm * accuracyPct * difficultyMultiplier)
     * @return               delta to apply to MMR
     */
     int calculateDelta(int currentRating, int perfScore);
}