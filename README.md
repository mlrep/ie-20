# Experiments for: *Instance exploitation for learning temporary concepts from sparsely labeled drifting data streams*

### Requirements

* Install Java 11.
* The experiments use: MOA 2019.05.0 (moa.cms.waikato.ac.nz/downloads), jcommon-1.0.23, jfreechart-1.0.19 and javafx-base-11.
* For the post-processing scripts you will need: Python 3.6.

### Data

Two sets of streams are required: real streams and synthetic streams. The former can be downloaded from websites given below. The latter can be generated based on the real ones, using given scripts. 

#### Real streams

* ACTIVITY and ACTIVITY_RAW: http://www.cis.fordham.edu/wisdm/dataset.php
* AIRLINES: https://moa.cms.waikato.ac.nz/datasets/
* CONNECT4: http://archive.ics.uci.edu/ml/datasets/connect-4
* COVERTYPE: http://www.openml.org/d/150
* CRIMES: https://www.kaggle.com/c/sf-crime/data
* DJ30: (given in the ***data*** directory)
* EEG: https://archive.ics.uci.edu/ml/datasets/EEG+Eye+State
* ELEC: http://moa.cms.waikato.ac.nz/datasets
* GAS: http://archive.ics.uci.edu/ml/datasets/Gas+Sensor+Array+Drift+Dataset
* POKER: http://moa.cms.waikato.ac.nz/datasets
* SENSOR: http://www.cse.fau.edu/~xqzhu/stream.html
* SPAM: http://mlkd.csd.auth.gr/concept_drift.html
* WEATHER: http://users.rowan.edu/~polikar/research/NSE/

All the data streams should be converted to ARFF files. Make sure that all the real streams are in: ***streams/real***.

#### Semi-synthetic streams

* Run scripts in ***scripts/gendata*** (you will need ***moa.jar*** for that).
* The synthetic streams should be generated into: ***streams/synthetic***.
* The directory should consist of the following streams: SEA_1, SEA_2, STAGGER_1, STAGGER_2, HYPERPLANE_1, HYPERPLANE_2, TREE_1, TREE_2, TREE_3, TREE_4, RBF_1, RBF_2, RBF_3, RBF_4.
* Do not change the file paths - they should be organized in this way when running the experiments in the next step.

### Package

We can also provide the whole package with all the data streams on request: *mlrep.contact@gmail.com*

### Testing
All unit tests can be found in ***tests***.

### Running experiments
- In order to conduct experiments set paths in ***src/eval/Evaluator.java*** and run it. You can pick between different experiments in *runInstanceExploitationExperiments()*.
* Details of experiments are defined in ***src/eval/cases/os***. 
* Comments describe how parameters should be set for different configurations. 
* To change the base learner simply uncomment a selected classifier in ***src/eval/experiment/ExperimentRow.java***
* To run selected rows on specific data uncomment selected streams in ***src/eval/experiment/ExperimentStream.java***
* The final results should be now available in the ***results*** directory.
