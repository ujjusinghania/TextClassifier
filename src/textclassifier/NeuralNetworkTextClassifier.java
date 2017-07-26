/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
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
        String[] folders = {"tech", "sport", "business"};
        dataArray = dataCreator.createDataDumpFromTxtFolder(folders); 
        
        System.out.println(dataArray.size());
        int trainingDataSize = (int) (dataArray.size() * 0.85);
        System.out.println(trainingDataSize);
        
        Set<Double[]> dataSet = dataArray.keySet();
        HashMap<Double[], Double[]> testingDataSet = new HashMap<>();
        
        int inputSize = 0; 
        int outputSize = 0;
        
        for (Double[] key: dataSet) {
                inputSize = key.length;
                outputSize = dataArray.get(key).length;
                break;
        }
        
        System.out.println(inputSize + " " + outputSize);
        
        
        NeuralNetwork textClassificationNN = new MultiLayerPerceptron(0, 0);

    }
    
    public static void main(String[] args) {
        try { 
            trainNeuralNetwork();
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkTextClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
