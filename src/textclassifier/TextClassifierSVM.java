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
import java.util.HashMap;
import java.util.Objects;
import javax.print.attribute.standard.MediaSize;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author ujjwalsinghania
 */
public class TextClassifierSVM {

    /*
    createLIBSVMProblemFromDataFile() - Function that creates a svm_problem object from a data file in
    the libsvm data format. 
    returnType: Void. 
    parameters: Null.
     */
    protected static svm_problem createLIBSVMProblemFromDataFile(String filename) throws FileNotFoundException, IOException {
        svm_problem TrainingData = new svm_problem();

        ArrayList<svm_node[]> xValues = new ArrayList<>();
        ArrayList<Double> yValues = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("data/" + filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileLine;

            while ((fileLine = bufferedReader.readLine()) != null) {

                String[] dataValues = fileLine.split(" ");

                yValues.add(Double.parseDouble(dataValues[0]));
                svm_node[] rowValue = new svm_node[dataValues.length - 1];

                for (int i = 1; i < dataValues.length; i++) {
                    String dataPoint = dataValues[i];
                    String[] dataPointValues = dataPoint.split(":");
                    svm_node nodeValue = new svm_node();
                    nodeValue.index = Integer.parseInt(dataPointValues[0]);
                    nodeValue.value = Double.parseDouble(dataPointValues[1]);
                    rowValue[i - 1] = nodeValue;
                }
                xValues.add(rowValue);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File " + filename + " couldn't be found: createLIBSVMProblemFromDataFile()");
        }

        double[] yValuesArray = new double[yValues.size()];
        for (int i = 0; i < yValues.size(); i++) {
            yValuesArray[i] = yValues.get(i);
        }

        svm_node[][] xValuesArray = new svm_node[xValues.size()][];
        for (int i = 0; i < xValues.size(); i++) {
            xValuesArray[i] = xValues.get(i);
        }

        TrainingData.y = yValuesArray;
        TrainingData.x = xValuesArray;
        TrainingData.l = yValuesArray.length;

        return TrainingData;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        LIBSVMFormatDataCreator dataCreator = new LIBSVMFormatDataCreator();

//        try {
//            testData.createDataDumpFromTxtFolder("business");
//        } catch (IOException ex) {
//            System.out.println("Couldn't run the method: createDataDumpFromTxtFolder(): " + ex);
//        }
//        try {
//            dataCreator.createDataDumpFromExcelSheet("News-Categories.xlsx");
//        } catch (IOException | InvalidFormatException ex) {
//            System.out.println("Couldn't run the method: createDataDumpFromExcelSheet(): " + ex);
//        }
        try {

            svm_model SVMModel = TextClassifierSVM.trainSVMAndSaveModel("a1a.txt");

            HashMap<svm_node[], Double> testingDataFile = readTestingValuesFromDataFile("a1aT.txt");
            double correctPredictions = 0;
            ArrayList<Double> predictionList = new ArrayList<>();
            for (svm_node[] testingValue : testingDataFile.keySet()) {
                Double prediction = svm.svm_predict(SVMModel, testingValue);
                if (Objects.equals(prediction, testingDataFile.get(testingValue))) {
                    correctPredictions += 1;
                }
                predictionList.add(prediction);
            }

            Double accuracy = correctPredictions / (double) (predictionList.size()) * 100.0;
            System.out.println("--------------------------------" + '\n' + "Overall Accuracy = " + accuracy + "%");

        } catch (Exception ex) {
            System.out.println("Caught an exception: main(): " + ex);
        }
    }

    protected static HashMap<svm_node[], Double> readTestingValuesFromDataFile(String filename) throws IOException {
        HashMap<svm_node[], Double> testingData = new HashMap<>();
        try {
            FileReader fileReader = new FileReader("data/" + filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileLine;

            while ((fileLine = bufferedReader.readLine()) != null) {

                String[] dataValues = fileLine.split(" ");

                double yValue = Double.parseDouble(dataValues[0]);
                svm_node[] rowValue = new svm_node[dataValues.length - 1];
//
                for (int i = 1; i < dataValues.length; i++) {
                    String dataPoint = dataValues[i];
                    String[] dataPointValues = dataPoint.split(":");
                    svm_node nodeValue = new svm_node();
                    nodeValue.index = Integer.parseInt(dataPointValues[0]);
                    nodeValue.value = Double.parseDouble(dataPointValues[1]);
                    rowValue[i - 1] = nodeValue;
                }
                testingData.put(rowValue, yValue);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File " + filename + " couldn't be found: readTestingValuesFromDataFile()");
        }
        return testingData;
    }

    protected static void setTrainingParameters(svm_parameter TrainingParameters) {
        TrainingParameters.svm_type = svm_parameter.C_SVC;
        TrainingParameters.kernel_type = svm_parameter.LINEAR;
        TrainingParameters.degree = 1;
        TrainingParameters.gamma = 1;
        TrainingParameters.coef0 = 0;
        TrainingParameters.C = 1;
        TrainingParameters.nu = 0.5;
        TrainingParameters.p = 0.1;
        TrainingParameters.cache_size = 200;
        TrainingParameters.eps = 0.001;
        TrainingParameters.shrinking = 1;
        TrainingParameters.probability = 0;
        TrainingParameters.weight = new double[1];
    }

    protected static svm_model trainSVMAndGetModel(String filename) throws IOException {
        svm_problem TrainingData = createLIBSVMProblemFromDataFile(filename);
        svm_parameter TrainingParameters = new svm_parameter();
        setTrainingParameters(TrainingParameters);
        svm_model SVMModel = svm.svm_train(TrainingData, TrainingParameters);
        String fileNameString = "data/" + filename + ".model";
        svm.svm_save_model(fileNameString, SVMModel);
        return SVMModel;
    }

}
