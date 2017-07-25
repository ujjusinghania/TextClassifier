/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
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

    /**
     * createLIBSVMProblemFromDataFile() - Function that creates a svm_problem
     * object from a data file in the libsvm data format. returnType: Void.
     * parameters: Null.
     */
    protected static svm_problem createLIBSVMProblemFromDataFile(String filename, int classType) throws FileNotFoundException, IOException {
        svm_problem TrainingData = new svm_problem();

        ArrayList<svm_node[]> xValues = new ArrayList<>();
        ArrayList<Double> yValues = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("data/" + filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileLine;

            while ((fileLine = bufferedReader.readLine()) != null) {

                String[] dataValues = fileLine.split(" ");

                double classTypeValueForNode = Double.parseDouble(dataValues[0]);

                if (classType != 0) {
                    if (classType == classTypeValueForNode) {
                        classTypeValueForNode = 1;
                    } else {
                        classTypeValueForNode = -1;
                    }
                }

                yValues.add(classTypeValueForNode);
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
    public static void main(String[] args) throws IOException, InvalidFormatException {
        // TODO code application logic here
        LIBSVMFormatDataCreator dataCreator = new LIBSVMFormatDataCreator();

//        try {
//            String[] folders = {"business", "politics", "entertainment", "sport", "tech"};
//            dataCreator.createDataDumpFromTxtFolder(folders);
//        } catch (IOException ex) {
//            System.out.println("Couldn't run the method: createDataDumpFromTxtFolder(): " + ex);
//        }

        try {
            dataCreator.createDataDumpFromExcelSheet("News-Categories.xlsx");
        } catch (IOException | InvalidFormatException ex) {
            System.out.println("Couldn't run the method: createDataDumpFromExcelSheet(): " + ex);
        }

//        try {


// ADD PROVISION FOR WHEN THERE ARE JUST 2 CLASSES.

        int numberOfClassTypes = dataCreator.getNumberOfClassTypes();
        svm_model[] SVMModels = new svm_model[numberOfClassTypes];

        for (int i = 0; i < numberOfClassTypes; i++) {
            SVMModels[i] = TextClassifierSVM.trainSVMAndSaveModel("libsvmDataTrain.txt", i + 1);
        }

//        svm_model hello = TextClassifierSVM.trainSVMAndSaveModel("libsvmDataTrain.txt", 0);
//        svm_model[] SVMModels = new svm_model[79];
//
//        for (int i = 0; i < SVMModels.length; i++) {
//            SVMModels[i] = svm.svm_load_model("data/" + (i + 1) + "libsvmDataTrain.txt.model");
//        }

        svm_problem testingDataFile = createLIBSVMProblemFromDataFile("libsvmDataTest.txt", 0);
        double correctPredictions = 0;
        ArrayList<Double> predictionList = new ArrayList<>();

        for (int testingValueIndex = 0; testingValueIndex < testingDataFile.x.length; testingValueIndex++) {
            svm_node[] testingValue = testingDataFile.x[testingValueIndex];
            ArrayList<Double> probabilityArrayList = new ArrayList<Double>();
            for (svm_model SVMModel : SVMModels) {

                double[] probabilityEstimates = new double[svm.svm_get_nr_class(SVMModel)];
                svm.svm_predict_probability(SVMModel, testingValue, probabilityEstimates);

                int[] labels = new int[probabilityEstimates.length];
                svm.svm_get_labels(SVMModel, labels);

                boolean valueAdded = false;

                for (int i = 0; i < probabilityEstimates.length; i++) {
                    System.out.print(labels[i] + ":" + probabilityEstimates[i] + " ");
                    if (labels[i] == 1) {
                        probabilityArrayList.add(probabilityEstimates[i]);
                        valueAdded = true;
                        break;
                    }
                }

                if (!valueAdded) {
                    probabilityArrayList.add(0.0);
                }

                System.out.println("");

            }
            System.out.println("--------");
            Double maxProbabilityValue = probabilityArrayList.get(0);
            double maxProbabilityValueIndex = 1;

            for (int i = 0; i < probabilityArrayList.size(); i++) {
                if (maxProbabilityValue < probabilityArrayList.get(i)) {
                    maxProbabilityValue = probabilityArrayList.get(i);
                    maxProbabilityValueIndex = i + 1;
                }
            }

            if (Objects.equals(maxProbabilityValueIndex, testingDataFile.y[testingValueIndex])) {
                correctPredictions += 1;
            }
            predictionList.add(maxProbabilityValueIndex);
        }

        Double accuracy = correctPredictions / (double) (predictionList.size()) * 100.0;
        System.out.println("--------------------------------" + '\n' + "Overall Accuracy = " + accuracy + "%");

//       } catch (Exception ex) {
//            System.out.println("Caught an exception: main(): " + ex);
//        }
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
        TrainingParameters.cache_size = 100;
        TrainingParameters.eps = 0.001;
        TrainingParameters.shrinking = 1;
        TrainingParameters.probability = 1;
    }

    protected static svm_model trainSVMAndSaveModel(String filename, int classType) throws IOException {
        svm_problem TrainingData = createLIBSVMProblemFromDataFile(filename, classType);
        svm_parameter TrainingParameters = new svm_parameter();
        setTrainingParameters(TrainingParameters);
        svm_model SVMModel = svm.svm_train(TrainingData, TrainingParameters);
        String fileNameString = "data/" + classType + filename + ".model";
        svm.svm_save_model(fileNameString, SVMModel);
        return SVMModel;
    }

}
