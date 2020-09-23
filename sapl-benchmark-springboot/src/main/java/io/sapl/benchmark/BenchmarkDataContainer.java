package io.sapl.benchmark;

import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class BenchmarkDataContainer {

    final String benchmarkId;
    final long benchmarkTimestamp;
    final String runtimeInfo;

    final IndexType indexType;
    final boolean newPoliciesGenerated;
    final int iterations;
    final int runs;
    final String comparisonId;

    List<XlsRecord> data = new LinkedList<>();
    List<Double> minValues = new LinkedList<>();
    List<Double> maxValues = new LinkedList<>();
    List<Double> avgValues = new LinkedList<>();
    List<Double> mdnValues = new LinkedList<>();
    List<String> identifier = new LinkedList<>();

    List<XlsAggregateRecord> aggregateData = new LinkedList<>();

    public BenchmarkDataContainer(IndexType indexType, boolean reuseExistingPolicies, int iterations, int runs,
                                  String comparisonId) {
        this.benchmarkId = UUID.randomUUID().toString();
        this.benchmarkTimestamp = System.currentTimeMillis();
        this.runtimeInfo = String.format("%s_%s", System.getProperty("java.vendor"),
                System.getProperty("java.version"));
        this.indexType = indexType;
        this.newPoliciesGenerated = reuseExistingPolicies;
        this.iterations = iterations;
        this.runs = runs;
        this.comparisonId = comparisonId;

    }
}
