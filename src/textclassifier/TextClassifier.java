/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
        
        DataCreator testData = new DataCreator();
        
//        try {
//            testData.createDataDumpFromTxtFolder("business");
//        } catch (IOException ex) {
//            System.out.println("Couldn't run the method: createDataDumpFromTxtFolder()");
//        }  

        try { 
            testData.createDataDumpFromExcelSheet(); 
        }
        catch (IOException | InvalidFormatException ex) { 
            System.out.println(ex); 
        }
        
        /*
        svm SupportVectorMachine = new svm(); 
        SupportVectorMachine.svm_train(s, s1)
        */

    }

}
