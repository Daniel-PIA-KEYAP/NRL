package com.nrl.analytics.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrl.analytics.model.Defender;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.Side;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm 9: Data Storage.
 *
 * <p>Persists match events and predictions to an SQLite database and
 * retrieves them for model retraining.
 *
 * <p>When created via {@link #inMemory()}, a single shared {@link Connection}
 * is retained for the lifetime of the instance so that all operations see the
 * same in-memory database.  File-backed instances open a new connection per
 * operation, which is safe for concurrent access.
 */
public class DataStorage {

    private final String dbUrl;
    private final ObjectMapper objectMapper;
    /** Non-null only for in-memory instances. */
    private final Connection sharedConnection;

    /** Creates a DataStorage backed by the given SQLite database file. */
    public DataStorage(String dbFilePath) {
        this.dbUrl = "jdbc:sqlite:" + dbFilePath;
        this.objectMapper = new ObjectMapper();
        this.sharedConnection = null;
        initDatabase();
    }

    /** Creates an in-memory SQLite DataStorage (useful for testing). */
    public static DataStorage inMemory() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            return new DataStorage(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create in-memory database", e);
        }
    }

    /** Constructor for shared-connection (in-memory) mode. */
    private DataStorage(Connection sharedConnection) {
        this.dbUrl = null;
        this.objectMapper = new ObjectMapper();
        this.sharedConnection = sharedConnection;
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        if (sharedConnection != null) {
            return sharedConnection;
        }
        return DriverManager.getConnection(dbUrl);
    }

    private void closeConnection(Connection conn) throws SQLException {
        if (sharedConnection == null) {
            conn.close();
        }
        // shared connection is kept open
    }

    private void initDatabase() {
        String createTable = """
                CREATE TABLE IF NOT EXISTS match_events (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    match_id   TEXT    NOT NULL,
                    minute     INTEGER NOT NULL,
                    side       TEXT    NOT NULL,
                    defenders  TEXT,
                    prediction TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            } finally {
                closeConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("[DataStorage] Failed to initialize database: " + e.getMessage());
        }
    }

    /**
     * Stores a match event and its associated prediction.
     *
     * @param event      the match event to store
     * @param prediction the predicted next try side (may be {@code null})
     */
    public void storeMatchEvent(Event event, Side prediction) {
        String sql = """
                INSERT INTO match_events (match_id, minute, side, defenders, prediction)
                VALUES (?, ?, ?, ?, ?)
                """;

        String defendersJson = serializeDefenders(event.getDefenders());
        String predictionStr = prediction != null ? prediction.name() : null;

        try {
            Connection conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, event.getMatchId());
                pstmt.setInt(2, event.getMinute());
                pstmt.setString(3, event.getSide().name());
                pstmt.setString(4, defendersJson);
                pstmt.setString(5, predictionStr);
                pstmt.executeUpdate();
            } finally {
                closeConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("[DataStorage] Failed to store event: " + e.getMessage());
        }
    }

    /**
     * Loads all stored match events from the database.
     *
     * @return list of all stored events
     */
    public List<Event> loadAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT match_id, minute, side, defenders FROM match_events ORDER BY id";

        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String matchId = rs.getString("match_id");
                    int minute = rs.getInt("minute");
                    String sideStr = rs.getString("side");
                    String defendersJson = rs.getString("defenders");

                    Side side;
                    try {
                        side = Side.fromString(sideStr);
                    } catch (IllegalArgumentException ex) {
                        side = Side.MIDDLE;
                    }

                    Event event = new Event("TRY", side, minute, matchId);
                    event.setDefenders(deserializeDefenders(defendersJson));
                    events.add(event);
                }
            } finally {
                closeConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("[DataStorage] Failed to load events: " + e.getMessage());
        }

        return events;
    }

    private String serializeDefenders(List<Defender> defenders) {
        try {
            return objectMapper.writeValueAsString(defenders);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<Defender> deserializeDefenders(String json) {
        List<Defender> defenders = new ArrayList<>();
        if (json == null || json.isBlank()) {
            return defenders;
        }
        try {
            JsonNode array = objectMapper.readTree(json);
            if (array.isArray()) {
                for (JsonNode node : array) {
                    int number = node.path("number").asInt(0);
                    int missedTackles = node.path("missedTackles").asInt(0);
                    defenders.add(new Defender(number, missedTackles));
                }
            }
        } catch (Exception e) {
            System.err.println("[DataStorage] Failed to deserialize defenders: " + e.getMessage());
        }
        return defenders;
    }
}
