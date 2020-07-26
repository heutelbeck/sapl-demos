#!/usr/bin/env bash

JARFILE="target/sapl-benchmark-2.0.0-SNAPSHOT-jar-with-dependencies.jar"
GRAALVM_HOME="/Library/Java/JavaVirtualMachines/graalvm-ce-java8-20.1.0/Contents/Home"
NATIVE_IMAGE_OPTIONS="--no-fallback --allow-incomplete-classpath"

echo "building graal vm native-image using $JARFILE"

${GRAALVM_HOME}/bin/native-image ${NATIVE_IMAGE_OPTIONS} -jar ${JARFILE}

