package io.sapl.benchmark.jmh;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.benchmark.util.EchoPIP;
import io.sapl.interpreter.InitializationException;
import io.sapl.pdp.PolicyDecisionPointFactory;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.List;

import static io.sapl.benchmark.jmh.Helper.decide;
import static io.sapl.benchmark.jmh.Helper.decideOnce;


@Slf4j
@State(Scope.Benchmark)
public class EmbeddedBenchmark {

    @Param({"{}"})
    @SuppressWarnings("unused")
    String contextJsonString;
    private PolicyDecisionPoint pdp;
    private BenchmarkExecutionContext context;

    @Setup(Level.Trial)
    public void setup() throws InitializationException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing embedded PDP");
        pdp = PolicyDecisionPointFactory.resourcesPolicyDecisionPoint(List::of, () -> List.of(EchoPIP.class),
                List::of, List::of);
    }

    @Benchmark
    public void NoAuthDecideSubscribe() {
        decide(pdp, context.authorizationSubscription);
    }

    @Benchmark
    public void NoAuthDecideOnce() {
        decideOnce(pdp, context.authorizationSubscription);
    }
}
