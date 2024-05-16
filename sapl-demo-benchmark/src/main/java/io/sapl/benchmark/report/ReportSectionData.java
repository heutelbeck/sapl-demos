package io.sapl.benchmark.report;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ReportSectionData {
    private String chartFilePath;
    private BenchmarkResult benchmarkResult;

    public String getBenchmarkName() {
        return benchmarkResult.getBenchmarkShortName();
    }

    public String getPdpName() {
        return benchmarkResult.getPdp();
    }

    public String getAuthMethod() {
        return benchmarkResult.getAuthMethod();
    }

    public Integer getThreads() {
        return benchmarkResult.getThreads();
    }

    public Double getThroughputAvg(){
        return benchmarkResult.getThoughputAvg();
    }

    public Double getThroughputStdDev(){
        return benchmarkResult.getThoughputStdDev();
    }

    public Double getResponseTimeAvg(){
        return benchmarkResult.getResponseTimeAvg();
    }

    public Double getResponseTimeStdDev(){
        return benchmarkResult.getResponseTimeStdDev();
    }

    public Map<String, Object> getMap(){
        return new HashMap<>() {{
            put("benchmark", getBenchmarkName());
            put("pdpName", getPdpName());
            put("threads", getThreads());
            put("thrpt", getThroughputAvg());
            put("thrpt_stddev", getThroughputStdDev());
            put("rspt", getResponseTimeAvg());
            put("rspt_stddev", getResponseTimeStdDev());
            put("rspt_min", benchmarkResult.getResponseTimeMin());
            put("rspt_max", benchmarkResult.getResponseTimeMax());
            put("chart", chartFilePath);
        }};
    }
}