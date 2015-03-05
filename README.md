# Classifiers for CS 378 Data Mining  
### (Homework 3)

Supported classifiers: 

* ID3 decision tree
* C4.5 decision tree
* Naive Bayesian classifier without Laplacian correction
* Naive Bayesian classifier with Laplacian correction
  

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