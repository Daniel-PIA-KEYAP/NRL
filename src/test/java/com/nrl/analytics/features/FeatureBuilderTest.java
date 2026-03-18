package com.nrl.analytics.features;

import com.nrl.analytics.model.Defender;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureBuilderTest {

    private FeatureBuilder featureBuilder;

    @BeforeEach
    void setUp() {
        featureBuilder = new FeatureBuilder();
    }

    @Test
    void testBuildFeaturesWithTwoDefenders() {
        Event event = new Event("TRY", Side.LEFT, 40, "NRL-001");
        event.addDefender(new Defender(2, 1));
        event.addDefender(new Defender(3, 2));

        FeatureVector fv = featureBuilder.buildFeatures(event);

        assertEquals(2, fv.getD1Number());
        assertEquals(3, fv.getD2Number());
        assertEquals(1, fv.getD1MissedTackles());
        assertEquals(2, fv.getD2MissedTackles());
        assertEquals(Side.LEFT.getCode(), fv.getSideCode());
        assertEquals(40.0 / 80.0, fv.getD1Fatigue(), 0.001);
        assertEquals(40.0 / 80.0, fv.getD2Fatigue(), 0.001);
    }

    @Test
    void testBuildFeaturesWithNoDefenders() {
        Event event = new Event("TRY", Side.RIGHT, 20, "NRL-002");

        FeatureVector fv = featureBuilder.buildFeatures(event);

        assertEquals(0, fv.getD1Number());
        assertEquals(0, fv.getD2Number());
        assertEquals(0, fv.getD1MissedTackles());
        assertEquals(0, fv.getD2MissedTackles());
        assertEquals(Side.RIGHT.getCode(), fv.getSideCode());
        assertEquals(20.0 / 80.0, fv.getD1Fatigue(), 0.001);
    }

    @Test
    void testBuildFeaturesWithOneDefender() {
        Event event = new Event("TRY", Side.MIDDLE, 60, "NRL-003");
        event.addDefender(new Defender(9, 3));

        FeatureVector fv = featureBuilder.buildFeatures(event);

        assertEquals(9, fv.getD1Number());
        assertEquals(0, fv.getD2Number());
        assertEquals(3, fv.getD1MissedTackles());
        assertEquals(0, fv.getD2MissedTackles());
        assertEquals(Side.MIDDLE.getCode(), fv.getSideCode());
    }

    @Test
    void testFatigueAtEndOfMatch() {
        Event event = new Event("TRY", Side.LEFT, 80, "NRL-004");
        FeatureVector fv = featureBuilder.buildFeatures(event);
        assertEquals(1.0, fv.getD1Fatigue(), 0.001);
    }

    @Test
    void testSideEncoding() {
        assertEquals(0, Side.LEFT.getCode());
        assertEquals(1, Side.RIGHT.getCode());
        assertEquals(2, Side.MIDDLE.getCode());
    }

    @Test
    void testToDoubleArray() {
        Event event = new Event("TRY", Side.RIGHT, 40, "NRL-005");
        event.addDefender(new Defender(4, 1));
        event.addDefender(new Defender(5, 2));

        FeatureVector fv = featureBuilder.buildFeatures(event);
        double[] arr = fv.toDoubleArray();

        assertEquals(7, arr.length);
        assertEquals(4.0, arr[0]);
        assertEquals(5.0, arr[1]);
        assertEquals(1.0, arr[2]);
        assertEquals(2.0, arr[3]);
        assertEquals(Side.RIGHT.getCode(), (int) arr[4]);
    }
}
