/*******************************************************************************
 * Copyright 2017-2018 Dominic Heutelbeck (dheutelbeck@ftk.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package io.sapl.benchmark;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.generator.DomainGenerator;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchart.XYChart;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static io.sapl.benchmark.BenchmarkConstants.DEFAULT_HEIGHT;
import static io.sapl.benchmark.BenchmarkConstants.DEFAULT_WIDTH;
import static io.sapl.benchmark.BenchmarkConstants.ERROR_READING_TEST_CONFIGURATION;
import static io.sapl.benchmark.BenchmarkConstants.HELP;
import static io.sapl.benchmark.BenchmarkConstants.HELP_DOC;
import static io.sapl.benchmark.BenchmarkConstants.INDEX;
import static io.sapl.benchmark.BenchmarkConstants.INDEX_DOC;
import static io.sapl.benchmark.BenchmarkConstants.ITERATIONS;
import static io.sapl.benchmark.BenchmarkConstants.ITERATIONS_DOC;
import static io.sapl.benchmark.BenchmarkConstants.PATH;
import static io.sapl.benchmark.BenchmarkConstants.PATH_DOC;
import static io.sapl.benchmark.BenchmarkConstants.TEST;
import static io.sapl.benchmark.BenchmarkConstants.TEST_DOC;
import static io.sapl.benchmark.BenchmarkConstants.USAGE;
import static io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType.FAST;
import static io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType.IMPROVED;
import static io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType.SIMPLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class Benchmark implements CommandLineRunner {

    private static final TestRunner TEST_RUNNER = new TestRunner();

    public static final String DEFAULT_PATH = System.getProperty("user.home") + "/benchmarks/";
    private static final double REMOVE_EDGE_DATA_BY_PERCENTAGE = 0.005D;
    private static final boolean PERFORM_RANDOM_BENCHMARK = false;

    private final DomainGenerator domainGenerator;

    private int numberOfBenchmarks = 2;

    private IndexType indexType = IMPROVED;

    private String path = DEFAULT_PATH;
    private String testFilePath;
    private String filePrefix;

    private final List<Long> seedList = new LinkedList<>();

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("command line runner started");

        parseCommandLineArguments(args);

        init();
        runBenchmark(path);

        System.exit(0);
    }

    private void init() {
        filePrefix = String.format("%s_%s_%s/",
                LocalDateTime.now(), indexType, PERFORM_RANDOM_BENCHMARK ? "RANDOM" : "SYSTEMATIC");

        LOGGER.info("\n randomBenchmark={},\n numberOfBenchmarks={}," +
                        "\n index={},\n initialSeed={},\n runs={}," +
                        "\n testfile={},\n filePrefix={}",
                PERFORM_RANDOM_BENCHMARK, numberOfBenchmarks,
                indexType, domainGenerator.getDomainData().getSeed(),
                domainGenerator.getDomainData().getNumberOfBenchmarkRuns(),
                testFilePath, filePrefix);

        try {
            final Path dir = Paths.get(path, filePrefix);
            Files.createDirectories(dir);
        } catch (IOException e) {
            LOGGER.error(ERROR_READING_TEST_CONFIGURATION, e);
        }

        // seed list
        seedList.add(domainGenerator.getDomainData().getSeed()); //initial seed from properties file
        for (int i = 0; i < numberOfBenchmarks - 1; i++) {
            seedList.add((long) Math.abs(new Random().nextInt()));
        }
    }

    public void runBenchmark(String path) throws Exception {
        String resultPath = path + filePrefix;

        XYChart overviewChart = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        ResultWriter resultWriter = new ResultWriter(resultPath, indexType);

        BenchmarkDataContainer benchmarkDataContainer = new BenchmarkDataContainer(indexType,
                domainGenerator.getDomainData());

        List<PolicyGeneratorConfiguration> configs = generateConfigurations();

        for (PolicyGeneratorConfiguration config : configs) {

            List<XlsRecord> results = benchmarkConfiguration(path, benchmarkDataContainer, config);

            sanitizeResults(results);

            double[] times = new double[results.size()];
            resultWriter.writeDetailsChart(results, times, config.getName());
            overviewChart.addSeries(config.getName(), times);

            addResultsForConfigToContainer(benchmarkDataContainer, config, results);
        }

        resultWriter.writeFinalResults(benchmarkDataContainer, overviewChart);
    }

    private List<PolicyGeneratorConfiguration> generateConfigurations() {
        if (PERFORM_RANDOM_BENCHMARK) {
            return generateTestSuite(path).getCases();
        } else {
            List<PolicyGeneratorConfiguration> configurations = new LinkedList<>();

            for (Long seed : seedList) {

                configurations.add(PolicyGeneratorConfiguration.builder()
                        .name(String.format("Bench_%5d_%s", seed, indexType))
                        .path(domainGenerator.getDomainData().getPolicyDirectoryPath())
                        .build());
            }

            return configurations;
        }

    }

    @SneakyThrows
    private TestSuite generateTestSuite(String path) {
        TestSuite suite;
        if (!Strings.isNullOrEmpty(testFilePath)) {
            File testFile = new File(testFilePath);
            LOGGER.info("using testfile: {}", testFile);

            List<String> allLines = Files.readAllLines(Paths.get(testFile.toURI()));
            String allLinesAsString = StringUtils.join(allLines, "");

            suite = new Gson().fromJson(allLinesAsString, TestSuite.class);
        } else {
            suite = TestSuiteGenerator.generateN(path, numberOfBenchmarks, domainGenerator.getDomainData().getDice());
        }

        Objects.requireNonNull(suite, "test suite is null");
        Objects.requireNonNull(suite.getCases(), "test cases are null");
        LOGGER.info("suite contains {} test cases", suite.getCases().size());
        if (!suite.getCases().isEmpty()) throw new RuntimeException("at least one test case must be present");

        return suite;
    }

    private List<XlsRecord> benchmarkConfiguration(String path, BenchmarkDataContainer benchmarkDataContainer,
                                                   PolicyGeneratorConfiguration config) throws Exception {
        benchmarkDataContainer.getIdentifier().add(config.getName());
        List<XlsRecord> results = null;
        try {
            results = PERFORM_RANDOM_BENCHMARK
                    ? TEST_RUNNER.runTest(config, path, benchmarkDataContainer, domainGenerator)
                    : TEST_RUNNER
                    .runTestNew(config, config.getPath(), benchmarkDataContainer, domainGenerator);
        } catch (IOException | PolicyEvaluationException e) {
            LOGGER.error("Error running test", e);
            System.exit(1);
        }

        return results;
    }

    private void addResultsForConfigToContainer(BenchmarkDataContainer benchmarkDataContainer,
                                                PolicyGeneratorConfiguration config, List<XlsRecord> results) {
        benchmarkDataContainer.getMinValues().add(extractMin(results));
        benchmarkDataContainer.getMaxValues().add(extractMax(results));
        benchmarkDataContainer.getAvgValues().add(extractAvg(results));
        benchmarkDataContainer.getMdnValues().add(extractMdn(results));
        benchmarkDataContainer.getData().addAll(results);

        benchmarkDataContainer.getConfigs().add(config);
    }


    private void sanitizeResults(List<XlsRecord> results) {
        int numberOfDataToRemove = (int) (results.size() * REMOVE_EDGE_DATA_BY_PERCENTAGE);

        for (int i = 0; i < numberOfDataToRemove; i++) {
            results.stream().min(Comparator.comparingDouble(XlsRecord::getTimeDuration))
                    .ifPresent(results::remove);

            results.stream().max(Comparator.comparingDouble(XlsRecord::getTimeDuration))
                    .ifPresent(results::remove);
        }
    }

    private double extractMin(List<XlsRecord> data) {
        double min = Double.MAX_VALUE;
        for (XlsRecord item : data) {
            if (item.getTimeDuration() < min) {
                min = item.getTimeDuration();
            }
        }
        return min;
    }

    private double extractMax(List<XlsRecord> data) {
        double max = Double.MIN_VALUE;
        for (XlsRecord item : data) {
            if (item.getTimeDuration() > max) {
                max = item.getTimeDuration();
            }
        }
        return max;
    }

    private double extractAvg(List<XlsRecord> data) {
        double sum = 0;
        for (XlsRecord item : data) {
            sum += item.getTimeDuration();
        }
        return sum / data.size();
    }

    private double extractMdn(List<XlsRecord> data) {
        List<Double> list = data.stream().map(XlsRecord::getTimeDuration).sorted().collect(Collectors.toList());
        int index = list.size() / 2;
        if (list.size() % 2 == 0) {
            return (list.get(index) + list.get(index - 1)) / 2;
        } else {
            return list.get(index);
        }
    }

    private void parseCommandLineArguments(String... args) {
        Options options = new Options();

        options.addOption(PATH, true, PATH_DOC);
        options.addOption(HELP, false, HELP_DOC);
        options.addOption(INDEX, true, INDEX_DOC);
        options.addOption(TEST, true, TEST_DOC);
        options.addOption(ITERATIONS, true, ITERATIONS_DOC);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args, true);
            if (cmd.hasOption(HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(USAGE, options);
                System.exit(-1);
            }

            String pathOption = cmd.getOptionValue(PATH);
            if (!Strings.isNullOrEmpty(pathOption)) {
                if (!Files.exists(Paths.get(pathOption))) {
                    throw new IllegalArgumentException("path provided does not exists");
                }
                path = pathOption;
            }


            String indexOption = cmd.getOptionValue(INDEX);

            if (!Strings.isNullOrEmpty(indexOption)) {
                LOGGER.debug("using index {}", indexOption);
                switch (indexOption.toUpperCase()) {
                    case "FAST":
                        indexType = FAST;
                        break;
                    case "IMPROVED":
                        indexType = IMPROVED;
                        break;
                    case "SIMPLE":
                        indexType = SIMPLE;
                        break;
                    default:
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.printHelp(USAGE, options);
                        throw new IllegalArgumentException("invalid index option provided");
                }
            }

            String testOption = cmd.getOptionValue(TEST);
            if (!Strings.isNullOrEmpty(testOption)) {
                if (!Files.exists(Paths.get(testOption))) {
                    throw new IllegalArgumentException("test file provided does not exists");
                }
                testFilePath = testOption;
            }

            String iterOption = cmd.getOptionValue(ITERATIONS);
            if (!Strings.isNullOrEmpty(iterOption)) {
                this.numberOfBenchmarks = Integer.parseInt(iterOption);
            }

        } catch (ParseException e) {
            LOGGER.error("encountered an error running the demo: {}", e.getMessage(), e);
            System.exit(1);
        }

    }


}
