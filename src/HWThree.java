import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/* THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Yiwei Gao */

public class HWThree {

    public static final int FILE_ERROR = 1;
    public static final int ARGUMENT_ERROR = 2;

    public static void main(String[] args) throws IOException {

        // defaults
        String trainingData = "textbook.txt";
        String testData = "test.txt";
        int indexOfClassLabel = 4;
        Classifier.Type type = Classifier.Type.C45;

        // validates input
        if (args.length == 0) {
            Classifier defaultClassifier = new Classifier(type, trainingData, testData, indexOfClassLabel);
            defaultClassifier.print();
            defaultClassifier.test();
        } else if (args.length == 4 || args.length == 5) {
            trainingData = args[1];
            testData = args[2];
            indexOfClassLabel = Integer.parseInt(args[3]);

            if (args[0].equals("-b")) {
                type = Classifier.Type.BAYES;
                if (args.length == 5) {
                    if (args[4].equals("-l")) {
                        type = Classifier.Type.BAYES_WITH_LAPLACIAN;
                    } else {
                        argumentError();
                    }
                }
            } else if (args[0].equals("-c")) {
                type = Classifier.Type.C45;
            } else if (args[0].equals("-i")) {
                type = Classifier.Type.ID3;
            } else {
                argumentError();
            }

            Classifier classifier = new Classifier(type, trainingData, testData, indexOfClassLabel);

            if (type == Classifier.Type.C45 || type == Classifier.Type.ID3) {
                classifier.print();
            }

            System.out.println("Testing " + testData + "...");
            classifier.test();

        } else {
            argumentError();
        }
    }

    // reads in data
    public static List<char[]> readData(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        StringTokenizer line;
        List<char[]> data;

        data = new ArrayList<char[]>();

        do {
            try {
                line = new StringTokenizer(scanner.nextLine());
            } catch (NoSuchElementException e) {
                break;
            }
            int numberOfAttributes = line.countTokens();

            char[] tuple = new char[numberOfAttributes];
            for (int i = 0; i < numberOfAttributes; i++) {
                String value = line.nextToken();
                assert value.length() == 1;
                tuple[i] = value.charAt(0);
            }
            data.add(tuple);
        } while (true);

        if (data.size() == 0) {
            fileError();
        }

        return data;
    }

    // called when specified file does not exist/cannot be found
    public static void fileError() {
        System.out.println("Specified file(s) missing or empty!");
        System.exit(FILE_ERROR);
    }

    // called when arguments are incorrect
    public static void argumentError() {
        System.out.println("Argument error!");
        System.out.println("Usage: \n\tjava HWThree [-option] [training_data] [test_data] [index_of_class_label] [-l]");
        System.out.println("\t0 <= 'index_of_class_label' < total number of attributes");
        System.out.println("\tDefault behavior is to run the dataset from the textbook using C4.5");
        System.out.println("\tOptions:\n" +
                "\t\t-b\t use Bayes" + "  ...  -l use Laplace smoothing" +
                "\n" +
                "\t\t-c\t use C4.5" +
                "\n" +
                "\t\t-i\t use ID3");
        System.out.println("\nExample usages: " +
                "\n\tjava HWThree -b mushroom.training mushroom.test 0 -l" +
                "\n\tjava HWThree -b mushroom.training mushroom.test 0" +
                "\n\tjava HWThree -c mushroom.training mushroom.test 0" +
                "\n\tjava HWThree -i mushroom.training mushroom.test 0");
        System.exit(ARGUMENT_ERROR);
    }
}