package com.nrl.analytics.analytics;

import com.nrl.analytics.model.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerWeaknessCalculatorTest {

    private PlayerWeaknessCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PlayerWeaknessCalculator();
    }

    @Test
    void testCalculateWeakness() {
        // weakness = (3 * 2) + 2 + 0.5 = 8.5
        PlayerStats stats = new PlayerStats(2, 3, 2, 0.5);
        double weakness = calculator.calculateWeakness(stats);
        assertEquals(8.5, weakness, 0.001);
    }

    @Test
    void testZeroWeakness() {
        PlayerStats stats = new PlayerStats(4, 0, 0, 0.0);
        double weakness = calculator.calculateWeakness(stats);
        assertEquals(0.0, weakness, 0.001);
    }

    @Test
    void testFatigueOnlyWeakness() {
        PlayerStats stats = new PlayerStats(9, 0, 0, 0.75);
        double weakness = calculator.calculateWeakness(stats);
        assertEquals(0.75, weakness, 0.001);
    }

    @Test
    void testTriesConcededDoubled() {
        PlayerStats stats = new PlayerStats(3, 5, 0, 0.0);
        double weakness = calculator.calculateWeakness(stats);
        assertEquals(10.0, weakness, 0.001);
    }

    @Test
    void testMissedTacklesAdded() {
        PlayerStats stats = new PlayerStats(5, 0, 4, 0.0);
        double weakness = calculator.calculateWeakness(stats);
        assertEquals(4.0, weakness, 0.001);
    }
}
