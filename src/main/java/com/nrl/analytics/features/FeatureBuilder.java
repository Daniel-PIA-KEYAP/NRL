package com.nrl.analytics.features;

import com.nrl.analytics.model.Defender;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.FeatureVector;

import java.util.List;

/**
 * Algorithm 3: Feature Engineering.
 *
 * <p>Converts a match {@link Event} into an ML {@link FeatureVector}.
 *
 * <p>The feature vector contains:
 * <ul>
 *   <li>d1_number       – jersey number of first defender</li>
 *   <li>d2_number       – jersey number of second defender</li>
 *   <li>d1_missedTackles – missed tackles by first defender</li>
 *   <li>d2_missedTackles – missed tackles by second defender</li>
 *   <li>side            – encoded: LEFT=0, RIGHT=1, MIDDLE=2</li>
 *   <li>d1_fatigue      – minute / 80 for first defender</li>
 *   <li>d2_fatigue      – minute / 80 for second defender</li>
 * </ul>
 */
public class FeatureBuilder {

    private static final double MATCH_DURATION = 80.0;

    /**
     * Builds a {@link FeatureVector} from the given event.
     *
     * <p>If the event has fewer than two defenders, missing values default to 0.
     *
     * @param event the match event to extract features from
     * @return the constructed feature vector
     */
    public FeatureVector buildFeatures(Event event) {
        List<Defender> defenders = event.getDefenders();

        int d1Number = 0;
        int d2Number = 0;
        int d1MissedTackles = 0;
        int d2MissedTackles = 0;

        if (defenders.size() >= 1) {
            d1Number = defenders.get(0).getNumber();
            d1MissedTackles = defenders.get(0).getMissedTackles();
        }
        if (defenders.size() >= 2) {
            d2Number = defenders.get(1).getNumber();
            d2MissedTackles = defenders.get(1).getMissedTackles();
        }

        int sideCode = event.getSide().getCode();

        double fatigue = event.getMinute() / MATCH_DURATION;
        double d1Fatigue = fatigue;
        double d2Fatigue = fatigue;

        return new FeatureVector(
                d1Number, d2Number,
                d1MissedTackles, d2MissedTackles,
                sideCode,
                d1Fatigue, d2Fatigue
        );
    }
}
