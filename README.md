# Classifiers for CS 378 Data Mining  
### (Homework 3 | Implemented in Java)

Supported classifiers: 

1. [ID3](http://en.wikipedia.org/wiki/ID3_algorithm) decision tree
2. [C4.5](http://en.wikipedia.org/wiki/C4.5_algorithm) decision tree
3. [Naive Bayes](http://en.wikipedia.org/wiki/Naive_Bayes_classifier) classifier with Laplace smoothing\*  
4. Naive Bayes classifier without Laplace smoothing\*  
\* Laplace smoothing == Laplacian correction == [additive smoothing](http://en.wikipedia.org/wiki/Additive_smoothing)
  

Compilation:
  
        javac HWThree.java  
    
    
Usage: 
 
        java HWThree [-option] [training_data] [test_data] [index_of_class_label] [-l]
        
    Where 0 <= 'index_of_class_label' < total number of attributes
    Default behavior is to run the dataset from the textbook using the C4.5 decision tree
    
        Options:
            -b   use Bayes  ...  -l use Laplace smoothing
            -c   use C4.5
            -i   use ID3
    
    Example usages:
        java HWThree -b mushroom.training mushroom.test 0 -l
        java HWThree -b mushroom.training mushroom.test 0
        java HWThree -c mushroom.training mushroom.test 0
        java HWThree -i mushroom.training mushroom.test 0
    
    Note: training and test data must be in the same directory as the program files!
    
    
**mushroom.training** and **mushroom.test** data are compiled from their [original source](http://archive.ics.uci.edu/ml/datasets/Mushroom) located at the UCI Machine Learning Repository,
with attribute 11 removed because of missing values. Class labels (poisonous "p" or edible "e") are the first attribute (index = 0).

**textbook.txt** is data from the class textbook, page 338 of *Data Mining: Concepts and Techniques, Third Edition*, by Han, Kamber, and Pei.  
**test.txt** contains one line that represents a tuple that needs to be classified. This tuple was personally created, with some influence from the test tuples from the textbook.  

## Known Issues  
- The decision tree algorithm does not currently implement [pruning](http://en.wikipedia.org/wiki/Pruning_%28decision_trees%29), meaning that outliers and/or noisy data will sometimes result in [overfitting](http://en.wikipedia.org/wiki/Overfitting). 