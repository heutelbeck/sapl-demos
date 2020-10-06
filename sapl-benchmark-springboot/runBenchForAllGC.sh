#!/bin/bash

SEED=$RANDOM

java --version

#echo "G1"
# java -Xms4096m -Xmx8192m -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index IMPROVED -iter 1 --sapl.random.seed=$SEED --sapl.number.of.benchmark.runs=10


echo "SHENANDOAH"
 java -Xms4096m -Xmx8192m -XX:+UseShenandoahGC -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index IMPROVED -iter 1 --sapl.random.seed=$SEED --sapl.number.of.benchmark.runs=3


echo "ZGC"
java -Xms4096m -Xmx8192m -XX:+UseZGC -jar target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar -index IMPROVED -iter 1 --sapl.random.seed=$SEED --sapl.number.of.benchmark.runs=3
