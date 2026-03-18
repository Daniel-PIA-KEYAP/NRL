package com.nrl.analytics.analytics;

import com.nrl.analytics.model.PlayerStats;

/**
 * Algorithm 7: Player Weakness Rating.
 *
 * <p>Calculates a composite weakness score for an individual player based on
 * tries conceded, missed tackles, and fatigue.
 */
public class PlayerWeaknessCalculator {

    /**
     * Calculates the weakness score for a player.
     *
     * <p>Formula:
     * <pre>
     *   weakness = (triesConceded * 2) + missedTackles + fatigueFactor
     * </pre>
     *
     * @param stats the player's accumulated statistics
     * @return the composite weakness score (higher = weaker)
     */
    public double calculateWeakness(PlayerStats stats) {
        double weakness = (stats.getTriesConceded() * 2.0)
                + stats.getMissedTackles()
                + stats.getFatigueFactor();
        return weakness;
    }
}
