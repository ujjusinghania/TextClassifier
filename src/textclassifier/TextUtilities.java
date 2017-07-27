/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ujjwalsinghania
 */
public class TextUtilities {

    // Universal Map to make sure that the same word in different titles have the same index in the resultant data set.
    private HashMap<String, Integer> wordIndexMap = new HashMap<>();
    // Size of the wordIndexMap to keep track of the index to be assigned to the next new word. 
    private Integer wordIndexSize = 1;

    /**
     * splitStringAndMakeWordFrequencyMap() - Function that creates a word
     * frequency map for the provided string and stores it in wordFrequencyMap.
     * returnType: HashMap<Integer, Integer> - the wordFrequencyMap. parameters:
     * String fileLine - String that will be converted into a wordFrequencyMap.
     */
    public TreeMap<Integer, Integer> splitStringAndMakeWordFrequencyMap(String fileLine) {

        TreeMap<Integer, Integer> wordFrequencyMap = new TreeMap<Integer, Integer>();
        TextPreprocessor textCleaner = new TextPreprocessor();

        for (String word : fileLine.split(" ")) {
            String cleanWord = textCleaner.cleanString(word);

            if (cleanWord != null) {

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
        return wordFrequencyMap;
    }

    /**
     * createDataDumpFromExcelSheet() - Function that creates a word frequency
     * chart for all data in an Excel Sheet. returnType: Void. parameters:
     * String filename - the name of the .xlsx file that will be read and
     * converted to libsvm data format.
     */
    public HashMap<String, String> createDataDumpFromExcelSheet(String filename) throws InvalidFormatException, IOException {

        HashMap<String, String> excelSheetDatabase = new HashMap<String, String>();

        try {

            XSSFWorkbook wb = new XSSFWorkbook(new File("data/" + filename));
            XSSFSheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String classType = row.getCell(4).toString();
                excelSheetDatabase.put(row.getCell(1).toString(), classType);
            }

        } catch (InvalidFormatException ex) {
            System.out.println("Caught an IOException: createDataDumpFromExcelSheet()" + '\n' + "Exception: " + ex);
        }
        return excelSheetDatabase;
    }

    /**
     * createDataDumpFromTxtFolder() - Function that creates a word frequency
     * chart for all the .txt files in the /data folder. returnType: Void.
     * parameters: String folder - specifies folder for which datadump is
     * created.
     */
    public HashMap<String, String> createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {

        HashMap<String, String> foldersDatabase = new HashMap<String, String>();

        for (int i = 0; i < folders.length; i++) {

            String folder = folders[i];

            // Change path extension to location of working directory/folder containing all files.
            File[] files = new File("data/" + folder).listFiles();

            for (File file : files) {

                if (!file.isFile()) {
                    continue;
                }

                String fileName = file.getAbsolutePath();
                String fileLine;
                String textFile = "";

                try {

                    FileReader fileReader = new FileReader(fileName);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while ((fileLine = bufferedReader.readLine()) != null) {
                        textFile += fileLine;
                    }

                    foldersDatabase.put(textFile, folder);

                } catch (FileNotFoundException ex) {
                    System.out.println("File " + fileName + " couldn't be found: createDataDumpFromTxtFolder(String folder)");
                }

            }
        }

        return foldersDatabase;
    }

    int getNumberOfWords() {
        return wordIndexSize - 1;
    }
}
