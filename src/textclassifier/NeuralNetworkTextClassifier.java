/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

/**
 *
 * @author ujjwalsinghania
 */
public class NeuralNetworkTextClassifier {
    
    public double[] runNeuralNetwork() {
        
        NeuralNetwork textClassificationNN = new MultiLayerPerceptron(0, 0);
        
        return null; 
    }
    
    public static void main(String[] args) {
        
    }
    
}
