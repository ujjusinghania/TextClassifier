/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ujjwalsinghania
 */
public class DataCreator {

    // A word frequency map for the last document or row of excel sheet that was read. 
    private Map<Integer, Integer> wordFrequencyMap;
    private Map<String, Integer> classificationMap;
    // Universal Map to make sure that the same word in different titles have the same index in the resultant data set.
    private HashMap<String, Integer> wordIndexMap = new HashMap<String, Integer>();
    // Size of the wordIndexMap to keep track of the index to be assigned to the next new word. 
    private Integer wordIndexSize = 1;

    private void splitString(String fileLine) {

        wordFrequencyMap = new TreeMap<Integer, Integer>();
        String[] words = fileLine.split(" ");
        TextPreprocessor textCleaner = new TextPreprocessor();

        for (String word : words) {
            String cleanWord = textCleaner.cleanString(word);
            if (wordIndexMap.containsKey(cleanWord) == false) {
                wordIndexMap.put(cleanWord, wordIndexSize);
                wordIndexSize += 1;
            }
            Integer wordIndex = wordIndexMap.get(cleanWord);
            if (wordFrequencyMap.containsKey(wordIndex) == false) {
                wordFrequencyMap.put(wordIndex, 1);
            } else {
                wordFrequencyMap.put(wordIndex, wordFrequencyMap.get(wordIndex) + 1);
            }
        }
    }

    /*
    createDataDumpFromExcelSheet() - Function that creates a word frequency chart for all data in an
    Excel Sheet. 
    returnType: Void. 
    parameters: Null.
     */
    public void createDataDumpFromExcelSheet() throws InvalidFormatException, IOException {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(new File("data/News-Categories.xlsx"));
            XSSFSheet sheet = wb.getSheetAt(0);

            Map<String, Integer> excelSheetDatabase = new HashMap<String, Integer>();
            classificationMap = new HashMap<String, Integer>();
            Integer classTypeIndentifier = 1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String classType = row.getCell(4).toString();
                if (classType == null) {
                    continue;
                }
                if (classificationMap.containsKey(classType) == false) {
                    classificationMap.put(classType, classTypeIndentifier);
                    classTypeIndentifier += 1;
                }
                excelSheetDatabase.put(row.getCell(1).toString(), classificationMap.get(classType));
            }

            for (String key : excelSheetDatabase.keySet()) {
                splitString(key);
                createLIBSVMDataFile(excelSheetDatabase.get(key));
            }

        } catch (InvalidFormatException ex) {
            System.out.println("Caught an IOException: createDataDumpFromExcelSheet()" + '\n' + "Exception: " + ex);
        }

    }

    /*
    createDataDumpFromTxtFolder() - Function that creates a word frequency chart for all the 
    .txt files in the /data folder. 
    returnType: Void. 
    parameters: String folder - specifices folder for which datadump is created.
     */
    public void createDataDumpFromTxtFolder(String folder) throws FileNotFoundException, IOException {

        File[] files = new File("data/" + folder).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getAbsolutePath();
                
                String fileLine;

                try {

                    FileReader fileReader = new FileReader(fileName);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while ((fileLine = bufferedReader.readLine()) != null) {
                        System.out.println(fileLine);
                        splitString(fileLine);
                    }

                    Set<Integer> keys = wordFrequencyMap.keySet();
                    keys.forEach((key) -> {
                        System.out.println(key + ": " + wordFrequencyMap.get(key));
                    });

                    bufferedReader.close();

                    createLIBSVMDataFile(0);

                } catch (FileNotFoundException ex) {
                    System.out.println("File " + fileName + " couldn't be found: createDataDumpFromTxtFolder(String folder)");
                }
            }
        }
    }

    /*
    createLIBSVMDataFile() - Function that creates a text file in the LIBSVM format
    to train the SVM classifier. 
    returnType: Void. 
    parameters: Integer classLabel - specifies the label for the dataPoint.    
     */
    private void createLIBSVMDataFile(Integer classLabel) throws IOException {
        if (classLabel == null) {
            classLabel = 0;
        }
        String fileNameString = "data/libsvmData.txt";
        FileWriter fileWriter = new FileWriter(fileNameString, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(classLabel + " ");

        Set<Integer> keys = wordFrequencyMap.keySet();
        for (Integer key : keys) {
            bufferedWriter.write(key + ":" + wordFrequencyMap.get(key) + " ");
        }
        bufferedWriter.write('\n');
        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
