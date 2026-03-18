package com.nrl.analytics.parser;

import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchDataParserTest {

    private MatchDataParser parser;

    @BeforeEach
    void setUp() {
        parser = new MatchDataParser();
    }

    @Test
    void testParseValidJson() throws IOException {
        String json = """
                {
                  "events": [
                    {
                      "matchId": "NRL-2024-001",
                      "type": "TRY",
                      "side": "LEFT",
                      "minute": 23,
                      "defenders": [
                        { "number": 2, "missedTackles": 1 },
                        { "number": 3, "missedTackles": 0 }
                      ]
                    }
                  ]
                }
                """;

        List<Event> events = parser.parseMatchData(json);

        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals("TRY", event.getType());
        assertEquals(Side.LEFT, event.getSide());
        assertEquals(23, event.getMinute());
        assertEquals("NRL-2024-001", event.getMatchId());
        assertEquals(2, event.getDefenders().size());
        assertEquals(2, event.getDefenders().get(0).getNumber());
        assertEquals(1, event.getDefenders().get(0).getMissedTackles());
        assertEquals(3, event.getDefenders().get(1).getNumber());
        assertEquals(0, event.getDefenders().get(1).getMissedTackles());
    }

    @Test
    void testParseMultipleEvents() throws IOException {
        String json = """
                {
                  "events": [
                    { "matchId": "M1", "type": "TRY", "side": "RIGHT", "minute": 10, "defenders": [] },
                    { "matchId": "M1", "type": "TRY", "side": "MIDDLE", "minute": 35, "defenders": [] }
                  ]
                }
                """;

        List<Event> events = parser.parseMatchData(json);
        assertEquals(2, events.size());
        assertEquals(Side.RIGHT, events.get(0).getSide());
        assertEquals(Side.MIDDLE, events.get(1).getSide());
    }

    @Test
    void testParseEmptyEventsList() throws IOException {
        String json = "{ \"events\": [] }";
        List<Event> events = parser.parseMatchData(json);
        assertTrue(events.isEmpty());
    }

    @Test
    void testParseMissingEventsKey() throws IOException {
        String json = "{}";
        List<Event> events = parser.parseMatchData(json);
        assertTrue(events.isEmpty());
    }

    @Test
    void testParseUnknownSideDefaultsToMiddle() throws IOException {
        String json = """
                {
                  "events": [
                    { "matchId": "M1", "type": "TRY", "side": "UNKNOWN", "minute": 5, "defenders": [] }
                  ]
                }
                """;

        List<Event> events = parser.parseMatchData(json);
        assertEquals(1, events.size());
        assertEquals(Side.MIDDLE, events.get(0).getSide());
    }

    @Test
    void testParseInvalidJsonThrowsIOException() {
        assertThrows(IOException.class, () -> parser.parseMatchData("not-json"));
    }

    @Test
    void testParseAllSideValues() throws IOException {
        for (String side : new String[]{"LEFT", "RIGHT", "MIDDLE"}) {
            String json = String.format(
                    "{\"events\":[{\"matchId\":\"M1\",\"type\":\"TRY\",\"side\":\"%s\",\"minute\":1,\"defenders\":[]}]}",
                    side);
            List<Event> events = parser.parseMatchData(json);
            assertEquals(Side.fromString(side), events.get(0).getSide());
        }
    }
}
