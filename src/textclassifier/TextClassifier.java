/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author ujjwalsinghania
 */
public class TextClassifier {

    public static svm_problem createLIBSVMProblemFromDataFile() throws FileNotFoundException, IOException {
        svm_problem TrainingData = new svm_problem();

        ArrayList<ArrayList<svm_node>> xValues = null;
        ArrayList<Double> yValues = null;
        try {
            FileReader fileReader = new FileReader("data/libsvmData.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileLine;

            while ((fileLine = bufferedReader.readLine()) != null) {
                System.out.println(fileLine); // for testing only
                String[] dataValues = fileLine.split(" ");

                yValues.add(Double.parseDouble(dataValues[0]));
//
//                for (int i = 1; i < dataValues.length; i++) {
//                    String dataPoint = dataValues[i];
//                    String[] dataPointValues = dataPoint.split(":");
//                    svm_node nodeValue = new svm_node();
//                    nodeValue.index = Integer.parseInt(dataPointValues[0]);
//                    nodeValue.value = Double.parseDouble(dataPointValues[1]);
//
//                    xValues.get(yValues.size() - 1).add(nodeValue);
//                }

            }
        } catch (FileNotFoundException ex) {
            System.out.println("File libsvmData.txt couldn't be found: createLIBSVMProblemFromDataFile()");
        }
        
        // Add values to TrainingData in array format. 
        
        return TrainingData;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        DataCreator dataCreator = new DataCreator();

//        try {
//            testData.createDataDumpFromTxtFolder("business");
//        } catch (IOException ex) {
//            System.out.println("Couldn't run the method: createDataDumpFromTxtFolder()");
//        }  
//        try {
//            dataCreator.createDataDumpFromExcelSheet();
//        } catch (IOException | InvalidFormatException ex) {
//            System.out.println(ex);
//        }
        try {
            svm SupportVectorMachine = new svm();
            svm_problem TrainingData = createLIBSVMProblemFromDataFile();
            svm_parameter TrainingParameters = new svm_parameter();
            svm_model SVMModel = SupportVectorMachine.svm_train(TrainingData, TrainingParameters);

            String fileNameString = "data/libsvmData.txt.model";
            FileWriter fileWriter = new FileWriter(fileNameString, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(SVMModel.toString());
        } catch (Exception ex) {
            System.out.println("Caught an exception: main(): " + ex);
        }
    }

}
