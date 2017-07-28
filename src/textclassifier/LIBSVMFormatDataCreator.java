/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author ujjwalsinghania
 */
public class LIBSVMFormatDataCreator {

    private int numberOfClasses;
    private int numberOfWords;

    /**
     * createDataDumpFromExcelSheet() - Function that creates a word frequency
     * chart for all data in an Excel Sheet.
     *
     * @param filename
     * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
     * @throws java.io.IOException
     */
    public void createDataDumpFromExcelSheet(String filename) throws InvalidFormatException, IOException {
        TextUtilities textUtilities = new TextUtilities();
        createLIBSVMDataFile(textUtilities.createDataDumpFromExcelSheet(filename));
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
    }

    /**
     * createDataDumpFromTxtFolder() - Function that creates a word frequency
     * chart for all the .txt files in the /data folder.
     *
     * @param folders
     * @throws java.io.IOException
     */
    public void createDataDumpFromTxtFolder(String[] folders) throws IOException {
        TextUtilities textUtilities = new TextUtilities();
        createLIBSVMDataFile(textUtilities.createDataDumpFromTxtFolder(folders));
        numberOfClasses = textUtilities.getNumberOfClasses();
        numberOfWords = textUtilities.getNumberOfWords();
    }

    /**
     * createLIBSVMDataFile() - Function that creates a text file in the LIBSVM
     * format to train the SVM classifier. returnType: Void. parameters: Integer
     * classLabel - specifies the label for the dataPoint,
     * HashMap<Integer, Integer> - the wordFrequencyMap that is to be written to
     * the file.
     */
    private void createLIBSVMDataFile(HashMap<TreeMap<Integer, Integer>, Integer> libsvmData) throws IOException {
        String fileNameString = "data/libsvmData.txt";
        FileWriter fileWriter = new FileWriter(fileNameString);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (TreeMap<Integer, Integer> wordFrequencyMap : libsvmData.keySet()) {

            Integer classLabel = libsvmData.get(wordFrequencyMap);

            bufferedWriter.write(classLabel + " ");
            for (Integer key : wordFrequencyMap.keySet()) {
                bufferedWriter.write(key + ":" + wordFrequencyMap.get(key) + " ");
            }
            bufferedWriter.write('\n');
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    int getNumberOfClassTypes() {
        return numberOfClasses;
    }

    int getNumberOfWords() {
        return numberOfWords;
    }

}
