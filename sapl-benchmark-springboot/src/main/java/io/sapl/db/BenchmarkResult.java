package io.sapl.db;

import io.sapl.benchmark.BenchmarkDataContainer;
import io.sapl.benchmark.XlsAggregateRecord;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;


@AllArgsConstructor
@RequiredArgsConstructor
public class BenchmarkResult {

    @Id
    final String id;
    final String benchmarkId;
    final long benchmarkTimestamp;
    final String runtimeInfo;
    String comparisonId;

    final String indexType;
    final boolean newPoliciesGenerated;
    final int runs;

    /* Test Results */
    String name;

    double min;

    double max;

    double avg;

    double mdn;

    public BenchmarkResult(BenchmarkDataContainer benchmarkDataContainer,
                           XlsAggregateRecord aggregateRecord) {
        this.id = UUID.randomUUID().toString();
        this.benchmarkId = benchmarkDataContainer.getBenchmarkId();
        this.benchmarkTimestamp = benchmarkDataContainer.getBenchmarkTimestamp();
        this.indexType = benchmarkDataContainer.getIndexType().toString();
        this.runtimeInfo = benchmarkDataContainer.getRuntimeInfo();
        this.newPoliciesGenerated = benchmarkDataContainer.isNewPoliciesGenerated();
        this.runs = benchmarkDataContainer.getRuns();
        this.comparisonId = benchmarkDataContainer.getComparisonId();

        this.name = aggregateRecord.getName();
        this.min = aggregateRecord.getMin();
        this.max = aggregateRecord.getMax();
        this.avg = aggregateRecord.getAvg();
        this.mdn = aggregateRecord.getMdn();

    }
}
