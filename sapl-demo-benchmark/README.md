# build and generate jar file
```shell
mvn clean install
mvn -B verify failsafe:integration-test
```

# Start benchmark
```shell
# small_remote_benchmark
java -jar target/sapl-pdp-benchmark-1.0.0-SNAPSHOT-jar-with-dependencies.jar --cfg examples/small_remote_benchmark.yaml --output results/small_remote_benchmark/

# small_docker_benchmark
java -jar target/sapl-pdp-benchmark-1.0.0-SNAPSHOT-jar-with-dependencies.jar --output results/small_docker_benchmark --cfg examples/small_docker_benchmark.yaml

# large_docker_benchmark
java -jar target/sapl-pdp-benchmark-1.0.0-SNAPSHOT-jar-with-dependencies.jar --cfg examples/large_docker_benchmark.yaml --output results/large_docker_benchmark/


# large_docker_benchmark
java -jar target/sapl-pdp-benchmark-1.0.0-SNAPSHOT-jar-with-dependencies.jar --cfg src/test/resources/test_benchmark_config.yaml --output results/tmp_benchmark_test
```