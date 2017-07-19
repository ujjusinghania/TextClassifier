/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import static java.time.Clock.system;

/**
 *
 * @author ujjwalsinghania
 */
public class TextClassifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        TrainingData testData = new TrainingData();
        try {
            testData.readFromDataDump();
        } catch (Exception ex) {
            System.out.println("Couldn't run the method: readFromDataDump()");
        }   
    }

}
