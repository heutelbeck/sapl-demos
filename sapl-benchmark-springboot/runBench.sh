#!/usr/bin/env bash

JARFILE="target/sapl-benchmark-springboot-1.0.0-SNAPSHOT.jar"
COMPARISON_ID=$(uuidgen)

java -version

echo "running bench for SIMPLE index - generating new policies"
java -jar $JARFILE -index "SIMPLE" -reuse "false" -cid $COMPARISON_ID

echo "running bench for FAST index"
java -jar $JARFILE -index "FAST" -reuse "true" -cid $COMPARISON_ID

echo "running bench for IMPROVED index"
java -jar $JARFILE -index "IMPROVED" -reuse "true" -cid $COMPARISON_ID
