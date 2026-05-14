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

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.pdp.BlockingPolicyDecisionPoint;
import io.sapl.pdp.PDPComponents;
import io.sapl.pdp.PolicyDecisionPointBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

/**
 * This demo shows how to manually construct a PDP without infrastructure
 * support. A Custom Policy Information Point and Function Library are bound to
 * the PDP. The demo runs a few performance tests and illustrates different ways
 * of invoking the PDP.
 */
@Command(name = "sapl-demo-embedded", version = "4.1.0", mixinStandardHelpOptions = true,
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

    private static final double NS_PER_S  = 1_000_000_000.0D;
    private static final double NS_PER_MS = 1_000_000.0D;
    private static final double NS_PER_US = 1_000.0D;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

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
        try (var components = buildPdpComponents()) {
            var pdp = components.pdp();
            blockingDecisionDemo(pdp);
            streamingDecisionDemo(pdp);
            runBlockingDecideOnceBenchmark(pdp);
            runStreamingDecideTakeOneBenchmark(pdp);
            LOGGER.info("End of demo.");
        }
        return 0;
    }

    private PDPComponents buildPdpComponents() {
        var builder = PolicyDecisionPointBuilder.withDefaults()
                .withPolicyInformationPoint(new EchoPIP())
                .withFunctionLibrary(new SimpleFunctionLibrary());

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
     * Single decision via the blocking PDP. decideOnce returns the value
     * directly, no Reactor on the call path.
     */
    private static void blockingDecisionDemo(BlockingPolicyDecisionPoint pdp) {
        LOGGER.info("");
        LOGGER.info("Demo Part 1: Single blocking decisions via decideOnce()");
        var readDecision = pdp.decideOnce(READ_SUBSCRIPTION);
        LOGGER.info("Decision for action 'read' : {}", readDecision.decision());
        var writeDecision = pdp.decideOnce(WRITE_SUBSCRIPTION);
        LOGGER.info("Decision for action 'write': {}", writeDecision.decision());
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    /**
     * Continuous decision stream. decide() returns a SAPL Stream which
     * delivers updated decisions whenever the authorization context
     * changes. Always close the stream (try-with-resources).
     */
    private static void streamingDecisionDemo(BlockingPolicyDecisionPoint pdp) throws InterruptedException {
        LOGGER.info("");
        LOGGER.info("Demo Part 2: Streaming decisions via decide()");
        try (var stream = pdp.decide(READ_SUBSCRIPTION)) {
            var decision = stream.awaitNext();
            handleAuthorizationDecision(ACTION_READ, decision);
        }
        try (var stream = pdp.decide(WRITE_SUBSCRIPTION)) {
            var decision = stream.awaitNext();
            handleAuthorizationDecision(ACTION_WRITE, decision);
        }
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    private static void handleAuthorizationDecision(String action, AuthorizationDecision authzDecision) {
        LOGGER.info("Decision for action '{}': {}", action, authzDecision.decision());
    }

    private static void runBlockingDecideOnceBenchmark(BlockingPolicyDecisionPoint pdp) {
        var runs = getRuns();
        LOGGER.info("");
        LOGGER.info("Demo Part 3: Benchmark for decideOnce() (single blocking decision).");

        LOGGER.info("Warming up for {} runs...", runs);
        for (var i = 0; i < runs; i++) {
            pdp.decideOnce(WRITE_SUBSCRIPTION);
        }
        LOGGER.info("Measure time for {} runs...", runs);
        var start = System.nanoTime();
        for (var i = 0; i < runs; i++) {
            pdp.decideOnce(WRITE_SUBSCRIPTION);
        }
        var end = System.nanoTime();
        LOGGER.info("");
        logResults("Benchmark results for decideOnce():", runs, start, end);
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    private static void runStreamingDecideTakeOneBenchmark(BlockingPolicyDecisionPoint pdp) throws InterruptedException {
        var runs = getRuns();
        LOGGER.info("");
        LOGGER.info("Demo Part 4: Benchmark for decide() reading the first decision off the stream.");

        LOGGER.info("Warming up for {} runs...", runs);
        for (var i = 0; i < runs; i++) {
            try (var stream = pdp.decide(WRITE_SUBSCRIPTION)) {
                stream.awaitNext();
            }
        }
        LOGGER.info("Measure time for {} runs...", runs);

        var start = System.nanoTime();
        for (var i = 0; i < runs; i++) {
            try (var stream = pdp.decide(WRITE_SUBSCRIPTION)) {
                stream.awaitNext();
            }
        }
        var end = System.nanoTime();
        LOGGER.info("");
        logResults("Benchmark results for decide() + first:", runs, start, end);
        LOGGER.info("");
        LOGGER.info(LINE);
    }

    /**
     * Formats a nanosecond duration using the largest temporal unit for
     * which the value is at least one. Tried in order: s, ms, μs, ns.
     */
    private static String formatDuration(double nanoseconds) {
        if (nanoseconds >= NS_PER_S) {
            return DECIMAL_FORMAT.format(nanoseconds / NS_PER_S) + " s";
        }
        if (nanoseconds >= NS_PER_MS) {
            return DECIMAL_FORMAT.format(nanoseconds / NS_PER_MS) + " ms";
        }
        if (nanoseconds >= NS_PER_US) {
            return DECIMAL_FORMAT.format(nanoseconds / NS_PER_US) + " μs";
        }
        return DECIMAL_FORMAT.format(nanoseconds) + " ns";
    }

    private static void logResults(String title, int runs, long start, long end) {
        if (LOGGER.isInfoEnabled()) {
            final double totalNs = (double) end - start;
            LOGGER.info(title);
            LOGGER.info("Runs  : {}", runs);
            LOGGER.info("Total : {}", formatDuration(totalNs));
            LOGGER.info("Avg.  : {}", formatDuration(totalNs / runs));
        }
    }

}
