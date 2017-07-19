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

    public void readFromDataDump() throws FileNotFoundException, IOException {

        String fileName = "data/mock.txt";
        String fileLine;

        Map<String, Integer> wordFrequencyMap = new HashMap<String, Integer>();

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
            
        } catch (FileNotFoundException ex) {
            System.out.println("File " + fileName + " couldn't be found.");
        }
    }
}
