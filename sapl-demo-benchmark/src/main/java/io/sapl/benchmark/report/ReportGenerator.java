package io.sapl.benchmark.report;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
public class ReportGenerator {
    private static double round(double value) {
        double d = Math.pow(10, 2);
        return Math.round(value * d) / d;
    }

    private static java.util.List<String> getThroughputJsonFiles(String bechmarkFolder){
        // loop over files in the correct order - by thread number
        FilenameFilter filenameFilter = (d, s) -> s.matches("throughput_\\d+threads.json");
        var treadResultFiles = new File(bechmarkFolder).list(filenameFilter);
        return Arrays.stream(Optional.ofNullable(treadResultFiles).orElse(new String[0])).sorted(
                Comparator.comparing(s -> Integer.valueOf(s.replaceAll("\\w+_(\\d+)threads.json", "$1")))
        ).toList();
    }

    public static void generateThroughputBarChart(String bechmarkFolder, String outputFilename, String title,
                                                  List<Map<String, Object>> tableData) throws IOException{
        var chart = new BarChart(title, "ops/s");
        for ( var row: tableData) {
            chart.addBenchmarkResult((String) row.get("threads"), (String) row.get("pdpName"), round((Double) row.get("score")));
        }
        chart.showLabels(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#"), new DecimalFormat("#")));
        chart.useLogAxis();
        chart.saveToPNGFile( new File( bechmarkFolder + "/" + outputFilename ));
    }

    public static void generateResponsetimeBarChart(String bechmarkFolder, String outputFilename, String title,
                                                    List<Map<String, Object>> tableData) throws IOException{
        var chart = new BarChart(title, "ms/op");
        for ( var row: tableData) {
            chart.addBenchmarkResult((String) row.get("authName"), (String) row.get("pdpName"), round((Double) row.get("score")));
        }
        chart.showLabels();
        chart.useLogAxis();
        chart.saveToPNGFile( new File( bechmarkFolder + "/" + outputFilename ));
    }


    private static void createDetailLineChart(String bechmarkFolder, String filePath, String title, JsonArray rawData ) throws IOException {
        // add detailed graph
        var chart = new LineChart(title, "ops/s");
        int forkNumber = 1;
        for (JsonElement entry: rawData ){
            JsonArray forkResults = entry.getAsJsonArray();
            String fork = "fork " + forkNumber++;
            int iteration = 1;
            for (JsonElement e3 : forkResults) {
                chart.addValue(e3.getAsDouble(), fork, String.valueOf(iteration++));
            }
        }
        chart.saveToPNGFile( new File( bechmarkFolder + "/" + filePath ));
    }

    private static String getBenchmarkNameFromFqn(String methodFqn){
        String[] benchmarkNames = methodFqn.split("\\.");
        return benchmarkNames[benchmarkNames.length - 2] + "." + benchmarkNames[benchmarkNames.length - 1];
    }

    private static String getDecisionMethodFromBenchmarkName(String benchmarkName){
        String[] benchmarkNames = benchmarkName.split("\\.");
        String methodName = benchmarkNames[benchmarkNames.length-1];
        if ( methodName.endsWith("DecideOnce") ){
            return "Decide Once";
        } else if ( methodName.endsWith("DecideSubscribe") || methodName.endsWith("Decide")){
            return "Decide Subscribe";
        } else {
            throw new RuntimeException("Unable to determine DecisionMethod in " + methodName);
        }
    }

    private static String getAuthMethodFromBenchmarkName(String benchmarkName){
        String[] benchmarkNames = benchmarkName.split("\\.");
        String methodName = benchmarkNames[benchmarkNames.length-1];
        return methodName.replaceAll("Decide(Once|Subscribe)?$", "");
    }

    private static String getPdpFromBenchmarkName(String benchmarkName){
        String[] benchmarkNames = benchmarkName.split("\\.");
        return benchmarkNames[benchmarkNames.length-2].replace("Benchmark", "").toLowerCase();
    }

    private static Map<String, Object> getSummaryTableContext(String bechmarkFolder) throws IOException {
        List<String> header_facts = new ArrayList<>();
        Map<String, Map<String, Map<String, List<Object>>>> rowData = Maps.newHashMap();
        header_facts.add("avg ms/op");

        // get data from average_response
        JsonArray jsonContent = JsonParser.parseReader(new FileReader(bechmarkFolder+"/average_response.json")).getAsJsonArray();
        for (JsonElement e : jsonContent) {
            JsonObject runResult = e.getAsJsonObject();
            String benchmarkName = runResult.get("benchmark").getAsString();
            String pdp = getPdpFromBenchmarkName(benchmarkName);
            String decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
            String authMethod = getAuthMethodFromBenchmarkName(benchmarkName);
            var base_entry = rowData.computeIfAbsent(decisionMethod, __ -> Maps.newHashMap())
                                    .computeIfAbsent(authMethod, __ -> Maps.newHashMap())
                                    .computeIfAbsent(pdp, __ -> new ArrayList<>());
            base_entry.add(runResult.get("primaryMetric").getAsJsonObject().get("score").getAsDouble());
        }

        // get data from average_response
        for ( String filename: getThroughputJsonFiles(bechmarkFolder) ) {
            jsonContent = JsonParser.parseReader(new FileReader(bechmarkFolder + "/" + filename)).getAsJsonArray();
            String threads = filename.replaceAll("\\w+_(\\d+)threads.json", "$1-threads");
            header_facts.add("throughput ops/s<br>("+threads+")");
            for (JsonElement e : jsonContent) {
                JsonObject runResult = e.getAsJsonObject();
                String benchmarkName = runResult.get("benchmark").getAsString();
                String pdp = getPdpFromBenchmarkName(benchmarkName);
                String decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
                String authMethod = getAuthMethodFromBenchmarkName(benchmarkName);
                var base_entry = rowData.computeIfAbsent(decisionMethod, __ -> Maps.newHashMap())
                        .computeIfAbsent(authMethod, __ -> Maps.newHashMap())
                        .computeIfAbsent(pdp, __ -> new ArrayList<>());
                base_entry.add(runResult.get("primaryMetric").getAsJsonObject().get("score").getAsDouble());
            }
        }
        return Map.of("header_facts", header_facts, "row_data", rowData);
    }

    private static Map<String, Map<String, Object>> getResponseTimeContext(String bechmarkFolder) throws IOException {
        Map<String, List<Map<String, Object>>> baseData = Maps.newHashMap();

        JsonArray jsonContent = JsonParser.parseReader(new FileReader(bechmarkFolder + "/average_response.json")).getAsJsonArray();
        for (JsonElement e : jsonContent) {
            JsonObject runResult = e.getAsJsonObject();
            String benchmarkName = getBenchmarkNameFromFqn(runResult.get("benchmark").getAsString());
            String decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
            String section = decisionMethod + " - Average Response Time";

            // generate detail graph
            var chartFilePath = "img/" + benchmarkName + " response time.png";
            createDetailLineChart(bechmarkFolder, chartFilePath, benchmarkName + " - response time",
                    runResult.get("primaryMetric").getAsJsonObject().get("rawData").getAsJsonArray()
            );

            // add table entry
            var entry = baseData.computeIfAbsent(section, __ -> new ArrayList<>());
            entry.add(Map.of(
                    "benchmark", benchmarkName,
                    "authName", getAuthMethodFromBenchmarkName(benchmarkName),
                    "pdpName", getPdpFromBenchmarkName(benchmarkName),
                    "score", runResult.get("primaryMetric").getAsJsonObject().get("score").getAsDouble(),
                    "error", runResult.get("primaryMetric").getAsJsonObject().get("scoreError").getAsDouble(),
                    "pct_90", runResult.get("primaryMetric").getAsJsonObject().get("scorePercentiles").getAsJsonObject().get("90.0").getAsDouble(),
                    "pct_95", runResult.get("primaryMetric").getAsJsonObject().get("scorePercentiles").getAsJsonObject().get("95.0").getAsDouble(),
                    "pct_99", runResult.get("primaryMetric").getAsJsonObject().get("scorePercentiles").getAsJsonObject().get("99.0").getAsDouble(),
                    "chart", chartFilePath
            ));
        }

        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();
        for (String section: baseData.keySet()){
            String fileName = "img/" + section + ".png";
            generateResponsetimeBarChart(bechmarkFolder, fileName, section, baseData.get(section));
            resultMap.put(section, Map.of(
                    "chart", fileName,
                    "tableData", baseData.get(section))
            );
        }

        return resultMap;

    }

    private static Map<String, Map<String, Object>> getThroughputContext(String bechmarkFolder) throws IOException {
        Map<String, List<Map<String, Object>>> baseData = Maps.newHashMap();

        for ( String filename: getThroughputJsonFiles(bechmarkFolder) ) {
            JsonArray jsonContent = JsonParser.parseReader(new FileReader(bechmarkFolder + "/" + filename)).getAsJsonArray();
            String threads = filename.replaceAll("\\w+_(\\d+)threads.json", "$1-threads");
            for (JsonElement e : jsonContent) {
                JsonObject runResult = e.getAsJsonObject();
                String benchmarkName = getBenchmarkNameFromFqn(runResult.get("benchmark").getAsString());
                String decisionMethod = getDecisionMethodFromBenchmarkName(benchmarkName);
                String authMethod = getAuthMethodFromBenchmarkName(benchmarkName);
                String section = decisionMethod + " - " + authMethod + " - throughput";

                // generate detail
                var chartFilePath = "img/" + benchmarkName + " throughput.png";
                createDetailLineChart(bechmarkFolder, chartFilePath, benchmarkName + " - Throughput",
                        runResult.get("primaryMetric").getAsJsonObject().get("rawData").getAsJsonArray()
                );

                // add table entry
                var entry = baseData.computeIfAbsent(section, __ -> new ArrayList<>());
                entry.add(Map.of(
                        "benchmark", benchmarkName,
                        "pdpName", getPdpFromBenchmarkName(benchmarkName),
                        "threads", threads,
                        "score", runResult.get("primaryMetric").getAsJsonObject().get("score").getAsDouble(),
                        "error", runResult.get("primaryMetric").getAsJsonObject().get("scoreError").getAsDouble(),
                        "chart", chartFilePath
                ));
            }
        }

        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();
        for (String section: baseData.keySet()){
            String fileName = "img/" + section + ".png";
            generateThroughputBarChart(bechmarkFolder, fileName, section, baseData.get(section));
            resultMap.put(section, Map.of(
                    "chart", fileName,
                    "tableData", baseData.get(section))
            );
        }

        return resultMap;
    }

    public static void generateHTMLReport(String benchmarkFolder) throws IOException {
        Files.createDirectories(Paths.get(benchmarkFolder+"/img"));
        // build context
        Map<String, Object> context = Maps.newHashMap();
        context.put("SummaryTableData", getSummaryTableContext(benchmarkFolder));
        context.put("responseTimeData", getResponseTimeContext(benchmarkFolder));
        context.put("throughputData", getThroughputContext(benchmarkFolder));
        context.put("throughputJsonFiles", getThroughputJsonFiles(benchmarkFolder));

        // build context
        var jnj = new Jinjava();
        var inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream("Report.html");
        if (inputStream != null) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            String fileContent = jnj.render(template, context);
            var reportFilePath = benchmarkFolder + "/Report.html";
            log.info("generating report: {}", reportFilePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath));
            writer.write(fileContent);
            writer.close();
        }

        // copy static files
        for (String file: new String[]{"custom.css", "favicon.png"}) {
            inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(file);
            if (inputStream != null) {
                FileUtils.copyInputStreamToFile(inputStream, new File(benchmarkFolder + "/" + file));
            }
        }
    }
}