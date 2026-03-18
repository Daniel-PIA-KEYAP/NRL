package com.nrl.analytics.ml;

import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.Side;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Algorithm 5: Real-Time Prediction.
 *
 * <p>Uses a trained Weka classifier to predict the side of the next try
 * given a feature vector.
 */
public class MatchPredictor {

    private final Classifier model;
    private final Instances schema;

    public MatchPredictor(Classifier model) {
        this.model = model;
        this.schema = ModelTrainer.createDatasetSchema();
    }

    /**
     * Predicts the most likely side for the next try.
     *
     * @param featureVector the features extracted from the current event
     * @return the predicted {@link Side}
     * @throws Exception if Weka classification fails
     */
    public Side predictNextTry(FeatureVector featureVector) throws Exception {
        Instance instance = toWekaInstance(featureVector);
        double predictionIndex = model.classifyInstance(instance);
        String label = schema.classAttribute().value((int) predictionIndex);
        return Side.fromString(label);
    }

    /**
     * Returns the probability distribution over all three sides.
     *
     * @param featureVector the features extracted from the current event
     * @return probability array [LEFT, RIGHT, MIDDLE]
     * @throws Exception if Weka distribution calculation fails
     */
    public double[] getProbabilityDistribution(FeatureVector featureVector) throws Exception {
        Instance instance = toWekaInstance(featureVector);
        return model.distributionForInstance(instance);
    }

    private Instance toWekaInstance(FeatureVector fv) {
        double[] values = new double[schema.numAttributes()];
        double[] fvValues = fv.toDoubleArray();
        System.arraycopy(fvValues, 0, values, 0, fvValues.length);
        // Class attribute left as missing for prediction
        values[schema.numAttributes() - 1] = Utils.missingValue();

        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(schema);
        return instance;
    }
}
