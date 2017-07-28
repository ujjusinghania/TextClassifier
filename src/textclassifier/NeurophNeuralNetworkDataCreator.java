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

    public HashMap<Double[], Double[]> createDataDumpFromExcelSheet(String fileName) throws IOException, InvalidFormatException {
        TextUtilities textUtilities = new TextUtilities();
        HashMap<TreeMap<Integer, Integer>, Integer> dataFile = textUtilities.createDataDumpFromExcelSheet(fileName);
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
        HashMap<Double[], Double[]> neuralNetworkData = new HashMap<>();
        for (TreeMap<Integer, Integer> wordFrequencyMap : dataFile.keySet()) {
            neuralNetworkData.put(convertToDoubleArray(wordFrequencyMap, numberOfWords), convertToBinaryArray(dataFile.get(wordFrequencyMap), numberOfClasses));
        }
        return neuralNetworkData;
    }

    private Double[] convertToBinaryArray(int classTypeIdentifier, int numberOfClasses) {
        String[] bits = Integer.toBinaryString(classTypeIdentifier).split("");
        Double[] bitPattern = new Double[numberOfClasses];
        java.util.Arrays.fill(bitPattern, 0.0);
        int differenceInLength = bitPattern.length - bits.length;
        for (int index = 0; index < bits.length; index++) {
            bitPattern[index + differenceInLength] = Double.parseDouble(bits[index]);
        }
        return bitPattern;
    }

    private Double[] convertToDoubleArray(TreeMap<Integer, Integer> textFile, int numberOfWords) {
        Double[] wordArray = new Double[numberOfWords];
        java.util.Arrays.fill(wordArray, 0.0);
        for (int key : textFile.keySet()) {
            wordArray[key - 1] = (double) textFile.get(key);
        }
        return wordArray;
    }

    public HashMap<Double[], Double[]> createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {
        TextUtilities textUtilities = new TextUtilities();
        HashMap<TreeMap<Integer, Integer>, Integer> dataFile = textUtilities.createDataDumpFromTxtFolder(folders);
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
        HashMap<Double[], Double[]> neuralNetworkData = new HashMap<>();
        for (TreeMap<Integer, Integer> wordFrequencyMap : dataFile.keySet()) {
            neuralNetworkData.put(convertToDoubleArray(wordFrequencyMap, numberOfWords), convertToBinaryArray(dataFile.get(wordFrequencyMap), numberOfClasses));
        }
        return neuralNetworkData;
    }

    int getNumberOfClassTypes() {
        return numberOfClasses;
    }

    int getNumberOfWords() {
        return numberOfWords;
    }
}
