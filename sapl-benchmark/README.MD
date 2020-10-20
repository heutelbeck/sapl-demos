# READ ME

This is the deprecated version of the SAPL PRP Index Benchmark Application.
Keep this in mind while working with this tool.

The current implementation can be found in the Maven module [sapl-benchmark-springboot](../sapl-benchmark-springboot/).

## Setup Benchmark - sapl-benchmark (deprecated)

`Benchmark.java` requires a working directory for the benchmark. By default this value is set to:

`private static final String DEFAULT_PATH = System.getProperty("user.home") + "/sapl/";`

Make sure this directory is existing on your file system, including the sub-directory `tests` containing the test configurations.

Furthermore, a `pdp.json` file is expected. If everything is correctly set-up, it should look like this:

Examples of both files can be found [here](./src/main/resources/).

```
$ pwd
/sapl
$ ls
pdp.json	tests
$ ls tests/
tests.json
```

## Run Benchmark

The most convenient way to execute the benchmark is to use the BASH script [`runBench.sh`](./runBench.sh).
It runs the benchmark for both index implementations using the same test configuration.

Make sure to build the sapl-benchmark application with Maven before executing the script!

The output should look similar to this:
```
$ ./runBench.sh
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)
running bench for SIMPLE index - generating new policies
```


## Benchmark Output

The application writes all generated policies to the defined directory using subfolders with the name of the test configuration.

The results of all executed benchmarks are stored in a seperate directory consisting of a timestamp and the index type (e.g. `2020-10-20T10:17:47.259874_SIMPLE`).
