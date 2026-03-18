package com.nrl.analytics.storage;

import com.nrl.analytics.model.Defender;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = DataStorage.inMemory();
    }

    @Test
    void testStoreAndLoadEvent() {
        Event event = new Event("TRY", Side.LEFT, 25, "NRL-001");
        event.addDefender(new Defender(2, 1));
        event.addDefender(new Defender(3, 0));

        storage.storeMatchEvent(event, Side.RIGHT);

        List<Event> loaded = storage.loadAllEvents();
        assertEquals(1, loaded.size());
        Event retrieved = loaded.get(0);
        assertEquals("NRL-001", retrieved.getMatchId());
        assertEquals(25, retrieved.getMinute());
        assertEquals(Side.LEFT, retrieved.getSide());
        assertEquals(2, retrieved.getDefenders().size());
    }

    @Test
    void testStoreEventWithNullPrediction() {
        Event event = new Event("TRY", Side.MIDDLE, 50, "NRL-002");
        storage.storeMatchEvent(event, null);

        List<Event> loaded = storage.loadAllEvents();
        assertEquals(1, loaded.size());
    }

    @Test
    void testLoadEmptyDatabase() {
        List<Event> events = storage.loadAllEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    void testStoreMultipleEvents() {
        for (int i = 0; i < 5; i++) {
            Event event = new Event("TRY", Side.LEFT, i * 10, "NRL-00" + i);
            storage.storeMatchEvent(event, Side.RIGHT);
        }

        List<Event> loaded = storage.loadAllEvents();
        assertEquals(5, loaded.size());
    }

    @Test
    void testDefendersSerializedAndDeserialized() {
        Event event = new Event("TRY", Side.RIGHT, 30, "NRL-003");
        event.addDefender(new Defender(4, 2));
        event.addDefender(new Defender(5, 1));

        storage.storeMatchEvent(event, Side.LEFT);

        List<Event> loaded = storage.loadAllEvents();
        Event retrieved = loaded.get(0);

        assertEquals(2, retrieved.getDefenders().size());
        assertEquals(4, retrieved.getDefenders().get(0).getNumber());
        assertEquals(2, retrieved.getDefenders().get(0).getMissedTackles());
        assertEquals(5, retrieved.getDefenders().get(1).getNumber());
        assertEquals(1, retrieved.getDefenders().get(1).getMissedTackles());
    }
}
