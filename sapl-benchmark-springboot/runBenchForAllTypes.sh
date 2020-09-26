#!/bin/bash

#for i in {1..10}
#do
	SEED=$RANDOM
	RUNS=100
	ITER=5

	echo "SIMPLE - $SEED"
	time java -Xms4096m -Xmx8192m -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index SIMPLE -iter $ITER --sapl.random.seed=$SEED --sapl.number.of.benchmark.runs=$RUNS

	echo "IMPROVED - $SEED"
	time java -Xms4096m -Xmx8192m -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index IMPROVED -iter $ITER --sapl.random.seed=$SEED --sapl.number.of.benchmark.runs=$RUNS

	#echo "FAST"
	#time java -Xms4096m -Xmx8192m -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index FAST -reuse false
#done
