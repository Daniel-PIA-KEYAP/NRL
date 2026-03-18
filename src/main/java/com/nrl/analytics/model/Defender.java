package com.nrl.analytics.model;

/**
 * Represents a defending player involved in a match event.
 */
public class Defender {

    private int number;
    private int missedTackles;

    public Defender() {
    }

    public Defender(int number, int missedTackles) {
        this.number = number;
        this.missedTackles = missedTackles;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMissedTackles() {
        return missedTackles;
    }

    public void setMissedTackles(int missedTackles) {
        this.missedTackles = missedTackles;
    }

    @Override
    public String toString() {
        return "Defender{number=" + number + ", missedTackles=" + missedTackles + "}";
    }
}
