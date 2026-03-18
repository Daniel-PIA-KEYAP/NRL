package com.nrl.analytics.ml;

import com.nrl.analytics.model.FeatureVector;
import com.nrl.analytics.model.Side;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.Evaluation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Algorithm 4: Model Training.
 *
 * <p>Trains a Logistic Regression classifier using Weka on historical match data.
 * The class attribute is {@code next_try_side} (LEFT, RIGHT, or MIDDLE).
 */
public class ModelTrainer {

    static final String CLASS_ATTRIBUTE = "next_try_side";

    /**
     * Trains a Logistic Regression model on the provided {@link Instances} dataset.
     *
     * @param dataset the training data (must contain the class attribute)
     * @return the trained {@link Classifier}
     * @throws Exception if Weka training fails
     */
    public Classifier trainModel(Instances dataset) throws Exception {
        // Set class attribute to the last attribute if not already set
        if (dataset.classIndex() == -1) {
            dataset.setClassIndex(dataset.numAttributes() - 1);
        }

        Logistic logistic = new Logistic();
        logistic.buildClassifier(dataset);
        return logistic;
    }

    /**
     * Trains a model and validates it using 10-fold cross-validation.
     *
     * @param dataset the training data
     * @return the trained {@link Classifier}
     * @throws Exception if Weka training or evaluation fails
     */
    public Classifier trainAndValidateModel(Instances dataset) throws Exception {
        if (dataset.classIndex() == -1) {
            dataset.setClassIndex(dataset.numAttributes() - 1);
        }

        Logistic logistic = new Logistic();

        // Cross-validation
        Evaluation eval = new Evaluation(dataset);
        eval.crossValidateModel(logistic, dataset, 10, new Random(42));
        System.out.printf("[ModelTrainer] Cross-validation accuracy: %.2f%%%n",
                eval.pctCorrect());

        // Train on full dataset
        logistic.buildClassifier(dataset);
        return logistic;
    }

    /**
     * Creates an empty {@link Instances} schema matching the NRL feature vector.
     *
     * @return empty {@link Instances} with the correct attribute definitions
     */
    public static Instances createDatasetSchema() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("d1_number"));
        attributes.add(new Attribute("d2_number"));
        attributes.add(new Attribute("d1_missed_tackles"));
        attributes.add(new Attribute("d2_missed_tackles"));
        attributes.add(new Attribute("side"));
        attributes.add(new Attribute("d1_fatigue"));
        attributes.add(new Attribute("d2_fatigue"));

        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("LEFT");
        classValues.add("RIGHT");
        classValues.add("MIDDLE");
        attributes.add(new Attribute(CLASS_ATTRIBUTE, classValues));

        Instances schema = new Instances("NRL_Match_Data", attributes, 0);
        schema.setClassIndex(schema.numAttributes() - 1);
        return schema;
    }

    /**
     * Converts a {@link FeatureVector} and a known next-try side into a Weka
     * {@link Instance} that can be added to the training dataset.
     *
     * @param schema  the dataset schema (from {@link #createDatasetSchema()})
     * @param fv      the feature vector
     * @param nextSide the actual next try side (the label)
     * @return a fully labelled Weka Instance
     */
    public static Instance toInstance(Instances schema, FeatureVector fv, Side nextSide) {
        double[] values = new double[schema.numAttributes()];
        double[] fvValues = fv.toDoubleArray();
        System.arraycopy(fvValues, 0, values, 0, fvValues.length);
        values[schema.numAttributes() - 1] =
                schema.classAttribute().indexOfValue(nextSide.name());

        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(schema);
        return instance;
    }
}
