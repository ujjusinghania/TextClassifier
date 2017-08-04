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

    private int classTypeIdentifier = 1;
    private Map<String, Integer> classificationMap;
    // Universal Map to make sure that the same word in different titles have the same index in the resultant data set.
    private HashMap<String, Integer> wordIndexMap = new HashMap<>();
    // Size of the wordIndexMap to keep track of the index to be assigned to the next new word. 
    private Integer wordIndexSize = 1;

    /**
     * Function that creates a word frequency map for the provided string and stores it in wordFrequencyMap.
     * @param fileLine String that will be converted into a wordFrequencyMap.
     * @return HashMap The wordFrequencyMap.
     */
    public TreeMap<Integer, Integer> splitStringAndMakeWordFrequencyMap(String fileLine) {

        TreeMap<Integer, Integer> wordFrequencyMap = new TreeMap<>();
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
     * Function that reads the given excel file and creates a HashMap out of it.
     * @param filename Name of the file.
     * @return HashMap Each key:value pair is wordFrequencyMap for excelRow: classLabel.
     * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
     * @throws java.io.IOException
     */
    public HashMap<TreeMap<Integer, Integer>, Integer> createDataDumpFromExcelSheet(String filename) throws InvalidFormatException, IOException {

        HashMap<String, String> excelSheetDatabase = new HashMap<>();

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
            System.out.println("Caught an InvalidFormatException: createDataDumpFromExcelSheet()" + '\n' + "Exception: " + ex);
        }
        return classifyAndCreateDataFile(excelSheetDatabase);
    }

    /**
     * Function that reads all the .txt files in the /data folder and creates a HashMap out of it.
     * @param folders Array containing the names of the folders.
     * @return HashMap Each key:value pair is wordFrequencyMap for file: classLabel.
     * @throws java.io.FileNotFoundException
     */
    public HashMap<TreeMap<Integer, Integer>, Integer> createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {

        HashMap<String, String> foldersDatabase = new HashMap<>();

        for (String folder : folders) {
            // Change path extension to location of working directory/folder containing all files.
            File[] files = new File("data/" + folder).listFiles();

            for (File file : files) {

                if (!file.isFile()) {
                    continue;
                }

                String fileLine;
                String textFile = "";

                try {

                    FileReader fileReader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while ((fileLine = bufferedReader.readLine()) != null) {
                        textFile += fileLine;
                    }

                    foldersDatabase.put(textFile, folder);

                } catch (FileNotFoundException ex) {
                    System.out.println("File " + file.getAbsolutePath() + " couldn't be found: createDataDumpFromTxtFolder(String folder)");
                }

            }
        }
        return classifyAndCreateDataFile(foldersDatabase);
    }

    /**
     * Function that processes the given dataFile HashMap, quantifies the text and classLabels into numbers, and creates a HashMap out of it.
     * @param dataFile Each key:value pair is text: classLabel.
     * @return Each key:value pair is wordFrequencyMap for file: classLabel.
     * @throws IOException
     */
    private HashMap<TreeMap<Integer, Integer>, Integer> classifyAndCreateDataFile(HashMap<String, String> dataFile) throws IOException {

        classificationMap = new HashMap<>();

        HashMap<TreeMap<Integer, Integer>, Integer> finalData = new HashMap<>();

        dataFile.keySet().forEach((textFile) -> {
            String classLabel = dataFile.get(textFile);
            if (!classificationMap.containsKey(classLabel)) {
                classificationMap.put(classLabel, classTypeIdentifier);
                classTypeIdentifier += 1;
            }

            finalData.put(splitStringAndMakeWordFrequencyMap(textFile), classificationMap.get(classLabel));
        });
        return finalData;
    }
    
    /**
     * Function that returns the number of unique words in the data set.
     * @return Integer
     */
    public int getNumberOfWords() {
        return wordIndexSize - 1;
    }

    /**
     * Function that returns the number of class labels in the data set.
     * @return Integer
     */
    public int getNumberOfClasses() {
        return classTypeIdentifier - 1;
    }

    
}
