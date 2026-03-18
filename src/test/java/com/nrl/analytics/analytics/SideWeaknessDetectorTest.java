package com.nrl.analytics.analytics;

import com.nrl.analytics.model.PlayerStats;
import com.nrl.analytics.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SideWeaknessDetectorTest {

    private SideWeaknessDetector detector;

    @BeforeEach
    void setUp() {
        detector = new SideWeaknessDetector();
    }

    @Test
    void testLeftSideWeakest() {
        // Players 2 and 3 have high weakness → LEFT
        List<PlayerStats> players = Arrays.asList(
                new PlayerStats(2, 5, 3, 0.8),  // weakness = 13.8
                new PlayerStats(3, 3, 2, 0.5),  // weakness = 8.5
                new PlayerStats(4, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(5, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(9, 1, 1, 0.2)   // weakness = 3.2
        );
        assertEquals(Side.LEFT, detector.detectWeakSide(players));
    }

    @Test
    void testRightSideWeakest() {
        // Players 4 and 5 have high weakness → RIGHT
        List<PlayerStats> players = Arrays.asList(
                new PlayerStats(2, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(3, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(4, 4, 3, 0.6),  // weakness = 11.6
                new PlayerStats(5, 2, 2, 0.4),  // weakness = 6.4
                new PlayerStats(9, 0, 1, 0.1)   // weakness = 1.1
        );
        assertEquals(Side.RIGHT, detector.detectWeakSide(players));
    }

    @Test
    void testMiddleSideWeakest() {
        // Other players have highest combined weakness → MIDDLE
        List<PlayerStats> players = Arrays.asList(
                new PlayerStats(2, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(3, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(4, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(5, 0, 0, 0.0),  // weakness = 0
                new PlayerStats(9, 5, 4, 0.9),  // weakness = 14.9
                new PlayerStats(1, 3, 2, 0.5)   // weakness = 8.5
        );
        assertEquals(Side.MIDDLE, detector.detectWeakSide(players));
    }

    @Test
    void testEmptyPlayerListReturnsMiddle() {
        assertEquals(Side.MIDDLE, detector.detectWeakSide(Collections.emptyList()));
    }

    @Test
    void testTieDefaultsToMiddle() {
        // All scores equal → MIDDLE (else branch)
        List<PlayerStats> players = Arrays.asList(
                new PlayerStats(2, 0, 0, 0.0),
                new PlayerStats(4, 0, 0, 0.0),
                new PlayerStats(9, 0, 0, 0.0)
        );
        assertEquals(Side.MIDDLE, detector.detectWeakSide(players));
    }
}
