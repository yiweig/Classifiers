import java.io.FileNotFoundException;
import java.util.*;

/* THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Yiwei Gao */

public class Classifier {

    private TreeNode root;
    private Bayes bayes;
    private List<char[]> trainingData;
    private List<char[]> testData;
    private int indexOfClassLabel;
    private boolean usingC45;
    private Type type;

    public Classifier(Type type, String trainingData, String testData, int indexOfClassLabel) {
        this.type = type;
        this.indexOfClassLabel = indexOfClassLabel;
        try {
            this.trainingData = HWThree.readData(trainingData);
            this.testData = HWThree.readData(testData);
        } catch (FileNotFoundException e) {
            HWThree.fileError();
        }

        if (this.type == Type.C45 || this.type == Type.ID3) {
            usingC45 = type == Type.C45;
            root = new TreeNode();
            root.setTrainingDataInitial(this.trainingData, this.indexOfClassLabel);
            root.setType(TreeNode.Type.ROOT);
            learn();
        } else {
            bayes = new Bayes();
            bayes.setData(this.trainingData, this.indexOfClassLabel);
            bayes.setTestData(this.testData);
        }
    }

    // prints out accuracy of classifier
    public static void printAccuracy(int numberCorrect, int numberIncorrect, int total) {

        double accuracy = (double) numberCorrect / total;

        System.out.println("Number correct: " + numberCorrect + "\n" +
                "Number incorrect: " + numberIncorrect + "\n" +
                "Accuracy: " + accuracy * 100 + "%");
    }

    // tests the classifier, depending on if its a decision tree or Bayesian
    public void test() {
        if (type == Type.BAYES || type == Type.BAYES_WITH_LAPLACE) {
            testBayes();
        } else {
            testTree();
        }
    }

    // starts learning from root
    private void learn() {
        learn(root);
    }

    // recursively learns starting at given node
    private void learn(TreeNode node) {
        if (node.getType() != TreeNode.Type.LEAF) {
            int indexOfPartitioningAttribute = node.findPartitioningAttribute(usingC45);
            node.partitionData(indexOfPartitioningAttribute);

            for (TreeNode child : node.getChildren()) {
                learn(child);
            }
        }
    }

    // test each tuple in the test data against the tree
    private void testTree() {
        int numberCorrect = 0, numberIncorrect = 0;

        for (char[] tuple : testData) {
            TreeNode node = root;
            char trueClassification = tuple[indexOfClassLabel];
            char testClassification = classify(tuple, node);

            if (testClassification == trueClassification) {
                numberCorrect++;
            } else {
                numberIncorrect++;
            }
        }

        printAccuracy(numberCorrect, numberIncorrect, testData.size());
    }

    // classifies the given tuple based on decision tree path
    private char classify(char[] tuple, TreeNode node) {
        char testClassification = '\0';

        while (node.getType() != TreeNode.Type.LEAF) {
            List<TreeNode> children = node.getChildren();
            int index = node.getSplittingAttribute();
            for (TreeNode child : children) {
                if (tuple[index] == child.getBranch()) {
                    node = child;
                    break;
                }
            }
        }

        testClassification = node.getLabel();

        assert testClassification != '\0';
        return testClassification;
    }

    // prints the tree starting at the root node
    public void print() {
        print(root, 3);
    }

    // prints out the subtree starting at given node
    private void print(TreeNode node, int depth) {
        System.out.println(
                String.format("%" + depth + "s",
                        node.getBranch() + " = " + node.getLabel()));

        if (node.getType() != TreeNode.Type.LEAF) {
            for (TreeNode child : node.getChildren()) {
                print(child, depth + 3);
            }
        }
    }

    // tests Bayesian classifier
    private void testBayes() {
        bayes.test(type == Type.BAYES_WITH_LAPLACE);
    }

    public enum Type {
        ID3, C45, BAYES, BAYES_WITH_LAPLACE
    }
}