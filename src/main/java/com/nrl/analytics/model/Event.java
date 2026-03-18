package com.nrl.analytics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a match event (e.g. a try being scored).
 */
public class Event {

    private String type;
    private Side side;
    private int minute;
    private String matchId;
    private List<Defender> defenders = new ArrayList<>();

    public Event() {
    }

    public Event(String type, Side side, int minute, String matchId) {
        this.type = type;
        this.side = side;
        this.minute = minute;
        this.matchId = matchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public List<Defender> getDefenders() {
        return defenders;
    }

    public void setDefenders(List<Defender> defenders) {
        this.defenders = defenders;
    }

    public void addDefender(Defender defender) {
        this.defenders.add(defender);
    }

    @Override
    public String toString() {
        return "Event{type='" + type + "', side=" + side + ", minute=" + minute
                + ", matchId='" + matchId + "', defenders=" + defenders + "}";
    }
}
