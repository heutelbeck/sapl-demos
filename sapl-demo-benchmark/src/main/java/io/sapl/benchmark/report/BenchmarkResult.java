package io.sapl.benchmark.report;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import io.sapl.benchmark.util.BenchmarkException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class BenchmarkResult {
    private final JsonObject benchmarkResult;

    @Getter
    private final String benchmarkFullName;
    @Getter
    private final String pdp;
    @Getter
    private final String decisionMethod;
    @Getter
    private final String authMethod;
    @Getter
    private final int threads;
    @Getter
    private final Integer measureTimeInSeconds;

    BenchmarkResult(JsonElement resultJsonElement){
        this.benchmarkResult = resultJsonElement.getAsJsonObject();
        this.benchmarkFullName = benchmarkResult.get("benchmark").getAsString();
        this.threads = benchmarkResult.get("threads").getAsInt();
        this.pdp = getPdpFromBenchmarkName(this.benchmarkFullName);
        this.decisionMethod = getDecisionMethodFromBenchmarkName(this.benchmarkFullName);
        this.authMethod = getAuthMethodFromBenchmarkName(this.benchmarkFullName);
        this.measureTimeInSeconds = getThroughputInSeconds();
    }

    private static String getPdpFromBenchmarkName(String benchmarkName) {
        String[] benchmarkNames = benchmarkName.split("\\.");
        return benchmarkNames[benchmarkNames.length - 2]
                .replaceAll("Benchmark$", "")
                .toLowerCase();
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

    private Integer getThroughputInSeconds(){
        var measureTimeStr = benchmarkResult.get("measurementTime").getAsString();
        return Integer.valueOf(measureTimeStr.replaceAll(" s$", ""));
    }

    private Double throughputToResponseTimeInMs(Double throughput){
        return 1 / throughput / threads  * 1000;
    }

    public Double getResponseTimeAvg(){
        return throughputToResponseTimeInMs(getThoughputAvg());
    }


    public Double getThoughputAvg(){
        return benchmarkResult.get("primaryMetric")
                .getAsJsonObject()
                .get("score")
                .getAsDouble();
    }

   public ArrayList<ArrayList<Double>> getThroughputRawResults(){
        ArrayList<ArrayList<Double>> resultArray = new ArrayList<>();
        var jsonRawData = benchmarkResult.get("primaryMetric").getAsJsonObject().get("rawData").getAsJsonArray();
        for (JsonElement forkData: jsonRawData) {
            ArrayList<Double> forkResultArray = new ArrayList<>();
            for (JsonElement entry : forkData.getAsJsonArray()) {
                forkResultArray.add(entry.getAsDouble());
            }
            resultArray.add(forkResultArray);
        }
        return resultArray;
    }

    public ArrayList<ArrayList<Double>> getResponseTimeRawResults(){
        ArrayList<ArrayList<Double>> resultArray = new ArrayList<>();
        var jsonRawData = benchmarkResult.get("primaryMetric").getAsJsonObject().get("rawData").getAsJsonArray();
        for (JsonElement forkData: jsonRawData) {
            ArrayList<Double> forkResultArray = new ArrayList<>();
            for (JsonElement entry : forkData.getAsJsonArray()) {
                forkResultArray.add(throughputToResponseTimeInMs(entry.getAsDouble()));
            }
            resultArray.add(forkResultArray);
        }
        return resultArray;
    }

    public ArrayList<Double> getThroughputAllRawResults(){
        ArrayList<Double> resultArray = new ArrayList<>();
        for (var forkData: getThroughputRawResults()) {
            resultArray.addAll(forkData);
        }
        return resultArray;
    }

    public ArrayList<Double> getResponseTimeAllRawResults(){
        ArrayList<Double> resultArray = new ArrayList<>();
        for (var forkData: getResponseTimeRawResults()) {
            resultArray.addAll(forkData);
        }
        return resultArray;
    }

    public Double getThoughputStdDev(){
        var rawResults = getThroughputAllRawResults();
        return new StandardDeviation().evaluate(rawResults.stream().mapToDouble(d -> d).toArray());
    }

    public Double getResponseTimeStdDev(){
        var rawResults = getResponseTimeAllRawResults();
        return new StandardDeviation().evaluate(rawResults.stream().mapToDouble(d -> d).toArray());
    }

    public Double getResponseTimeMin(){
        return getResponseTimeAllRawResults().stream().mapToDouble(d->d).min().getAsDouble();
    }

    public Double getResponseTimeMax(){
        return getResponseTimeAllRawResults().stream().mapToDouble(d->d).max().getAsDouble();
    }

    public String getBenchmarkShortName(){
        var benchmarkNameElements = this.benchmarkFullName.split("\\.");
        return Arrays.stream(benchmarkNameElements)
                .skip(benchmarkNameElements.length - 2)
                .map(String::valueOf)
                .collect(Collectors.joining("."));
    }

}
