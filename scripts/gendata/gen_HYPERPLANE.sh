#!/bin/bash
DIR=$PWD
DATA_DIR="."
cd .

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile -s (generators.HyperplaneGenerator -a 15 -k 7 -c 5 -t 0.001 -n 5 -s 1)
-f $DATA_DIR/HYPERPLANE_1.arff -m 500000"

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile -s (generators.HyperplaneGenerator -a 15 -k 7 -c 5 -t 0.01 -n 5 -s 1)
-f $DATA_DIR/HYPERPLANE_2.arff -m 500000"

sed -i 's/,$//' $DATA_DIR\\*.arff

cd $DIR