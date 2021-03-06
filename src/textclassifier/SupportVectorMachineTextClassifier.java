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
import java.io.InputStreamReader;
import java.util.ArrayList;
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
public class SupportVectorMachineTextClassifier {

    /**
     * Function that creates a svm_problem object from a data file in the libsvm data format.
     * @param filename Name of file whose data will be converted to a svm_problem.
     * @param preserveClass Class to be preserved for current one-vs-all svm_problem.
     * @return svm_problem.
     * @throws java.io.FileNotFoundException
     */
    protected static svm_problem createLIBSVMProblemFromDataFile(String filename, int preserveClass) throws FileNotFoundException, IOException {
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

                if (preserveClass != 0) {
                    if (preserveClass == classTypeValueForNode) {
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
     * @throws java.io.IOException
     * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
     */
    public static void main(String[] args) throws IOException, InvalidFormatException {
        LIBSVMFormatDataCreator dataCreator = new LIBSVMFormatDataCreator();

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(input);

        System.out.print("1. Load data from folders\n2. Load data from excel (.xlsx) file\n3. Continue \nEnter your option: ");
        int option = Integer.parseInt(bufferedReader.readLine());

        switch (option) {
            case 1:
                try {
                    String[] folders = {"business", "politics", "entertainment", "tech", "sport"};
                    dataCreator.createDataDumpFromTxtFolder(folders);
                } catch (IOException ex) {
                    System.out.println("Couldn't run the method: createDataDumpFromTxtFolder(): " + ex);
                }
                break;
            case 2:
                try {
                    dataCreator.createDataDumpFromExcelSheet("News-Categories.xlsx");
                } catch (IOException | InvalidFormatException ex) {
                    System.out.println("Couldn't run the method: createDataDumpFromExcelSheet(): " + ex);
                }
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid option selected. Program terminating.");
        }

        System.out.print("1. Train SVM Models\n2. Load SVM Models\nEnter your option: ");
        option = Integer.parseInt(bufferedReader.readLine());

        svm_model[] SVMModels = null;

        switch (option) {
            case 1:
                SVMModels = trainAndGetSVMModels(dataCreator.getNumberOfClassTypes());
                break;
            case 2:
                SVMModels = loadAndGetSVMModels();
                break;
            default:
                System.out.println("Invalid option selected. Program terminating.");
        }

        predictAndClassifyDocuments(SVMModels);
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

    /**
     * Function that trains the SVM and creates and saves the corresponding svm_model.
     * @param filename Name of file whose data will be converted to a svm_problem.
     * @param classType Class to be preserved for current one-vs-all svm_problem.
     * @return svm_model.
     * @throws java.io.IOException
     */
    protected static svm_model trainSVMAndSaveModels(String filename, int classType) throws IOException {
        svm_problem TrainingData = createLIBSVMProblemFromDataFile(filename, classType);
        svm_parameter TrainingParameters = new svm_parameter();
        setTrainingParameters(TrainingParameters);
        svm_model SVMModel = svm.svm_train(TrainingData, TrainingParameters);
        String fileNameString = "data/" + classType + filename + ".model";
        svm.svm_save_model(fileNameString, SVMModel);
        return SVMModel;
    }

    private static svm_model[] trainAndGetSVMModels(int numberOfClassTypes) throws IOException {
        svm_model[] SVMModels = new svm_model[numberOfClassTypes];

        for (int i = 0; i < numberOfClassTypes; i++) {
            SVMModels[i] = SupportVectorMachineTextClassifier.trainSVMAndSaveModels("libsvmDataTrain.txt", i + 1);
        }
        return SVMModels;
    }

    private static svm_model[] loadAndGetSVMModels() throws IOException {
        svm_model hello = SupportVectorMachineTextClassifier.trainSVMAndSaveModels("libsvmDataTrain.txt", 0);
        svm_model[] SVMModels = new svm_model[svm.svm_get_nr_class(hello)];

        for (int i = 0; i < SVMModels.length; i++) {
            SVMModels[i] = svm.svm_load_model("data/" + (i + 1) + "libsvmDataTrain.txt.model");
        }
        return SVMModels;
    }

    private static void predictAndClassifyDocuments(svm_model[] SVMModels) {

        try {
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
            System.out.println("--------------------------------" + '\n' + "Got "
                    + (int) correctPredictions + " out of " + predictionList.size()
                    + '\n' + "Overall Accuracy = " + accuracy + "%");
        } catch (IOException ex) {
            System.out.println("Caught an IOException in predictAndClassifyDocuments(): " + ex.toString());
        }
    }

}
