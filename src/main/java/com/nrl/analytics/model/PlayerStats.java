package com.nrl.analytics.model;

/**
 * Holds accumulated statistics for a player used in weakness calculation.
 */
public class PlayerStats {

    private int playerNumber;
    private int triesConceded;
    private int missedTackles;
    private double fatigueFactor;

    public PlayerStats() {
    }

    public PlayerStats(int playerNumber, int triesConceded, int missedTackles,
                       double fatigueFactor) {
        this.playerNumber = playerNumber;
        this.triesConceded = triesConceded;
        this.missedTackles = missedTackles;
        this.fatigueFactor = fatigueFactor;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getTriesConceded() {
        return triesConceded;
    }

    public void setTriesConceded(int triesConceded) {
        this.triesConceded = triesConceded;
    }

    public int getMissedTackles() {
        return missedTackles;
    }

    public void setMissedTackles(int missedTackles) {
        this.missedTackles = missedTackles;
    }

    public double getFatigueFactor() {
        return fatigueFactor;
    }

    public void setFatigueFactor(double fatigueFactor) {
        this.fatigueFactor = fatigueFactor;
    }

    @Override
    public String toString() {
        return "PlayerStats{playerNumber=" + playerNumber
                + ", triesConceded=" + triesConceded
                + ", missedTackles=" + missedTackles
                + ", fatigueFactor=" + String.format("%.3f", fatigueFactor) + "}";
    }
}
