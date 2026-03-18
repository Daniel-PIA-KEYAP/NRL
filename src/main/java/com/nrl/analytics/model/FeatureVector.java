package com.nrl.analytics.model;

/**
 * Holds the ML feature vector extracted from a match event.
 */
public class FeatureVector {

    private int d1Number;
    private int d2Number;
    private int d1MissedTackles;
    private int d2MissedTackles;
    private int sideCode;
    private double d1Fatigue;
    private double d2Fatigue;

    public FeatureVector() {
    }

    public FeatureVector(int d1Number, int d2Number, int d1MissedTackles,
                         int d2MissedTackles, int sideCode,
                         double d1Fatigue, double d2Fatigue) {
        this.d1Number = d1Number;
        this.d2Number = d2Number;
        this.d1MissedTackles = d1MissedTackles;
        this.d2MissedTackles = d2MissedTackles;
        this.sideCode = sideCode;
        this.d1Fatigue = d1Fatigue;
        this.d2Fatigue = d2Fatigue;
    }

    public int getD1Number() {
        return d1Number;
    }

    public void setD1Number(int d1Number) {
        this.d1Number = d1Number;
    }

    public int getD2Number() {
        return d2Number;
    }

    public void setD2Number(int d2Number) {
        this.d2Number = d2Number;
    }

    public int getD1MissedTackles() {
        return d1MissedTackles;
    }

    public void setD1MissedTackles(int d1MissedTackles) {
        this.d1MissedTackles = d1MissedTackles;
    }

    public int getD2MissedTackles() {
        return d2MissedTackles;
    }

    public void setD2MissedTackles(int d2MissedTackles) {
        this.d2MissedTackles = d2MissedTackles;
    }

    public int getSideCode() {
        return sideCode;
    }

    public void setSideCode(int sideCode) {
        this.sideCode = sideCode;
    }

    public double getD1Fatigue() {
        return d1Fatigue;
    }

    public void setD1Fatigue(double d1Fatigue) {
        this.d1Fatigue = d1Fatigue;
    }

    public double getD2Fatigue() {
        return d2Fatigue;
    }

    public void setD2Fatigue(double d2Fatigue) {
        this.d2Fatigue = d2Fatigue;
    }

    /** Returns features as a double array for use with Weka. */
    public double[] toDoubleArray() {
        return new double[]{
                d1Number, d2Number,
                d1MissedTackles, d2MissedTackles,
                sideCode,
                d1Fatigue, d2Fatigue
        };
    }

    @Override
    public String toString() {
        return "FeatureVector{"
                + "d1Number=" + d1Number
                + ", d2Number=" + d2Number
                + ", d1MissedTackles=" + d1MissedTackles
                + ", d2MissedTackles=" + d2MissedTackles
                + ", sideCode=" + sideCode
                + ", d1Fatigue=" + String.format("%.3f", d1Fatigue)
                + ", d2Fatigue=" + String.format("%.3f", d2Fatigue)
                + "}";
    }
}
