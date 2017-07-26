/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

/**
 *
 * @author ujjwalsinghania
 */
public class NeuralNetworkTextClassifier {

    protected static void trainNeuralNetwork() throws IOException {

        NeurophNeuralNetworkDataCreator dataCreator = new NeurophNeuralNetworkDataCreator();

        HashMap<Double[], Double[]> dataArray = new HashMap<>();
        String[] folders = {"tech", "sport", "business", "politics", "entertainment"};
        dataArray = dataCreator.createDataDumpFromTxtFolder(folders);
        
        dataCreator = null;

        System.out.println(dataArray.size());
        int trainingDataSize = (int) (dataArray.size() * 0.85);
        System.out.println(trainingDataSize);

        int inputSize = 0;
        int outputSize = 0;

        for (Double[] key : dataArray.keySet()) {
            inputSize = key.length;
            outputSize = dataArray.get(key).length;
            break;
        }

        HashMap<double[], double[]> testingDataSet = new HashMap<>();
        DataSet trainingDataSet = new DataSet(inputSize, outputSize);

        for (Double[] key : dataArray.keySet()) {
            if (trainingDataSet.size() < trainingDataSize) {
                trainingDataSet.add(new DataSetRow(ArrayUtils.toPrimitive(key), ArrayUtils.toPrimitive(dataArray.get(key))));
                continue;
            }
            testingDataSet.put(ArrayUtils.toPrimitive(key), ArrayUtils.toPrimitive(dataArray.get(key)));
        }

        NeuralNetwork textClassificationNN = new MultiLayerPerceptron(inputSize, outputSize);
        System.out.println("learning");
        textClassificationNN.learn(trainingDataSet);
        System.out.println("saving");
        textClassificationNN.save("/data/neuralNetwork.nnet");

        System.out.println("predicting");
        int correctPredictions = 0;
        for (double[] input : testingDataSet.keySet()) {
            textClassificationNN.setInput(input);
            textClassificationNN.calculate();
            double[] prediction = textClassificationNN.getOutput();
            if (sameArray(prediction, testingDataSet.get(input))) {
                correctPredictions += 1;
            }
        }
        
        System.out.println("Accuracy: " + ((double)(correctPredictions)/(double)testingDataSet.size()));

    }

    public static void main(String[] args) { 
        try {
            trainNeuralNetwork();
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkTextClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean sameArray(double[] prediction, double[] actualOutput) {
        for (int index = 0; index < actualOutput.length; index++) {
            if (prediction[index] != actualOutput[index]) {
                return false; 
            }
        }
        return true;
    }

}
