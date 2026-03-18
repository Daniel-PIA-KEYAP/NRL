package com.nrl.analytics;

import com.nrl.analytics.analytics.SideWeaknessDetector;
import com.nrl.analytics.api.ApiClient;
import com.nrl.analytics.engine.LiveMatchEngine;
import com.nrl.analytics.features.FeatureBuilder;
import com.nrl.analytics.ml.MatchPredictor;
import com.nrl.analytics.ml.ModelRetrainer;
import com.nrl.analytics.ml.ModelTrainer;
import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.PlayerStats;
import com.nrl.analytics.model.Side;
import com.nrl.analytics.parser.MatchDataParser;
import com.nrl.analytics.storage.DataStorage;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Arrays;
import java.util.List;

/**
 * Entry point for the NRL Analytics system.
 *
 * <p>Demonstrates the full pipeline:
 * <ol>
 *   <li>Train an initial ML model on seed data</li>
 *   <li>Show a sample prediction</li>
 *   <li>Show side weakness detection</li>
 *   <li>Start the live match engine (configurable via env vars)</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("=== NRL Analytics System ===");
        System.out.println();

        // --- Setup shared components ---
        DataStorage dataStorage = new DataStorage("nrl_analytics.db");
        ModelTrainer trainer = new ModelTrainer();
        FeatureBuilder featureBuilder = new FeatureBuilder();
        MatchDataParser parser = new MatchDataParser();

        // --- Train initial model on seed data ---
        System.out.println("[Main] Training initial model...");
        Instances dataset = ModelTrainer.createDatasetSchema();
        addSeedData(dataset);

        Classifier model = trainer.trainAndValidateModel(dataset);
        MatchPredictor predictor = new MatchPredictor(model);
        System.out.println("[Main] Model training complete.");
        System.out.println();

        // --- Demo prediction ---
        FeatureVector sample = new FeatureVector(2, 3, 1, 0, Side.LEFT.getCode(), 0.5, 0.5);
        Side prediction = predictor.predictNextTry(sample);
        System.out.println("[Main] Sample prediction for features " + sample);
        System.out.println("[Main] => Predicted next try side: " + prediction);
        System.out.println();

        // --- Demo side weakness detection ---
        List<PlayerStats> players = Arrays.asList(
                new PlayerStats(2, 2, 3, 0.5),
                new PlayerStats(3, 1, 1, 0.4),
                new PlayerStats(4, 0, 0, 0.3),
                new PlayerStats(5, 0, 1, 0.3),
                new PlayerStats(9, 1, 2, 0.6)
        );
        SideWeaknessDetector detector = new SideWeaknessDetector();
        Side weakSide = detector.detectWeakSide(players);
        System.out.println("[Main] Weak side detected: " + weakSide);
        System.out.println();

        // --- Live match engine ---
        String apiUrl = System.getenv("NRL_API_URL");
        String apiKey = System.getenv("NRL_API_KEY");

        if (apiUrl != null && !apiUrl.isBlank()) {
            System.out.println("[Main] Starting live match engine...");
            ApiClient apiClient = new ApiClient();
            LiveMatchEngine engine = new LiveMatchEngine(
                    apiClient, parser, featureBuilder, predictor, dataStorage);

            // Shutdown hook for graceful stop
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[Main] Shutting down engine...");
                engine.stop();
            }));

            engine.start(apiUrl, apiKey != null ? apiKey : "");
        } else {
            System.out.println("[Main] NRL_API_URL not set – skipping live match engine.");
            System.out.println("[Main] Set NRL_API_URL (and optionally NRL_API_KEY) "
                    + "environment variables to enable live mode.");
        }

        // --- Model retraining demo ---
        ModelRetrainer retrainer = new ModelRetrainer(dataStorage);
        Classifier retrained = retrainer.retrainModel();
        if (retrained != null) {
            System.out.println("[Main] Model successfully retrained with new data.");
        }

        System.out.println();
        System.out.println("=== NRL Analytics System finished ===");
    }

    /** Adds seed training instances so the initial model has something to learn from. */
    private static void addSeedData(Instances dataset) {
        Object[][] seed = {
            // d1Num, d2Num, d1MT, d2MT, side, d1F, d2F, nextSide
            {2, 3, 1, 0, 0, 0.3, 0.3, "LEFT"},
            {4, 5, 0, 1, 1, 0.5, 0.5, "RIGHT"},
            {9, 8, 2, 2, 2, 0.7, 0.7, "MIDDLE"},
            {2, 3, 2, 1, 0, 0.6, 0.6, "LEFT"},
            {4, 5, 1, 2, 1, 0.4, 0.4, "RIGHT"},
            {9, 1, 0, 0, 2, 0.2, 0.2, "MIDDLE"},
            {3, 2, 3, 0, 0, 0.8, 0.8, "LEFT"},
            {5, 4, 0, 3, 1, 0.9, 0.9, "RIGHT"},
            {6, 7, 1, 1, 2, 0.5, 0.5, "MIDDLE"},
            {2, 3, 0, 2, 0, 0.1, 0.1, "LEFT"},
        };

        for (Object[] row : seed) {
            double[] vals = new double[dataset.numAttributes()];
            vals[0] = (int) row[0];
            vals[1] = (int) row[1];
            vals[2] = (int) row[2];
            vals[3] = (int) row[3];
            vals[4] = (int) row[4];
            vals[5] = (double) row[5];
            vals[6] = (double) row[6];
            vals[7] = dataset.classAttribute().indexOfValue((String) row[7]);
            weka.core.Instance inst = new weka.core.DenseInstance(1.0, vals);
            inst.setDataset(dataset);
            dataset.add(inst);
        }
    }
}
