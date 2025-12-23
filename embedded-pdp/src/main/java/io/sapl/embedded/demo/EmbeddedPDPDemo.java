/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.embedded.demo;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.pdp.PolicyDecisionPointBuilder;
import io.sapl.pdp.PolicyDecisionPointBuilder.PDPComponents;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This demo shows how to manually construct a PDP without infrastructure
 * support. A Custom Policy Information Point and Function Library are bound to
 * the PDP. The demo runs a few performance tests and illustrates different ways
 * of invoking the PDP.
 */
@Command(name = "sapl-demo-embedded", version = "4.0.0-SNAPSHOT", mixinStandardHelpOptions = true,
        description = "This demo shows how to manually construct a PDP without infrastructure support. "
                + "A Custom Policy Information Point and Function Library are bound to the PDP. "
                + "The demo runs a few performance tests and illustrates different ways of invoking the PDP.")
public class EmbeddedPDPDemo implements Callable<Integer> {

    private static final String LINE = "------------------------------------------------------------------------";

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPDPDemo.class);

    @Option(names = { "-p", "--path" },
            description = "Sets the path for looking up policies and PDP configuration if the -f parameter is set. Defaults to '~/sapl/policies'")
    String path = "~/sapl/policies";

    @Option(names = { "-f", "--filesystem" },
            description = "If set, policies and PDP configuration are loaded from the filesystem instead of the bundled resources. Set path with -p.")
    boolean filesystem;

    private static final String SUBJECT = "willi";

    private static final String ACTION_READ = "read";

    private static final String ACTION_WRITE = "write";

    private static final String RESOURCE = "something";

    private static final AuthorizationSubscription READ_SUBSCRIPTION  = AuthorizationSubscription.of(SUBJECT,
            ACTION_READ, RESOURCE);
    private static final AuthorizationSubscription WRITE_SUBSCRIPTION = AuthorizationSubscription.of(SUBJECT,
            ACTION_WRITE, RESOURCE);

    private static final int DEMO_RUNS = 20_000;

    private static final int TEST_RUNS = 20;

    private static final double BILLION = 1_000_000_000.0D;

    private static final double MILLION = 1_000_000.0D;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    private static boolean useTestRuns = false;

    public static void main(String... args) {
        System.exit(new CommandLine(new EmbeddedPDPDemo()).execute(args));
    }

    static void setUseTestRuns(boolean useTestRuns) {
        EmbeddedPDPDemo.useTestRuns = useTestRuns;
    }

    private static int getRuns() {
        return useTestRuns ? TEST_RUNS : DEMO_RUNS;
    }

    @Override
    public Integer call() throws Exception {
        var components = buildPdpComponents();
        var pdp        = components.pdp();

        blockingUsageDemo(pdp);
        reactiveUsageDemo(pdp);
        runPerformanceDemoSingleBlocking(pdp);
        runPerformanceDemoSingleSequentialReactive(pdp);

        LOGGER.info("End of demo.");
        components.dispose();
        return 0;
    }

    private PDPComponents buildPdpComponents() {
        var builder = PolicyDecisionPointBuilder.withDefaults()
                .withPolicyInformationPoint(new EchoPIP())
                .withFunctionLibrary(SimpleFunctionLibrary.class);

        if (filesystem) {
            /*
             * withDirectorySource() creates a PDP that retrieves policies and configuration
             * from the file system.
             *
             * The PDP will monitor the path at runtime for any changes made to the policies
             * and update any subscribed PEPs accordingly.
             */
            builder.withDirectorySource(Path.of(path));
        } else {
            /*
             * withResourcesSource() creates a PDP that retrieves policies from the
             * resources bundled with the application.
             *
             * In a typical project structure, policies are located in the folder
             * 'src/main/resources/policies'.
             */
            builder.withResourcesSource();
        }

        return builder.build();
    }

    /**
     * If traditional blocking behavior is required, use .blockFirst(). This is not
     * applicable in multithreaded environments, e.g., web applications. The Reactor
     * runtime will likely complain that this behavior is not permitted.
     */
    private static void blockingUsageDemo(PolicyDecisionPoint pdp) {
        LOGGER.info("");
        LOGGER.info("Demo Part 1: Accessing the PDP in a blocking manner using .blockFirst()");
        var readDecision = pdp.decide(READ_SUBSCRIPTION).blockFirst();
        LOGGER.info("Decision for action 'read' : {}", readDecision != null ? readDecision.decision() : "null");
        var writeDecision = pdp.decide(WRITE_SUBSCRIPTION).blockFirst();
        LOGGER.info("Decision for action 'write': {}", writeDecision != null ? writeDecision.decision() : "null");
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    /**
     * If only one result is required, the appropriate way to consume exactly one
     * decision event is to use .take(1) and subscribe accordingly. In this demo
     * these will be processed sequentially, as this application is not declaring
     * schedulers.
     */
    private static void reactiveUsageDemo(PolicyDecisionPoint pdp) {
        LOGGER.info("");
        LOGGER.info("Demo Part 2: Accessing the PDP in a reactive manner using .take(1).subscribe()");

        LOGGER.info("Single reactive decision by using .take(1) and .subscribe()...");
        pdp.decide(READ_SUBSCRIPTION).take(1)
                .subscribe(authzDecision -> handleAuthorizationDecision(ACTION_READ, authzDecision));
        pdp.decide(WRITE_SUBSCRIPTION).take(1)
                .subscribe(authzDecision -> handleAuthorizationDecision(ACTION_WRITE, authzDecision));
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    private static void handleAuthorizationDecision(String action, AuthorizationDecision authzDecision) {
        LOGGER.info("Decision for action '{}': {}", action, authzDecision.decision());
    }

    private static void runPerformanceDemoSingleBlocking(PolicyDecisionPoint pdp) {
        var runs = getRuns();
        LOGGER.info("");
        LOGGER.info("Demo Part 3: Perform a small benchmark for blocking decisions.");

        LOGGER.info("Warming up for {} runs...", runs);
        for (var i = 0; i < runs; i++) {
            pdp.decide(READ_SUBSCRIPTION).blockFirst();
        }
        LOGGER.info("Measure time for {} runs...", runs);
        var start = System.nanoTime();
        for (var i = 0; i < runs; i++) {
            pdp.decide(READ_SUBSCRIPTION).blockFirst();
        }
        var end = System.nanoTime();
        LOGGER.info("");
        logResults("Benchmark results for blocking PDP access:", runs, start, end);
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    private static void runPerformanceDemoSingleSequentialReactive(PolicyDecisionPoint pdp) {
        var runs = getRuns();
        LOGGER.info("");
        LOGGER.info("Demo Part 4: Perform a small benchmark for sequential .take(1) decisions.");

        LOGGER.info("Warming up for {} runs...", runs);
        for (var i = 0; i < runs; i++) {
            pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
        }
        LOGGER.info("Measure time for {} runs...", runs);

        var start = System.nanoTime();
        for (var i = 0; i < runs; i++) {
            pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
        }
        var end = System.nanoTime();
        LOGGER.info("");
        logResults("Benchmark results for .take(1) access:", runs, start, end);
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    private static double nanoToMs(double nanoseconds) {
        return nanoseconds / MILLION;
    }

    private static double nanoToS(double nanoseconds) {
        return nanoseconds / BILLION;
    }

    private static void logResults(String title, int runs, long start, long end) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(title);
            LOGGER.info("Runs  : {}", runs);
            LOGGER.info("Total : {} s", DECIMAL_FORMAT.format(nanoToS((double) end - start)));
            LOGGER.info("Avg.  : {} ms", DECIMAL_FORMAT.format(nanoToMs(((double) end - start) / runs)));
        }
    }

}
