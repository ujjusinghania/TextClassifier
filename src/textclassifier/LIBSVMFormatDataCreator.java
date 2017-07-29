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
     * createDataDumpFromExcelSheet() - Function that reads the given excel file and
     * creates a libsvm data file out of it.
     * @param filename - name of the file. 
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
     * createDataDumpFromTxtFolder() - Function that reads all the .txt files
     * in the /data folder and creates a libsvm data file out of it.
     * @param folders - Array containing the names of the folders.
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
     * format to train the SVM classifier. 
     * @param libsvmData - tokenized data that contains the wordFrequencyMap for each file/row 
     * and the corresponding class label. 
     * @throws java.io.IOException
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

    /**
     * getNumberOfClassTypes() - Function that returns the number of class labels in the 
     * data set.
     * @return Integer
     */
    public int getNumberOfClassTypes() {
        return numberOfClasses;
    }

    /**
     * getNumberOfWords() - Function that returns the number of unique words in the 
     * data set.
     * @return Integer
     */
    public int getNumberOfWords() {
        return numberOfWords;
    }

}
