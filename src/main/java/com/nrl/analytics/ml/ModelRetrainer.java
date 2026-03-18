package com.nrl.analytics.ml;

import com.nrl.analytics.model.Event;
import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.Side;
import com.nrl.analytics.storage.DataStorage;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;

/**
 * Algorithm 10: Model Improvement (Self-Learning).
 *
 * <p>Loads stored historical data, rebuilds the training dataset, trains a
 * new model, and replaces the old one.
 */
public class ModelRetrainer {

    private final DataStorage dataStorage;
    private final ModelTrainer modelTrainer;

    public ModelRetrainer(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.modelTrainer = new ModelTrainer();
    }

    /**
     * Retrains the model using all stored historical match events.
     *
     * @return the newly trained {@link Classifier}, or {@code null} if
     *         there is insufficient historical data (fewer than 10 events)
     * @throws Exception if training fails
     */
    public Classifier retrainModel() throws Exception {
        List<Event> historicalEvents = dataStorage.loadAllEvents();

        if (historicalEvents.size() < 10) {
            System.out.println("[ModelRetrainer] Insufficient data for retraining ("
                    + historicalEvents.size() + " events). Need at least 10.");
            return null;
        }

        Instances dataset = ModelTrainer.createDatasetSchema();
        com.nrl.analytics.features.FeatureBuilder featureBuilder =
                new com.nrl.analytics.features.FeatureBuilder();

        for (int i = 0; i < historicalEvents.size() - 1; i++) {
            Event current = historicalEvents.get(i);
            Event next = historicalEvents.get(i + 1);

            if (!"TRY".equalsIgnoreCase(current.getType())) {
                continue;
            }

            FeatureVector fv = featureBuilder.buildFeatures(current);
            Side nextSide = next.getSide();
            dataset.add(ModelTrainer.toInstance(dataset, fv, nextSide));
        }

        if (dataset.numInstances() == 0) {
            System.out.println("[ModelRetrainer] No usable training instances found.");
            return null;
        }

        System.out.println("[ModelRetrainer] Retraining on " + dataset.numInstances()
                + " instances...");
        Classifier newModel = modelTrainer.trainAndValidateModel(dataset);
        System.out.println("[ModelRetrainer] Retraining complete.");
        return newModel;
    }
}
