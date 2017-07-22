/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

/**
 *
 * @author ujjwalsinghania
 */
public class TextPreprocessor {
    
    // Use .matches()
    
    private String specialCharacterRemover(String word) {
        return word.replaceAll("[^a-zA-Z0-9%$]", "");
    }
    
    public String cleanString(String word) { 
        String cleanWord = specialCharacterRemover(word).toLowerCase();
        if (cleanWord.contains("$") == true) {
            return "currency";
        }
        if (cleanWord.contains("%") == true) {
            return "percentage";
        }
        return cleanWord;
}
    
    
    
}
