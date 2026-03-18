package com.nrl.analytics.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrl.analytics.model.Defender;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.Side;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm 2: JSON Parsing.
 *
 * <p>Converts raw JSON received from the API into a list of {@link Event} objects.
 *
 * <p>Expected JSON structure:
 * <pre>
 * {
 *   "events": [
 *     {
 *       "matchId": "NRL-2024-001",
 *       "type": "TRY",
 *       "side": "LEFT",
 *       "minute": 23,
 *       "defenders": [
 *         { "number": 2, "missedTackles": 1 },
 *         { "number": 3, "missedTackles": 0 }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 */
public class MatchDataParser {

    private final ObjectMapper objectMapper;

    public MatchDataParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parses a JSON string into a list of {@link Event} objects.
     *
     * @param json the raw JSON string from the API
     * @return list of parsed events (may be empty, never {@code null})
     * @throws IOException if the JSON is malformed or cannot be read
     */
    public List<Event> parseMatchData(String json) throws IOException {
        List<Event> events = new ArrayList<>();

        JsonNode root = objectMapper.readTree(json);
        JsonNode eventsNode = root.path("events");

        if (eventsNode.isMissingNode() || !eventsNode.isArray()) {
            return events;
        }

        for (JsonNode eventNode : eventsNode) {
            Event event = parseEvent(eventNode);
            events.add(event);
        }

        return events;
    }

    private Event parseEvent(JsonNode node) {
        String type = node.path("type").asText("UNKNOWN");
        String sideStr = node.path("side").asText("MIDDLE");
        int minute = node.path("minute").asInt(0);
        String matchId = node.path("matchId").asText("");

        Side side;
        try {
            side = Side.fromString(sideStr);
        } catch (IllegalArgumentException e) {
            side = Side.MIDDLE;
        }

        Event event = new Event(type, side, minute, matchId);

        JsonNode defendersNode = node.path("defenders");
        if (defendersNode.isArray()) {
            for (JsonNode defNode : defendersNode) {
                int number = defNode.path("number").asInt(0);
                int missedTackles = defNode.path("missedTackles").asInt(0);
                event.addDefender(new Defender(number, missedTackles));
            }
        }

        return event;
    }
}
