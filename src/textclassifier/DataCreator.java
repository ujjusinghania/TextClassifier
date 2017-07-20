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

    private Map<Integer, Integer> wordFrequencyMap;
    private Map<String, Integer> classificationMap;
    private HashMap<String, Integer> wordIndexMap = new HashMap<String, Integer>();
    private Integer wordIndexSize = 1; 
    private ArrayList<String> classTypes; // ArrayList to store the list of all the folders in /data.

    public void splitString(String fileLine) {
        
        wordFrequencyMap = new HashMap<Integer, Integer>(); 
        String[] words = fileLine.split(" ");

        for (String word : words) {
            if (wordFrequencyMap.containsKey(word) == false) {
                if (wordIndexMap.containsKey(word) == false) {
                    wordIndexMap.put(word, wordIndexSize);
                    wordIndexSize += 1; 
                }
                wordFrequencyMap.put(wordIndexMap.get(word), 1);
            } else {
                wordFrequencyMap.put(wordIndexMap.get(word), wordFrequencyMap.get(word) + 1);
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
                if (row.getRowNum() == 0) { continue; }
                String classType = row.getCell(4).toString();
                if (classType == null) { continue; }
                if (classificationMap.containsKey(classType) == false) {
                    classificationMap.put(classType, classTypeIndentifier);
                    classTypeIndentifier += 1;
                }
                excelSheetDatabase.put(row.getCell(1).toString(), classificationMap.get(classType));
            }
            
            for (String key : excelSheetDatabase.keySet()) {
                splitString(key);
                createLIBSVMDataFile(null, excelSheetDatabase.get(key));
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

                    createLIBSVMDataFile(folder, 0);

                } catch (FileNotFoundException ex) {
                    System.out.println("File " + fileName + " couldn't be found.");
                }
            }
        }
    }

    /*
    createLIBSVMDataFile() - Function that creates a text file in the LIBSVM format
    to train the SVM classifier. 
    returnType: Void. 
    parameters: String folder - specifices folder where datafile has to be created.
                Integer classLabel - specifies the label for the dataPoint. 
     */
    private void createLIBSVMDataFile(String folder, Integer classLabel) throws IOException {
        if (folder == null) {
            folder = "misc";
        } else if (classLabel == null) {
            classLabel = 0;
        }
        String fileNameString = "data/" + folder + "/libsvmData.txt";
        FileWriter fileWriter = new FileWriter(fileNameString, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(classLabel + " ");

        Integer value = 1;
        Set<Integer> keys = wordFrequencyMap.keySet();
        for (Integer key : keys) {
            bufferedWriter.write(value + ":" + wordFrequencyMap.get(key) + " ");
            value = value + 1;
        }
        bufferedWriter.write('\n');
        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
