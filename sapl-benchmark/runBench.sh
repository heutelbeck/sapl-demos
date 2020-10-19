#!/usr/bin/env bash

JARFILE="target/sapl-benchmark-2.0.0-SNAPSHOT-jar-with-dependencies.jar"

java -version

echo "running bench for SIMPLE index - generating new policies"
java -jar $JARFILE -index "SIMPLE" -reuse "false"

echo "running bench for FAST index"
java -jar $JARFILE -index "FAST" -reuse "true"

echo "running bench for IMPROVED index"
java -jar $JARFILE -index "IMPROVED" -reuse "true"
