package com.nrl.analytics.analytics;

import com.nrl.analytics.model.PlayerStats;
import com.nrl.analytics.model.Side;

import java.util.List;

/**
 * Algorithm 8: Side Weakness Detection.
 *
 * <p>Analyses the weakness scores of players in each defensive position to
 * determine which side of the field is most vulnerable.
 *
 * <p>Player numbering convention used:
 * <ul>
 *   <li>LEFT  – players numbered 2 and 3</li>
 *   <li>RIGHT – players numbered 4 and 5</li>
 *   <li>MIDDLE – all other players</li>
 * </ul>
 */
public class SideWeaknessDetector {

    private final PlayerWeaknessCalculator weaknessCalculator;

    public SideWeaknessDetector() {
        this.weaknessCalculator = new PlayerWeaknessCalculator();
    }

    public SideWeaknessDetector(PlayerWeaknessCalculator weaknessCalculator) {
        this.weaknessCalculator = weaknessCalculator;
    }

    /**
     * Detects the weakest defensive side from a list of player statistics.
     *
     * @param players list of player statistics
     * @return the weakest {@link Side}
     */
    public Side detectWeakSide(List<PlayerStats> players) {
        double leftScore = 0.0;
        double rightScore = 0.0;
        double middleScore = 0.0;

        for (PlayerStats player : players) {
            double weakness = weaknessCalculator.calculateWeakness(player);
            int number = player.getPlayerNumber();

            if (number == 2 || number == 3) {
                leftScore += weakness;
            } else if (number == 4 || number == 5) {
                rightScore += weakness;
            } else {
                middleScore += weakness;
            }
        }

        if (leftScore > rightScore && leftScore > middleScore) {
            return Side.LEFT;
        } else if (rightScore > leftScore && rightScore > middleScore) {
            return Side.RIGHT;
        } else {
            return Side.MIDDLE;
        }
    }
}
