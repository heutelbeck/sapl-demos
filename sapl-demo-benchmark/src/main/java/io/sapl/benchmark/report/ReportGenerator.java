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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;

import io.sapl.benchmark.util.BenchmarkException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportGenerator {
    static String pdpNameField          = "pdpName";
    static String scoreField            = "score";
    static String benchmarkField        = "benchmark";
    static String primaryMetricField    = "primaryMetric";
    static String scorePercentilesField = "scorePercentiles";
    static String chartField            = "chart";

    private ReportGenerator() {
        throw new IllegalStateException("Utility class");
    }

    private static double round(double value) {
        double d = Math.pow(10, 2);
        return Math.round(value * d) / d;
    }

    private static int getThreadCountFromFileName(String fileName) {
        return Integer.parseInt(fileName.replaceFirst("throughput_(\\d+)threads.json", "$1"));
    }

    private static java.util.List<String> getThroughputJsonFiles(String bechmarkFolder) {
        // loop over files in the correct order - by thread number
        FilenameFilter filenameFilter   = (d, s) -> s.matches("throughput_\\d+threads.json");
        var            treadResultFiles = new File(bechmarkFolder).list(filenameFilter);
        return Arrays.stream(Optional.ofNullable(treadResultFiles).orElse(new String[0]))
                .sorted(Comparator.comparing(ReportGenerator::getThreadCountFromFileName)).toList();
    }

    public static void generateThroughputBarChart(String bechmarkFolder, String outputFilename, String title,
            Iterable<Map<String, Object>> tableData) throws IOException {
        var chart = new BarChart(title, "ops/s");
        for (var row : tableData) {
            chart.addBenchmarkResult((String) row.get("threads"), (String) row.get(pdpNameField),
                    round((Double) row.get(scoreField)));
        }
        chart.showLabels(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#"), new DecimalFormat("#")));
        chart.useLogAxis();
        chart.saveToPNGFile(new File(bechmarkFolder + File.separator + outputFilename));
    }

    public static void generateResponsetimeBarChart(String bechmarkFolder, String outputFilename, String title,
            Iterable<Map<String, Object>> tableData) throws IOException {
        var chart = new BarChart(title, "ms/op");
        for (var row : tableData) {
            chart.addBenchmarkResult((String) row.get("authName"), (String) row.get(pdpNameField),
                    round((Double) row.get(scoreField)));
        }
        chart.showLabels();
        chart.useLogAxis();
        chart.saveToPNGFile(new File(bechmarkFolder + File.separator + outputFilename));
    }

    private static void createDetailLineChart(String bechmarkFolder, String filePath, String title, JsonArray rawData)
            throws IOException {
        // add detailed graph
        var chart      = new LineChart(title, "ops/s");
        int forkNumber = 1;
        for (JsonElement entry : rawData) {
            JsonArray forkResults = entry.getAsJsonArray();
            String    fork        = "fork " + forkNumber++;
            int       iteration   = 1;
            for (JsonElement e3 : forkResults) {
                chart.addValue(e3.getAsDouble(), fork, String.valueOf(iteration++));
            }
        }
        chart.saveToPNGFile(new File(bechmarkFolder + File.separator + filePath));
    }

    private static String getBenchmarkNameFromFqn(String methodFqn) {
        String[] benchmarkNames = methodFqn.split("\\.");
        return benchmarkNames[benchmarkNames.length - 2] + "." + benchmarkNames[benchmarkNames.length - 1];
    }

    private static String getDecisionMethodFromBenchmarkName(String benchmarkName) {
        String[] benchmarkNames = benchmarkName.split("\\.");
        String   methodName     = benchmarkNames[benchmarkNames.length - 1];
        if (methodName.endsWith("DecideOnce")) {
            return "Decide Once";
        } else if (methodName.endsWith("DecideSubscribe") || methodName.endsWith("Decide")) {
            return "Decide Subscribe";
        } else {
            throw new BenchmarkException("Unable to determine DecisionMethod in " + methodName);
        }
    }

    private static String getAuthMethodFromBenchmarkName(String benchmarkName) {
        String[] benchmarkNames = benchmarkName.split("\\.");
        String   methodName     = benchmarkNames[benchmarkNames.length - 1];
        return methodName.replaceAll("Decide(Once|Subscribe)?$", "");
    }

    private static String getPdpFromBenchmarkName(String benchmarkName) {
        String[] benchmarkNames = benchmarkName.split("\\.");
        return benchmarkNames[benchmarkNames.length - 2].replace(benchmarkField, "").toLowerCase();
    }

    private static Map<String, Object> getSummaryTableContext(String bechmarkFolder) throws IOException {
        List<String>                                        headerFacts = new ArrayList<>();
        Map<String, Map<String, Map<String, List<Object>>>> rowData     = Maps.newHashMap();
        headerFacts.add("avg ms/op");

        // get data from average_response
        JsonArray jsonContent = JsonParser
                .parseReader(new FileReader(bechmarkFolder + "/average_response.json", StandardCharsets.UTF_8))
                .getAsJsonArray();
        for (JsonElement e : jsonContent) {
            JsonObject runResult      = e.getAsJsonObject();
            String     benchmarkName  = runResult.get(benchmarkField).getAsString();
            String     pdp            = getPdpFromBenchmarkName(benchmarkName);
            String     decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
            String     authMethod     = getAuthMethodFromBenchmarkName(benchmarkName);
            var        baseEntry      = rowData.computeIfAbsent(decisionMethod, xY -> Maps.newHashMap())
                    .computeIfAbsent(authMethod, xY -> Maps.newHashMap()).computeIfAbsent(pdp, xY -> new ArrayList<>());
            baseEntry.add(runResult.get(primaryMetricField).getAsJsonObject().get(scoreField).getAsDouble());
        }

        // get data from average_response
        for (String filename : getThroughputJsonFiles(bechmarkFolder)) {
            jsonContent = JsonParser
                    .parseReader(new FileReader(bechmarkFolder + File.separator + filename, StandardCharsets.UTF_8))
                    .getAsJsonArray();
            String threads = getThreadCountFromFileName(filename) + "-threads";
            headerFacts.add("throughput ops/s<br>(" + threads + ")");
            for (JsonElement e : jsonContent) {
                JsonObject runResult      = e.getAsJsonObject();
                String     benchmarkName  = runResult.get(benchmarkField).getAsString();
                String     pdp            = getPdpFromBenchmarkName(benchmarkName);
                String     decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
                String     authMethod     = getAuthMethodFromBenchmarkName(benchmarkName);
                var        baseEntry      = rowData.computeIfAbsent(decisionMethod, xY -> new HashMap<>(1))
                        .computeIfAbsent(authMethod, xY -> new HashMap<>(1))
                        .computeIfAbsent(pdp, xY -> new ArrayList<>(1));
                baseEntry.add(runResult.get(primaryMetricField).getAsJsonObject().get(scoreField).getAsDouble());
            }
        }
        return Map.of("header_facts", headerFacts, "row_data", rowData);
    }

    private static Map<String, Map<String, Object>> getResponseTimeContext(String bechmarkFolder) throws IOException {
        Map<String, List<Map<String, Object>>> baseData = new HashMap<>(1);

        JsonArray jsonContent = JsonParser
                .parseReader(new FileReader(bechmarkFolder + "/average_response.json", StandardCharsets.UTF_8))
                .getAsJsonArray();
        for (JsonElement e : jsonContent) {
            JsonObject runResult      = e.getAsJsonObject();
            String     benchmarkName  = getBenchmarkNameFromFqn(runResult.get(benchmarkField).getAsString());
            String     decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
            String     section        = decisionMethod + " - Average Response Time";

            // generate detail graph
            var chartFilePath = "img/" + benchmarkName + " response time.png";
            createDetailLineChart(bechmarkFolder, chartFilePath, benchmarkName + " - response time",
                    runResult.get(primaryMetricField).getAsJsonObject().get("rawData").getAsJsonArray());

            // add table entry
            var entry = baseData.computeIfAbsent(section, xY -> new ArrayList<>());
            entry.add(Map.of(benchmarkField, benchmarkName, "authName", getAuthMethodFromBenchmarkName(benchmarkName),
                    pdpNameField, getPdpFromBenchmarkName(benchmarkName), scoreField,
                    runResult.get(primaryMetricField).getAsJsonObject().get(scoreField).getAsDouble(), "error",
                    runResult.get(primaryMetricField).getAsJsonObject().get("scoreError").getAsDouble(), "pct_90",
                    runResult.get(primaryMetricField).getAsJsonObject().get(scorePercentilesField).getAsJsonObject()
                            .get("90.0").getAsDouble(),
                    "pct_95",
                    runResult.get(primaryMetricField).getAsJsonObject().get(scorePercentilesField).getAsJsonObject()
                            .get("95.0").getAsDouble(),
                    "pct_99", runResult.get(primaryMetricField).getAsJsonObject().get(scorePercentilesField)
                            .getAsJsonObject().get("99.0").getAsDouble(),
                    chartField, chartFilePath));
        }

        Map<String, Map<String, Object>> resultMap = new HashMap<>(1);
        for (Map.Entry<String, List<Map<String, Object>>> entry : baseData.entrySet()) {
            String section  = entry.getKey();
            String fileName = "img/" + section + ".png";
            generateResponsetimeBarChart(bechmarkFolder, fileName, section, baseData.get(section));
            resultMap.put(section, Map.of(chartField, fileName, "tableData", baseData.get(section)));
        }

        return resultMap;
    }

    private static Map<String, Map<String, Object>> getThroughputContext(String bechmarkFolder) throws IOException {
        Map<String, List<Map<String, Object>>> baseData = new HashMap<>(1);

        for (String filename : getThroughputJsonFiles(bechmarkFolder)) {
            JsonArray jsonContent = JsonParser
                    .parseReader(new FileReader(bechmarkFolder + File.separator + filename, StandardCharsets.UTF_8))
                    .getAsJsonArray();
            String    threads     = getThreadCountFromFileName(filename) + "-threads";
            for (JsonElement e : jsonContent) {
                JsonObject runResult      = e.getAsJsonObject();
                String     benchmarkName  = getBenchmarkNameFromFqn(runResult.get(benchmarkField).getAsString());
                String     decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
                String     authMethod     = getAuthMethodFromBenchmarkName(benchmarkName);
                String     section        = decisionMethod + " - " + authMethod + " - throughput";

                // generate detail
                var chartFilePath = "img/" + benchmarkName + " throughput.png";
                createDetailLineChart(bechmarkFolder, chartFilePath, benchmarkName + " - Throughput",
                        runResult.get(primaryMetricField).getAsJsonObject().get("rawData").getAsJsonArray());

                // add table entry
                var entry = baseData.computeIfAbsent(section, xY -> new ArrayList<>());
                entry.add(Map.of(benchmarkField, benchmarkName, pdpNameField, getPdpFromBenchmarkName(benchmarkName),
                        "threads", threads, scoreField,
                        runResult.get(primaryMetricField).getAsJsonObject().get(scoreField).getAsDouble(), "error",
                        runResult.get(primaryMetricField).getAsJsonObject().get("scoreError").getAsDouble(), chartField,
                        chartFilePath));
            }
        }

        Map<String, Map<String, Object>> resultMap = new HashMap<>(1);
        for (Map.Entry<String, List<Map<String, Object>>> entry : baseData.entrySet()) {
            String section  = entry.getKey();
            String fileName = "img/" + section + ".png";
            generateThroughputBarChart(bechmarkFolder, fileName, section, baseData.get(section));
            resultMap.put(section, Map.of(chartField, fileName, "tableData", baseData.get(section)));
        }

        return resultMap;
    }

    public static void generateHTMLReport(String benchmarkFolder) throws IOException {
        Files.createDirectories(Paths.get(benchmarkFolder + "/img"));
        // build context
        Map<String, Object> context = Maps.newHashMap();
        context.put("SummaryTableData", getSummaryTableContext(benchmarkFolder));
        context.put("responseTimeData", getResponseTimeContext(benchmarkFolder));
        context.put("throughputData", getThroughputContext(benchmarkFolder));
        context.put("throughputJsonFiles", getThroughputJsonFiles(benchmarkFolder));

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

        // copy static files
        for (String file : new String[] { "custom.css", "favicon.png" }) {
            inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(file);
            if (inputStream != null) {
                FileUtils.copyInputStreamToFile(inputStream, new File(benchmarkFolder + File.separator + file));
            }
        }
    }
}
