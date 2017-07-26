/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author ujjwalsinghania
 */
public class TextUtilities {
    
    // Universal Map to make sure that the same word in different titles have the same index in the resultant data set.
    private HashMap<String, Integer> wordIndexMap = new HashMap<String, Integer>();
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
        String[] words = fileLine.split(" ");
        TextPreprocessor textCleaner = new TextPreprocessor();

        for (String word : words) {
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

    int getNumberOfWords() {
        return wordIndexSize-1;
    }
}
