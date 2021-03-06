/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author ujjwalsinghania
 */
public class NeurophNeuralNetworkDataCreator {

    private int numberOfClasses;
    private int numberOfWords;

    /**
     * Function that reads the given excel file and creates a neuroph compliant HashMap out of it.
     * @param fileName Name of the file.
     * @return HashMap Each key:value pair is inputNode(s)Value:outputNode(s)Value
     * @throws IOException
     * @throws InvalidFormatException
     */
    public HashMap<Double[], Double[]> createDataDumpFromExcelSheet(String fileName) throws IOException, InvalidFormatException {
        TextUtilities textUtilities = new TextUtilities();
        HashMap<TreeMap<Integer, Integer>, Integer> dataFile = textUtilities.createDataDumpFromExcelSheet(fileName);
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
        HashMap<Double[], Double[]> neuralNetworkData = new HashMap<>();
        dataFile.keySet().forEach((TreeMap<Integer, Integer> wordFrequencyMap) -> {
            neuralNetworkData.put(convertToDoubleArray(wordFrequencyMap, numberOfWords), convertToBinaryArray(dataFile.get(wordFrequencyMap), numberOfClasses));
        });
        return neuralNetworkData;
    }

    /**
     * Function that reads all the .txt files in the /data folder and creates a neuroph compliant HashMap out of it.
     * @param folders Array containing the names of the folders.
     * @return HashMap Each key:value pair is inputNode(s)Value:outputNode(s)Value
     * @throws FileNotFoundException
     * @throws IOException
     */
    public HashMap<Double[], Double[]> createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {
        TextUtilities textUtilities = new TextUtilities();
        HashMap<TreeMap<Integer, Integer>, Integer> dataFile = textUtilities.createDataDumpFromTxtFolder(folders);
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
        HashMap<Double[], Double[]> neuralNetworkData = new HashMap<>();
        dataFile.keySet().forEach((TreeMap<Integer, Integer> wordFrequencyMap) -> {
            neuralNetworkData.put(convertToDoubleArray(wordFrequencyMap, numberOfWords), convertToBinaryArray(dataFile.get(wordFrequencyMap), numberOfClasses));
        });
        return neuralNetworkData;
    }
    
    /**
     * Function that converts the integer class label into a binary array.
     * @param classTypeIdentifier The class label.
     * @param numberOfClasses Total number of class labels in the data set (to ensure that each resultant array is of the same size). 
     * @return Double[]
     */
    private Double[] convertToBinaryArray(int classTypeIdentifier, int numberOfClasses) {
        Double[] bitPattern = new Double[numberOfClasses];
        java.util.Arrays.fill(bitPattern, 0.0);
        bitPattern[classTypeIdentifier-1] = 1.0;
        return bitPattern;
    }

    /**
     * Function that converts the wordFrequencyMap into a array with values at the corresponding word indices. 
     * @param textFile The wordFrequencyMap for the textFile/excelRow.
     * @param numberOfWords The total number of words in the data set (to ensure that each resultant array is of the same size). 
     * @return Double[]
     */
    private Double[] convertToDoubleArray(TreeMap<Integer, Integer> textFile, int numberOfWords) {
        Double[] wordArray = new Double[numberOfWords];
        java.util.Arrays.fill(wordArray, 0.0);
        textFile.keySet().forEach((key) -> {
            wordArray[key - 1] = (double) textFile.get(key);
        });
        return wordArray;
    }
    
    /**
     * Function that returns the number of class labels in the data set.
     * @return Integer
     */
    int getNumberOfClassTypes() {
        return numberOfClasses;
    }

    /**
     * Function that returns the number of unique words in the data set.
     * @return Integer
     */
    int getNumberOfWords() {
        return numberOfWords;
    }
}
