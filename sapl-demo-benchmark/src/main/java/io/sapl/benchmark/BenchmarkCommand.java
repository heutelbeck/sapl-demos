package io.sapl.benchmark;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Slf4j
@ToString
@Command(name = "sapl-demo-benchmark", version = "3.0.0-SNAPSHOT", mixinStandardHelpOptions = true, description = "Performs a benchmark on the PRP indexing data structures.")
public class BenchmarkCommand implements Callable<Integer> {

    private final LocalDateTime dateTime = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Option(names = {"-o", "--output"}, description = "Path to the output directory for benchmark results.")
    private String outputPath = "results/" + formatter.format(dateTime);

    @Option(names = {"-c", "--cfg"},  required = true, description = "YAML file to read json from")
    private String cfgFilePath;

    @Option(names = {"--skipBenchmark"})
    private Boolean skipBenchmark = Boolean.FALSE;

    @Option(names = {"--skipReportGeneration"})
    private Boolean skipReportGeneration = Boolean.FALSE;


    @Override
    public Integer call() throws Exception {
        log.info("Reading configuration from cfgFilePath={}", cfgFilePath);
        var benchmark = new SaplBenchmark(cfgFilePath, outputPath);
        if (!skipBenchmark) {
            log.info("Writing results to outputPath={}", outputPath);
            benchmark.executeBenchmark();
        }
        if (!skipReportGeneration) {
            log.info("Generating report in outputPath={}", outputPath);
            benchmark.generateBenchmarkReports();
        }
        return 0;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new BenchmarkCommand()).execute(args));
    }
}
