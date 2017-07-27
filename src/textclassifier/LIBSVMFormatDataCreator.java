/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author ujjwalsinghania
 */
public class LIBSVMFormatDataCreator {

    private Map<String, Integer> classificationMap;
    TextUtilities textUtilities = new TextUtilities();

    /**
     * createDataDumpFromExcelSheet() - Function that creates a word frequency
     * chart for all data in an Excel Sheet. returnType: Void. parameters:
     * String filename - the name of the .xlsx file that will be read and
     * converted to libsvm data format.
     */
    public void createDataDumpFromExcelSheet(String filename) throws InvalidFormatException, IOException {

        int classTypeIdentifier = 1;
        classificationMap = new HashMap<String, Integer>();

        HashMap<String, String> dataFile = textUtilities.createDataDumpFromExcelSheet(filename);
        HashMap<TreeMap<Integer, Integer>, Integer> libsvmData = new HashMap<TreeMap<Integer, Integer>, Integer>();

        for (String textFile : dataFile.keySet()) {
            String classLabel = dataFile.get(textFile);
            if (!classificationMap.containsKey(classLabel)) {
                classificationMap.put(classLabel, classTypeIdentifier);
                classTypeIdentifier += 1;
            }

            libsvmData.put(textUtilities.splitStringAndMakeWordFrequencyMap(textFile), classificationMap.get(classLabel));
        }
        createLIBSVMDataFile(libsvmData);
    }

    /**
     * createDataDumpFromTxtFolder() - Function that creates a word frequency
     * chart for all the .txt files in the /data folder. returnType: Void.
     * parameters: String folder - specifies folder for which datadump is
     * created.
     */
    public void createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {

        int classTypeIdentifier = 1;
        classificationMap = new HashMap<String, Integer>();

        HashMap<String, String> dataFile = textUtilities.createDataDumpFromTxtFolder(folders);
        HashMap<TreeMap<Integer, Integer>, Integer> libsvmData = new HashMap<TreeMap<Integer, Integer>, Integer>();

        for (String textFile : dataFile.keySet()) {
            String classLabel = dataFile.get(textFile);
            if (!classificationMap.containsKey(classLabel)) {
                classificationMap.put(classLabel, classTypeIdentifier);
                classTypeIdentifier += 1;
            }

            libsvmData.put(textUtilities.splitStringAndMakeWordFrequencyMap(textFile), classificationMap.get(classLabel));
        }
        createLIBSVMDataFile(libsvmData);
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
        return classificationMap.size();
    }

}
