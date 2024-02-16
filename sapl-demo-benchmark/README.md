# Benchmarking tool for SAPL Policy Decision Points

## Overview
This Benchmarking tool can be used to run performance tests against SAPL Policy Decision Points (PDP). The benchmarking 
tool supports embedded as well as remote (http and rsocket) PDP connections. The benchmark be used with an existing PDP 
infrastructures (target=remote) or it can spin off the required PDP Server using Docker (target=docker).

Furthermore, it supports different authentication methods (NoAuth, BasicAuth, ApiKeyAuth and Oauth2). 

The Benchmark is based on [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh) and at the end of each 
run a html report is generated

## Running the Benchmark

The benchmarking tool reads the benchmark config from a file (--cfg parameter) and generates a report in
the (--output folder). 

First build the demo, by changing into the `sapl-demo-benchmark` folder and execute the command:

```
mvn install
```

After the build completes, the `target` folder contains the executable "fat-jar" with all dependencies.
This allows us to easily copy the jar to another host and to execute it against an existing PDP infrastructure.

Execute the following command to run the benchmark:
```
java -jar target/sapl-demo-benchmark-3.0.0-SNAPSHOT-jar-with-dependencies.jar
```

The benchmark accepts the following command line parameters:

```
Usage: sapl-demo-benchmark [-hV] [--skipBenchmark] [--skipReportGeneration]
                           -c=<cfgFilePath> [-o=<outputPath>]
Performs a benchmark on the PRP indexing data structures.
  -c, --cfg=<cfgFilePath>   YAML file to read json from
  -h, --help                Show this help message and exit.
  -o, --output=<outputPath> Path to the output directory for benchmark results.
      --skipBenchmark
      --skipReportGeneration

  -V, --version             Print version information and exit.
```

Examples 
```
# small_remote_benchmark
java -jar target/sapl-demo-benchmark-3.00-SNAPSHOT-jar-with-dependencies.jar --cfg examples/small_remote_benchmark.yaml --output results/small_remote_benchmark/

# small_docker_benchmark
java -jar target/sapl-demo-benchmark-3.00-SNAPSHOT-jar-with-dependencies.jar --output results/small_docker_benchmark --cfg examples/small_docker_benchmark.yaml

# large_docker_benchmark
java -jar target/sapl-demo-benchmark-3.00-SNAPSHOT-jar-with-dependencies.jar --cfg examples/large_docker_benchmark.yaml --output results/large_docker_benchmark/


# large_docker_benchmark
java -jar target/sapl-demo-benchmark-3.00-SNAPSHOT-jar-with-dependencies.jar --cfg src/test/resources/test_benchmark_config.yaml --output results/tmp_benchmark_test
```