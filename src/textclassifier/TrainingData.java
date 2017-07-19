/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ujjwalsinghania
 */
public class TrainingData {

    private Map<String, Integer> wordFrequencyMap;
    
    /*
    
    readFromDataDump() - Function that creates a word frequency chart for all the 
    files in the /data folder. 
    returnType: Void. 
    parameters: Null.
    
    */
    
    public void readFromDataDump() throws FileNotFoundException, IOException {

        String fileName = "data/mock.txt";
        String fileLine;

        wordFrequencyMap = new HashMap<String, Integer>();

        try {

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((fileLine = bufferedReader.readLine()) != null) {
                System.out.println(fileLine);

                String[] words = fileLine.split(" ");

                for (String word : words) {
                    if (wordFrequencyMap.containsKey(word) != true) {
                        wordFrequencyMap.put(word, 1);
                    } else {
                        wordFrequencyMap.put(word, wordFrequencyMap.get(word) + 1);
                    }
                }
            }
            Set<String> keys = wordFrequencyMap.keySet();
            keys.forEach((key) -> {
                System.out.println(key + ": " + wordFrequencyMap.get(key));
            });
            
            bufferedReader.close();
            
            createLIBSVMDataFile();
            
        } catch (FileNotFoundException ex) {
            System.out.println("File " + fileName + " couldn't be found.");
        }
    }
    
    /*
    
    createLIBSVMDataFile() - Function that creates a text file in the LIBSVM format
    to train the SVM classifier. 
    returnType: Void. 
    parameters: Null.
    
    */
    
    private void createLIBSVMDataFile() throws IOException {
        String fileNameString = "data/writeData.txt";
        FileWriter fileWriter = new FileWriter(fileNameString);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        Integer value = 1; 
        Set<String> keys = wordFrequencyMap.keySet();
            for (String key: keys) {
                bufferedWriter.write(value + ":" + wordFrequencyMap.get(key) + " ");
                value = value + 1;
            }   
            
            bufferedWriter.close();
    }
    
}
