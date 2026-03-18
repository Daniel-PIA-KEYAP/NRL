package com.nrl.analytics.engine;

import com.nrl.analytics.analytics.PlayerWeaknessCalculator;
import com.nrl.analytics.analytics.SideWeaknessDetector;
import com.nrl.analytics.api.ApiClient;
import com.nrl.analytics.api.ApiClient.ApiException;
import com.nrl.analytics.features.FeatureBuilder;
import com.nrl.analytics.ml.MatchPredictor;
import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.Side;
import com.nrl.analytics.parser.MatchDataParser;
import com.nrl.analytics.storage.DataStorage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Algorithm 6: Live Match Engine.
 *
 * <p>This is the core engine that orchestrates the full analytics pipeline:
 * <ol>
 *   <li>Fetch live match data from the API</li>
 *   <li>Parse the JSON response into events</li>
 *   <li>For each TRY event: build features, predict next try, store results</li>
 *   <li>Wait 10 seconds, then repeat until the match ends</li>
 * </ol>
 */
public class LiveMatchEngine {

    private static final int POLL_INTERVAL_SECONDS = 10;

    private final ApiClient apiClient;
    private final MatchDataParser parser;
    private final FeatureBuilder featureBuilder;
    private final MatchPredictor predictor;
    private final DataStorage dataStorage;

    private volatile boolean running = false;

    public LiveMatchEngine(ApiClient apiClient,
                           MatchDataParser parser,
                           FeatureBuilder featureBuilder,
                           MatchPredictor predictor,
                           DataStorage dataStorage) {
        this.apiClient = apiClient;
        this.parser = parser;
        this.featureBuilder = featureBuilder;
        this.predictor = predictor;
        this.dataStorage = dataStorage;
    }

    /**
     * Starts the live match loop.
     *
     * <p>Runs until {@link #stop()} is called or the thread is interrupted.
     *
     * @param apiUrl the live-data endpoint URL
     * @param apiKey the API authentication key
     */
    public void start(String apiUrl, String apiKey) {
        running = true;
        System.out.println("[LiveMatchEngine] Starting live match loop...");

        while (running) {
            try {
                // Step 2.1 – Fetch
                String json = apiClient.fetchLiveMatchData(apiUrl, apiKey);

                // Step 2.2 – Parse
                List<Event> events = parser.parseMatchData(json);

                // Step 2.3 – Process events
                for (Event event : events) {
                    if ("TRY".equalsIgnoreCase(event.getType())) {
                        processTryEvent(event);
                    }
                }

                // Step 2.4 – Wait
                TimeUnit.SECONDS.sleep(POLL_INTERVAL_SECONDS);

            } catch (ApiException e) {
                System.err.println("[LiveMatchEngine] API error: " + e.getMessage());
                // Brief pause before retrying
                sleepQuietly(POLL_INTERVAL_SECONDS);
            } catch (IOException e) {
                System.err.println("[LiveMatchEngine] Parse error: " + e.getMessage());
                sleepQuietly(POLL_INTERVAL_SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }

        System.out.println("[LiveMatchEngine] Live match loop stopped.");
    }

    /** Signals the engine to stop after the current iteration. */
    public void stop() {
        running = false;
    }

    /** Returns whether the engine is currently running. */
    public boolean isRunning() {
        return running;
    }

    private void processTryEvent(Event event) {
        FeatureVector features = featureBuilder.buildFeatures(event);

        Side prediction;
        try {
            prediction = predictor.predictNextTry(features);
        } catch (Exception e) {
            System.err.println("[LiveMatchEngine] Prediction error: " + e.getMessage());
            prediction = null;
        }

        System.out.printf("[LiveMatchEngine] TRY scored on  : %s%n", event.getSide());
        if (prediction != null) {
            System.out.printf("[LiveMatchEngine] Predicted next : %s%n", prediction);
        }

        dataStorage.storeMatchEvent(event, prediction);
    }

    private void sleepQuietly(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
