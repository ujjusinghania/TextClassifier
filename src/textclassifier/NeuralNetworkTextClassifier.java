/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author ujjwalsinghania
 */
public class NeuralNetworkTextClassifier {

    protected static void trainNeuralNetwork() throws IOException {

        NeurophNeuralNetworkDataCreator dataCreator = new NeurophNeuralNetworkDataCreator();
        String[] folders = {"tech", "sport", "business", "politics", "entertainment"};
        HashMap<Double[], Double[]> dataArray = dataCreator.createDataDumpFromTxtFolder(folders);

        System.out.println("Total number of data points: " + dataArray.size());
        int trainingDataSize = (int) (dataArray.size() * 0.85);
        System.out.println("Numbers of data points in training data: " + trainingDataSize);

        int inputSize = dataCreator.getNumberOfWords();
        int outputSize = dataCreator.getNumberOfClassTypes();
          
        dataCreator = null;

        HashMap<double[], double[]> testingDataSet = new HashMap<>();
        DataSet trainingDataSet = new DataSet(inputSize, outputSize);

        for (Double[] key : dataArray.keySet()) {
            if (trainingDataSet.size() < trainingDataSize) {
                trainingDataSet.add(new DataSetRow(ArrayUtils.toPrimitive(key), ArrayUtils.toPrimitive(dataArray.get(key))));
                continue;
            }
            testingDataSet.put(ArrayUtils.toPrimitive(key), ArrayUtils.toPrimitive(dataArray.get(key)));
        }

        NeuralNetwork textClassificationNN = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, outputSize);
        System.out.println("learning");
        textClassificationNN.learn(trainingDataSet);
        System.out.println("saving");
        textClassificationNN.save("data/neuralNetwork.nnet");

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
        return Objects.equals(prediction, actualOutput);
    }

}
