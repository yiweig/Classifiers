import java.util.*;

/* THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Yiwei Gao */

public class TreeNode {

    private static final double DIVISOR = Math.log10(2);
    private static int INDEX_OF_CLASS_LABEL;
    private static int NUMBER_OF_ATTRIBUTES;
    private static boolean[] ATTRIBUTE_LIST;
    private static List<List<Character>> ALL_ATTRIBUTES;
    private double infoOfData;
    private int numberOfTuples;
    private int splittingAttribute;
    private char branch;
    private char label;
    private List<char[]> trainingData;
    private List<TreeNode> children;
    private Type type;

    public TreeNode() {
        numberOfTuples = -1;
        infoOfData = -1;
        splittingAttribute = -1;
        trainingData = new ArrayList<char[]>();
        children = new ArrayList<TreeNode>();
    }

    public char getLabel() {
        return label;
    }

    public int getSplittingAttribute() {
        return splittingAttribute;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // returns the branch for this node, which is the outcome that led to this node
    public char getBranch() {
        return branch;
    }

    // set the branch for this node, which is the outcome that leads to this node
    public void setBranch(char branch) {
        this.branch = branch;
    }

    // sets the data for this node
    public void setTrainingData(List<char[]> data) {
        this.trainingData = data;
        NUMBER_OF_ATTRIBUTES = this.trainingData.get(0).length;
        numberOfTuples = this.trainingData.size();
        infoOfData = calculateInfoOfSubset(this.trainingData, INDEX_OF_CLASS_LABEL);
        if (tuplesAreAllSameClass()) {
            convertToLeafNodeWithMajority(false);
        } else if (attributeListIsEmpty()) {
            convertToLeafNodeWithMajority(true);
        }
    }

    // sets the initial dataset, should only be called by root
    public void setTrainingDataInitial(List<char[]> data, int indexOfClassLabel) {
        this.trainingData = data;
        numberOfTuples = this.trainingData.size();
        NUMBER_OF_ATTRIBUTES = this.trainingData.get(0).length;

        if (indexOfClassLabel < 0 || indexOfClassLabel > NUMBER_OF_ATTRIBUTES) {
            HWThree.argumentError();
        }

        ALL_ATTRIBUTES = new ArrayList<List<Character>>();

        createAttributeList(indexOfClassLabel);
        generateUniqueAttributes();

        infoOfData = calculateInfoOfSubset(this.trainingData, INDEX_OF_CLASS_LABEL);
    }

    // create static list of all attributes
    private void createAttributeList(int indexOfClassLabel) {
        INDEX_OF_CLASS_LABEL = indexOfClassLabel;
        ATTRIBUTE_LIST = new boolean[NUMBER_OF_ATTRIBUTES];
        Arrays.fill(ATTRIBUTE_LIST, true);
        ATTRIBUTE_LIST[INDEX_OF_CLASS_LABEL] = false;
    }

    // finds all unique values for each attribute
    private void generateUniqueAttributes() {
        for (int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
            List<Character> attributeSet = new ArrayList<Character>();
            ALL_ATTRIBUTES.add(attributeSet);
        }

        for (char[] record : trainingData) {
            for (int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
                List<Character> uniqueAttributes = ALL_ATTRIBUTES.get(i);
                if (!uniqueAttributes.contains(record[i])) {
                    uniqueAttributes.add(record[i]);
                }
            }
        }
    }

    // finds and returns the index of the attribute that has the highest gain
    public int findPartitioningAttribute(boolean usingC45) {
        int indexOfAttribute = -1;
        double oldGain = 0, oldGainRatio = 0;

        for (int currentAttribute = 0; currentAttribute < NUMBER_OF_ATTRIBUTES; currentAttribute++) {
            if (currentAttribute == INDEX_OF_CLASS_LABEL || !ATTRIBUTE_LIST[currentAttribute]) {
                continue;
            }

            double infoOfAttribute = 0;
            List<Character> uniqueAttributes = ALL_ATTRIBUTES.get(currentAttribute);

            List<char[]> subsetForSplitInfo = new ArrayList<char[]>();

            for (Character attribute : uniqueAttributes) {
                List<char[]> subsetForInfo = new ArrayList<char[]>();
                for (int i = 0; i < numberOfTuples; i++) {
                    char[] tuple = trainingData.get(i);
                    if (tuple[currentAttribute] == attribute) {
                        subsetForInfo.add(tuple);
                        subsetForSplitInfo.add(tuple);
                    }
                }
                int subsetSize = subsetForInfo.size();
                double infoOfSubset = calculateInfoOfSubset(subsetForInfo, INDEX_OF_CLASS_LABEL);
                infoOfAttribute += (((double) subsetSize / numberOfTuples) * infoOfSubset);
            }

            double splitInfo = calculateInfoOfSubset(subsetForSplitInfo, currentAttribute);
            double gain = infoOfData - infoOfAttribute;
            double gainRatio = gain / splitInfo;

            if (usingC45) {
                // using gain ratio; C4.5
                if (gainRatio > oldGainRatio) {
                    oldGainRatio = gainRatio;
                    infoOfAttribute = 0;
                    indexOfAttribute = currentAttribute;
                }

            } else {
                // using gain; ID3 
                if (gain > oldGain) {
                    oldGain = gain;
                    infoOfAttribute = 0;
                    indexOfAttribute = currentAttribute;
                }
            }
//            System.out.println(gain);
//            System.out.println(splitInfo);
//            System.out.println(gainRatio + " -> " + currentAttribute);
        }

        assert indexOfAttribute > -1;
        splittingAttribute = indexOfAttribute;
        return indexOfAttribute;
    }

    // partition data based on indicated index
    public void partitionData(int indexOfPartitioningAttribute) {
        if (type == Type.LEAF) {
            return;
        }
        assert indexOfPartitioningAttribute == splittingAttribute;
        List<Character> uniqueAttributes = ALL_ATTRIBUTES.get(indexOfPartitioningAttribute);
        ATTRIBUTE_LIST[indexOfPartitioningAttribute] = false;

        for (Character attribute : uniqueAttributes) {
            TreeNode child = new TreeNode();
            List<char[]> subset = new ArrayList<char[]>();

            for (char[] tuple : trainingData) {
                if (tuple[indexOfPartitioningAttribute] == attribute) {
                    subset.add(tuple);
                }
            }

            children.add(child);
            child.setBranch(attribute);

            // what happens when a subset is size = 0?
            if (subset.size() == 0) {
                child.setType(Type.LEAF);
                continue;
            }

            child.setType(Type.INTERNAL);
            child.setTrainingData(subset);
        }
    }

    // calculate the info of the subset based on indicated attribute
    private double calculateInfoOfSubset(List<char[]> subset, int indexOfAttribute) {
        int subsetSize = subset.size();
        if (subsetSize == 0) {
            return 0;
        }
        double totalInfo = 0;

        List<Character> uniqueAttributes = ALL_ATTRIBUTES.get(indexOfAttribute);

        for (Character attribute : uniqueAttributes) {
            int countOfValue = countAttributeInSubset(attribute, subset, indexOfAttribute);
            double frequency = (double) countOfValue / subsetSize;
            totalInfo -= (frequency * log2(frequency));
        }

        return totalInfo;
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

    // check if tuples all belong to the same class
    public boolean tuplesAreAllSameClass() {
        boolean sameClass = false;

        char value = trainingData.get(0)[INDEX_OF_CLASS_LABEL];
        for (int i = 1; i < trainingData.size(); i++) {
            if (trainingData.get(i)[INDEX_OF_CLASS_LABEL] == value) {
                sameClass = true;
            } else {
                sameClass = false;
                break;
            }
        }
        return sameClass;
    }

    // check if attribute list is empty
    public boolean attributeListIsEmpty() {
        for (int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
            if (ATTRIBUTE_LIST[i]) {
                return false;
            }
        }
        return true;
    }

    // convert to leaf node, either with or without majority voting
    public void convertToLeafNodeWithMajority(boolean majority) {
        if (type != Type.LEAF) {
            type = Type.LEAF;

            if (majority) {
                label = getMajorityClass();
            } else {
                label = trainingData.get(0)[INDEX_OF_CLASS_LABEL];
            }
        }
    }

    // count the majority class label
    private char getMajorityClass() {
        List<Character> uniqueAttributes = ALL_ATTRIBUTES.get(INDEX_OF_CLASS_LABEL);

        Map<Character, Integer> counts = new HashMap<Character, Integer>();

        for (Character attribute : uniqueAttributes) {
            int count = countAttributeInSubset(attribute, trainingData, INDEX_OF_CLASS_LABEL);
            counts.put(attribute, count);
        }

        int max = -1;
        char majority = '\0';

        for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                majority = entry.getKey();
                max = entry.getValue();
            }
        }

        assert majority != '\0';
        return majority;
    }

    // custom log2 method; if 0, returns 0 rather than NaN
    private double log2(double value) {
        return value == 0 ? 0 : (Math.log10(value) / DIVISOR);
    }

    // node type
    public enum Type {
        INTERNAL, LEAF, ROOT
    }
}