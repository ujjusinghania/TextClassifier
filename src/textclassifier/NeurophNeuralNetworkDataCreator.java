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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neuroph.core.data.DataSet;

/**
 *
 * @author ujjwalsinghania
 */
public class NeurophNeuralNetworkDataCreator {

    TextUtilities textUtilities = new TextUtilities();

    private Map<String, Double[]> classificationMap;
    
/**
    public HashMap<Double[], Double[]> createDataDumpFromExcelSheet(String fileName) throws IOException, InvalidFormatException {
        try {

            XSSFWorkbook wb = new XSSFWorkbook(new File("data/" + fileName));
            XSSFSheet sheet = wb.getSheetAt(0);

            Map<String, Integer> excelSheetDatabase = new HashMap<String, Integer>();
            classificationMap = new HashMap<String, Double[]>();
            Integer classTypeIdentifier = 1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String classType = row.getCell(4).toString();
                if (classType == null) {
                    continue;
                }
                if (classificationMap.containsKey(classType) == false) {
                    classificationMap.put(classType, classTypeIdentifier);
                    classTypeIdentifier += 1;
                }
                excelSheetDatabase.put(row.getCell(1).toString(), classificationMap.get(classType));
            }

            for (String key : excelSheetDatabase.keySet()) {
                TreeMap<Integer, Integer> wordFrequencyMap = textUtilities.splitStringAndMakeWordFrequencyMap(key);
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("File " + fileName + " couldn't be found: createDataDumpFromTxtFolder(String folder)");
        }
        return null;
    }
    */
    
    private Double[] convertToBinaryArray(int classTypeIdentifier, int numberOfClasses) {
        String[] bits = Integer.toBinaryString(classTypeIdentifier).split("");
        Double[] bitPattern = new Double[numberOfClasses];
        java.util.Arrays.fill(bitPattern, 0.0);
        int differenceInLength = bitPattern.length - bits.length;
        for (int index = 0; index < bits.length ; index++) {
            bitPattern[index+differenceInLength] = Double.parseDouble(bits[index]);
        }
        return bitPattern;
    }
    
    private Double[] convertToDoubleArray(TreeMap<Integer, Integer> textFile, int numberOfWords) {
        Double[] wordArray = new Double[numberOfWords];
        java.util.Arrays.fill(wordArray, 0.0);
        for (int key: textFile.keySet()) {
            wordArray[key-1] = (double)textFile.get(key); 
        }
        return wordArray;
    }
    
    public HashMap<Double[], Double[]> createDataDumpFromTxtFolder(String[] folders) throws FileNotFoundException, IOException {

        HashMap<Double[], Double[]> dataFile = new HashMap<>();
        ArrayList<TreeMap<Integer, Integer>> wordFrequencyMap = new ArrayList<>();
        ArrayList<Double[]> classificationMapForDocument = new ArrayList<>(); 
        
        int classTypeIdentifier = 1;
        classificationMap = new HashMap<String, Double[]>();

        for (int i = 0; i < folders.length; i++) {

            String folder = folders[i];

            // Change path extension to location of working directory/folder containing all files.
            File[] files = new File("data/" + folder).listFiles();

            if (classificationMap.containsKey(folders[i]) == false) {
                classificationMap.put(folders[i], convertToBinaryArray(classTypeIdentifier, folders.length));
                classTypeIdentifier += 1;
            }

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

                    bufferedReader.close();

                    wordFrequencyMap.add(textUtilities.splitStringAndMakeWordFrequencyMap(textFile));
                    classificationMapForDocument.add(classificationMap.get(folder));

                } catch (FileNotFoundException ex) {
                    System.out.println("File " + fileName + " couldn't be found: createDataDumpFromTxtFolder(String folder)");
                }

            }
        }
        
        int totalNumberOfWords = textUtilities.getNumberOfWords();
        
        for (int index = 0; index < classificationMapForDocument.size(); index++) {
            dataFile.put(convertToDoubleArray(wordFrequencyMap.get(index), totalNumberOfWords), classificationMapForDocument.get(index));
        }     
        return dataFile; 
    }

}
