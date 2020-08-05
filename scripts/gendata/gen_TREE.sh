#!/bin/bash
DIR=$PWD
DATA_DIR="."
cd .

# TREE stream 1:
# I->II 250000 100, II->III 500000 100, II->III 750000 100

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile 
-s (ConceptDriftStream 
  -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 2)
  -p 250000 -w 100
  -d (ConceptDriftStream 
    -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 1)
    -p 250000 -w 100
    -d (ConceptDriftStream 
      -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 3)
      -d (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 4)
      -p 250000 -w 100
    )
  )
) -f $DATA_DIR/TREE_1.arff -m 1000000"

# TREE stream 2:
# I->II 250000 10000, II->III 500000 10000, II->III 750000 10000

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile 
-s (ConceptDriftStream 
  -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 2)
  -p 250000 -w 10000
  -d (ConceptDriftStream 
    -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 1)
    -p 250000 -w 10000
    -d (ConceptDriftStream 
      -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 3)
      -d (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 4)
      -p 250000 -w 10000
    )
  )
) -f $DATA_DIR/TREE_2.arff -m 1000000"

# TREE stream 3:
# I->II 400000 50000, II->III 800000 50000

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile 
-s (ConceptDriftStream 
  -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 2)
  -p 400000 -w 50000
  -d (ConceptDriftStream 
    -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 1)
    -d (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 3)
    -p 400000 -w 50000
  )
) -f $DATA_DIR/TREE_3.arff -m 1200000"

# TREE stream 4:
# I->II 400000 100000, II->III 800000 100000

java -cp moa.jar -javaagent:sizeofag.jar moa.DoTask \
"WriteStreamToARFFFile 
-s (ConceptDriftStream 
  -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 2)
  -p 400000 -w 100000
  -d (ConceptDriftStream 
    -s (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 1)
    -d (generators.RandomTreeGenerator -c 5 -o 0 -u 15 -r 3)
    -p 400000 -w 100000
  )
) -f $DATA_DIR/TREE_4.arff -m 1200000"

sed -i 's/,$//' $DATA_DIR\\*.arff

cd $DIR