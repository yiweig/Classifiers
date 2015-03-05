import java.util.*;

/**
 * THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
 * A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Yiwei Gao
 */
public class Bayes {

    private List<char[]> trainingData;
    private List<char[]> testData;
    private List<List<Character>> allAttributes;
    private Map<Character, List<char[]>> classSubsets;
    private Map<Character, Double> classProbabilities;
    private int indexOfClassLabel;
    private int numberOfAttributes;

    public Bayes() {
        trainingData = new ArrayList<char[]>();
        allAttributes = new ArrayList<List<Character>>();
        classSubsets = new HashMap<Character, List<char[]>>();
        classProbabilities = new HashMap<Character, Double>();
        indexOfClassLabel = -1;
        numberOfAttributes = -1;
    }

    public void setData(List<char[]> data, int indexOfClassLabel) {
        this.trainingData = data;
        this.indexOfClassLabel = indexOfClassLabel;
        numberOfAttributes = data.get(0).length;
        generateUniqueAttributes();
        findClassSubsets();
    }

    public void setTestData(List<char[]> data) {
        this.testData = data;
    }

    // tests each tuple in testData
    public void test(boolean usingLaplacian) {
        List<Character> classLabels = allAttributes.get(indexOfClassLabel);
        int correct = 0, incorrect = 0;

        for (char[] testTuple : testData) {
            char trueClassification = testTuple[indexOfClassLabel];
            char testClassification = '\0';
            double oldTotalProbability = 0.0;

            for (Character classLabel : classLabels) {
                List<char[]> subset = classSubsets.get(classLabel);
                double priorProbability = classProbabilities.get(classLabel);
                double conditionalProbability = 1.0;

                for (int indexOfAttribute = 0; indexOfAttribute < numberOfAttributes; indexOfAttribute++) {
                    if (indexOfAttribute == indexOfClassLabel) {
                        continue;
                    }
                    int count = countAttributeInSubset(testTuple[indexOfAttribute], subset, indexOfAttribute);
                    if (usingLaplacian) {
                        conditionalProbability *= (double) (count + 1) / (subset.size() + classLabels.size());
                    } else {
                        conditionalProbability *= (double) count / subset.size();
                    } 
                }
                double newTotalProbability = conditionalProbability * priorProbability;
                if (newTotalProbability > oldTotalProbability) {
                    oldTotalProbability = newTotalProbability;
                    testClassification = classLabel;
                }
            }
            if (trueClassification == testClassification) {
                correct++;
            } else {
                incorrect++;
            }
        }
        Classifier.printAccuracy(correct, incorrect, testData.size());
    }

    // splits up the data so that each unique class's subset is already there 
    private void findClassSubsets() {
        List<Character> uniqueAttributes = allAttributes.get(indexOfClassLabel);

        for (Character attribute : uniqueAttributes) {
            List<char[]> subset = getSubsetWithAttribute(attribute, indexOfClassLabel);
            classSubsets.put(attribute, subset);
            classProbabilities.put(attribute, (double) subset.size() / trainingData.size());
        }
    }

    // returns a subset that has a certain attribute value
    private List<char[]> getSubsetWithAttribute(char attribute, int indexOfAttribute) {
        List<char[]> subset = new ArrayList<char[]>();

        for (char[] tuple : trainingData) {
            if (tuple[indexOfAttribute] == attribute) {
                subset.add(tuple);
            }
        }

        return subset;
    }

    // count how many attributes there are in a subset of tuples
    private int countAttributeInSubset(char attribute, List<char[]> subset, int indexOfAttribute) {
        int sum = 0;

        for (char[] tuple : subset) {
            if (tuple[indexOfAttribute] == attribute) {
                sum++;
            }
        }

        return sum;
    }

    // finds all unique values for each attribute
    private void generateUniqueAttributes() {
        for (int i = 0; i < numberOfAttributes; i++) {
            List<Character> attributeSet = new ArrayList<Character>();
            allAttributes.add(attributeSet);
        }

        for (char[] record : trainingData) {
            for (int i = 0; i < numberOfAttributes; i++) {
                List<Character> uniqueSet = allAttributes.get(i);
                if (!uniqueSet.contains(record[i])) {
                    uniqueSet.add(record[i]);
                }
            }
        }
    }
}