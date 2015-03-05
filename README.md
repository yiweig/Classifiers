# Classifiers for CS 378 Data Mining  

Supported classifiers: 

ID3 and C4.5 decision treess, along with Naive Bayesian with and without Laplacian Correction  

To compile:
  
        javac HWThree.java  
    
    
To run: 
 
        java HWThree [-option] [training_data] [test_data] [index_of_class_label] [-l]
        
    Where 0 <= 'index_of_class_label' < total number of attributes
    Default behavior is to run the dataset from the textbook using the C4.5 decision tree
    
        Options:
            -b   use Bayes  ...  -l use Laplacian correction
            -c   use C4.5
            -i   use ID3
    
    Note: training and test data must be in the same directory as the program files!