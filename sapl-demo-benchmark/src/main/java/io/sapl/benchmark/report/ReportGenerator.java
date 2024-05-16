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
package io.sapl.benchmark.report;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class ReportGenerator {
    static String chartField            = "chart";
    private final List<String> resultFiles;
    private final String benchmarkFolder;

    @StandardException
    public static class BenchmarkReportException extends RuntimeException {}

    public ReportGenerator(String benchmarkFolder) {
        this.benchmarkFolder = benchmarkFolder;
        this.resultFiles = getResultFilesFiles();

        if (resultFiles.isEmpty()){
            throw new BenchmarkReportException(
                    "No Result files found matching: results_*threads.json in " + benchmarkFolder);
        }
    }

    private static int getThreadCountFromFileName(String fileName) {
        return Integer.parseInt(fileName.replaceFirst("results_(\\d+)threads.json", "$1"));
    }

    /**
     * Provides the result files of the benchmark in the correct order.
     */
    private java.util.List<String> getResultFilesFiles() {
        FilenameFilter filenameFilter   = (d, s) -> s.matches("results_\\d+threads.json");
        var            treadResultFiles = new File(benchmarkFolder).list(filenameFilter);
        return Arrays.stream(Optional.ofNullable(treadResultFiles).orElse(new String[0]))
                .sorted(Comparator.comparing(ReportGenerator::getThreadCountFromFileName)).toList();
    }

    private static void generateThroughputBarChart(String benchmarkFolder, String outputFilename, String title,
                                                   Iterable<ReportSectionData> tableData) throws IOException {
        var dataset = new DefaultStatisticalCategoryDataset();
        for (var row : tableData) {
            dataset.add(
                    row.getThroughputAvg(),
                    row.getThroughputStdDev(),
                    row.getThreads() + "-threads",
                    row.getAuthMethod());
        }

        var chart = new BarChart(title, dataset, "ops/s (more is better)", "#");
        chart.saveToPNGFile(new File(benchmarkFolder + File.separator + outputFilename));
    }

    private static void generateResponseTimeBarChart(String benchmarkFolder, String outputFilename, String title,
                                                     Iterable<ReportSectionData> tableData) throws IOException {
        var dataset = new DefaultStatisticalCategoryDataset();
        for (var row : tableData) {
            dataset.add(
                    row.getResponseTimeAvg(),
                    row.getResponseTimeStdDev(),
                    row.getAuthMethod(),
                    row.getPdpName());
        }

        var chart = new BarChart(title, dataset, "ms/op (less is better)", "0.00");
        chart.saveToPNGFile(new File(benchmarkFolder + File.separator + outputFilename));
    }


    private static void createDetailLineChart(String benchmarkFolder, String filePath, String title,
                                              List<List<Double>> rawData) throws IOException {
        var dataset = new DefaultCategoryDataset();
        var forkNumber = 1;
        for (var forkResults : rawData) {
            var fork        = "fork " + forkNumber++;
            var iteration   = 1;
            for (var result: forkResults) {
                dataset.addValue(result, fork, String.valueOf(iteration++));
            }
        }

        var chart = new LineChart(title, dataset, "ops/s");
        chart.saveToPNGFile(new File( benchmarkFolder + File.separator + filePath));
    }



    private List<BenchmarkResult> getBenchmarkResultsFromFile(String fileName) throws IOException {
        JsonArray jsonContent = JsonParser
                .parseReader(new FileReader(benchmarkFolder + "/" + fileName, StandardCharsets.UTF_8))
                .getAsJsonArray();
        List<BenchmarkResult> benchmarkResults = new ArrayList<>();
        for (JsonElement jsonElement : jsonContent) {
            benchmarkResults.add(new BenchmarkResult(jsonElement));
        }
        return benchmarkResults;
    }

    private Map<String, Object> getSummaryTableContext() throws IOException {
        List<String>                                          headerFacts = new ArrayList<>(0);
        Map<String, Map<String, Map<String, List<Object>>>> tableData     = Maps.newHashMap();

        // use the first file for average response time comparison
        var averageResultFile = resultFiles.get(0);
        var threads = getThreadCountFromFileName(averageResultFile);
        headerFacts.add("avg ms/op<br>(" + threads + " thread)");

        // loop Over Benchmark results in the file
        for (BenchmarkResult benchmarkResult: getBenchmarkResultsFromFile(averageResultFile)){
            var l1 = benchmarkResult.getDecisionMethod();
            var l2 = benchmarkResult.getPdp();
            var l3 = benchmarkResult.getAuthMethod();

            var rowEntry = tableData
                    .computeIfAbsent(l1, y -> new HashMap<>())
                    .computeIfAbsent(l2, y ->  new HashMap<>())
                    .computeIfAbsent(l3, y ->  new ArrayList<>());
            rowEntry.add(benchmarkResult.getResponseTimeAvg());
        }

        // add data from throughput
        for (String filename : getResultFilesFiles()) {
            threads = getThreadCountFromFileName(filename);
            headerFacts.add("throughput ops/s<br>(" + threads + " threads)");

            // loop over Benchmark results in the file
            for (BenchmarkResult benchmarkResult: getBenchmarkResultsFromFile(filename)){
                var l1 = benchmarkResult.getDecisionMethod();
                var l2 = benchmarkResult.getPdp();
                var l3 = benchmarkResult.getAuthMethod();

                // create emty if not existing
                var baseEntry = tableData
                        .computeIfAbsent(l1, y -> new HashMap<>())
                        .computeIfAbsent(l2, y ->  new HashMap<>())
                        .computeIfAbsent(l3, y ->  new ArrayList<>());
                baseEntry.add(benchmarkResult.getThoughputAvg());
            }
        }
        return Map.of("table_header", headerFacts, "table_data", tableData);
    }


    private Map<String, Map<String, Object>> getResponseTimeContext() throws IOException {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        log.info("collecting response time data ...");
        // use the first file for response time reporting
        var fileName = getResultFilesFiles().get(0);
        if ( fileName != null ) {

            Map<String, List<ReportSectionData>> baseData = new HashMap<>();
            for (BenchmarkResult benchmarkResult: getBenchmarkResultsFromFile(fileName)){
                var benchmarkName  = benchmarkResult.getBenchmarkShortName();
                var l1 = benchmarkResult.getDecisionMethod();
                var threads = benchmarkResult.getThreads();
                var section = "Average Response Time" + " - " + l1 + " - " + threads + " thread(s)";

                // generate detail chart
                var chartTitle = benchmarkName + " - " + threads + " threads - average response time";
                var chartFilePath = "img/" + benchmarkName + "_" + threads + "_threads_rspt" + ".png";
                createDetailLineChart(benchmarkFolder, chartFilePath, chartTitle, benchmarkResult.getResponseTimeRawResults());

                // build entry
                var resultData = ReportSectionData.builder()
                        .benchmarkResult(benchmarkResult)
                        .chartFilePath(chartFilePath)
                        .build();

                // add entry to list of this section
                baseData.computeIfAbsent(section, y -> new ArrayList<>()).add(resultData);
            }

            // build map for html rendering
            for (var entry: baseData.entrySet()){
                var section = entry.getKey();
                var resultDataList = entry.getValue();
                String chartFileName = "img/" + section + ".png";
                generateResponseTimeBarChart(benchmarkFolder, chartFileName, section, resultDataList);
                resultMap.put(section,
                        Map.of(chartField, chartFileName,
                                "tableData", resultDataList.stream()
                                        .sorted(Comparator.comparing(ReportSectionData::getBenchmarkName)
                                                .thenComparing(ReportSectionData::getAuthMethod))
                                        .map(ReportSectionData::getMap)
                                        .toList()
                        )
                );
            }
        }
        return resultMap;
    }



    private Map<String, Map<String, Object>> getThroughputContext() throws IOException {
        Map<String, Map<String, Object>> resultMap = new HashMap<>(0);
        Map<String, List<ReportSectionData>> baseData = new HashMap<>(0);
        log.info("collecting throughput data ...");

        for (String fileName : getResultFilesFiles()) {
            for (BenchmarkResult benchmarkResult: getBenchmarkResultsFromFile(fileName)){
                var benchmarkName  = benchmarkResult.getBenchmarkShortName();
                var l1 = benchmarkResult.getDecisionMethod();
                var l2 = benchmarkResult.getPdp();
                var threads = benchmarkResult.getThreads();
                var section = "Throughput" + " - " + l1 + " - " + l2;

                // generate detail chart
                var chartTitle = benchmarkName + " - " + threads + " threads - throughput";
                var chartFilePath = "img/" + benchmarkName + "_" + threads + "_threads_thrpt" + ".png";
                createDetailLineChart(benchmarkFolder, chartFilePath, chartTitle, benchmarkResult.getThroughputRawResults());

                // build entry
                var resultData = ReportSectionData.builder()
                        .benchmarkResult(benchmarkResult)
                        .chartFilePath(chartFilePath)
                        .build();

                // add entry to list of this section
                baseData.computeIfAbsent(section, y -> new ArrayList<>()).add(resultData);
            }
        }

        for (var entry: baseData.entrySet()){
            var section = entry.getKey();
            var chartFileName = "img/" + section + ".png";
            var resultDataList = entry.getValue();
            generateThroughputBarChart(benchmarkFolder, chartFileName, section, resultDataList);
            resultMap.put(section,
                    Map.of(chartField, chartFileName,
                    "tableData", resultDataList.stream()
                                    .sorted(Comparator.comparing(ReportSectionData::getBenchmarkName)
                                            .thenComparing(ReportSectionData::getThreads))
                                    .map(ReportSectionData::getMap)
                                    .toList()
                    )
            );
        }

        return resultMap;
    }

    private void loadStaticFilesIntoReport() throws IOException {
        // copy static files
        for (var file : new String[] { "custom.css", "favicon.png" }) {
            var inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(file);
            if (inputStream != null) {
                FileUtils.copyInputStreamToFile(inputStream, new File(benchmarkFolder + File.separator + file));
            }
        }
    }

    public void generateReport() throws IOException {
        Files.createDirectories(Paths.get(benchmarkFolder + "/img"));

        // build context
        Map<String, Object> context = Maps.newHashMap();
        log.info("generating data for summary Table");
        context.put("summaryTableData", getSummaryTableContext());
        context.put("responseTimeData", getResponseTimeContext());
        context.put("throughputData", getThroughputContext());
        context.put("resultFiles", getResultFilesFiles());

        // build context
        var jnj         = new Jinjava();
        var inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream("Report.html");
        if (inputStream != null) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            String fileContent    = jnj.render(template, context);
            var    reportFilePath = benchmarkFolder + "/Report.html";
            log.info("generating report: {}", reportFilePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath, StandardCharsets.UTF_8));
            writer.write(fileContent);
            writer.close();
        }

        // load static files into template
        loadStaticFilesIntoReport();
    }
}
