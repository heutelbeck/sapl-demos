/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package org.demo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.pdp.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.PolicyDecisionPointFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This demo shows how to manually construct a PDP without infrastructure support. A
 * Custom Policy Information Point and Function Library are bound to the PDP. The demo
 * runs a few performance tests and illustrates different ways of invoking the PDP.
 */
@Command(name = "sapl-demo-embedded", version = "2.1.0-SNAPSHOT", mixinStandardHelpOptions = true,
		description = "This demo shows how to manually construct a PDP without infrastructure support. "
				+ "A Custom Policy Information Point and Function Library are bound to the PDP. "
				+ "The demo runs a few performance tests and illustrates different ways of invoking the PDP. "
				+ "By default, ")
public class EmbeddedPDPDemo implements Callable<Integer> {

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

	private static final AuthorizationSubscription READ_SUBSCRIPTION = AuthorizationSubscription.of(SUBJECT,
			ACTION_READ, RESOURCE);

	private static final AuthorizationSubscription WRITE_SUBSCRIPTION = AuthorizationSubscription.of(SUBJECT,
			ACTION_WRITE, RESOURCE);

	private static final int RUNS = 20_000;

	private static final double BILLION = 1_000_000_000.0D;

	private static final double MILLION = 1_000_000.0D;

	private static final DecimalFormat decFormat = new DecimalFormat("#.####");

	public static void main(String... args) {
		System.exit(new CommandLine(new EmbeddedPDPDemo()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		EmbeddedPolicyDecisionPoint pdp;
		if (filesystem) {
			/*
			 * The factory method PolicyDecisionPointFactory.filesystemPolicyDecisionPoint
			 * creates a PDP witch is retrieving the policies and its configuration form
			 * the file system.
			 *
			 * It takes a parameter with the path and lists of extensions to load.
			 *
			 * The first list contains policy information points. The second list contains
			 * function libraries.
			 *
			 * The PDP will monitor the path at runtime for any changes made to the
			 * policies an update any subscribed PEPs accordingly.
			 */
			pdp = PolicyDecisionPointFactory.filesystemPolicyDecisionPoint(path, List.of(new EchoPIP()),
					List.of(new SimpleFunctionLibrary()));
		}
		else {
			/*
			 * The factory method PolicyDecisionPointFactory.resourcesPolicyDecisionPoint
			 * creates a PDP witch is retrieving the policies from the resources bundled
			 * with the application.
			 *
			 * In a typical project structure, policies are then located in the folder
			 * 'src/main/resources/policies'.
			 *
			 * The first list contains policy information points. The second list contains
			 * function libraries.
			 *
			 * The PDP will monitor the path at runtime for any changes made to the
			 * policies an update any subscribed PEPs accordingly.
			 */
			pdp = PolicyDecisionPointFactory.resourcesPolicyDecisionPoint(List.of(new EchoPIP()),
					List.of(new SimpleFunctionLibrary()));
		}

		blockingUsageDemo(pdp);

		reactiveUsageDemo(pdp);

		runPerformanceDemoSingleBlocking(pdp);

		runPerformanceDemoSingleSequentialReactive(pdp);

		LOGGER.info("End of demo.");
		pdp.dispose();
		return 0;
	}

	/**
	 * If traditional blocking behavior is required, use .blockFirst() this is not
	 * applicable in multi-threaded environments, e.g. web application. The reactor
	 * runtime will likely complain, that this behavior is not permitted.
	 */
	private static void blockingUsageDemo(PolicyDecisionPoint pdp) {
		LOGGER.info("");
		LOGGER.info("Demo Part 1: Accessing the PDP in a blocking manner using .blockFirst()");
		final AuthorizationDecision readDecision = pdp.decide(READ_SUBSCRIPTION).blockFirst();
		LOGGER.info("Decision for action 'read' : {}", readDecision != null ? readDecision.getDecision() : "null");
		final AuthorizationDecision writeDecision = pdp.decide(WRITE_SUBSCRIPTION).blockFirst();
		LOGGER.info("Decision for action 'write': {}", writeDecision != null ? writeDecision.getDecision() : "null");
		LOGGER.info("");
		LOGGER.info("------------------------------------------------------------------------");

	}

	/**
	 * If only one result is required, the appropriate way to consume exactly one decision
	 * event is to use .take(1) and subscribe accordingly. In this demo these will be
	 * processed sequentially, as this application is not declaring schedulers.
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
		LOGGER.info("------------------------------------------------------------------------");
	}

	private static void handleAuthorizationDecision(String action, AuthorizationDecision authzDecision) {
		LOGGER.info("Decision for action '{}': {}", action, authzDecision.getDecision());
	}

	private static void runPerformanceDemoSingleBlocking(PolicyDecisionPoint pdp) {
		LOGGER.info("");
		LOGGER.info("Demo Part 3: Perform a small benchmark for blocking decisions.");

		LOGGER.info("Warming up for {} runs...", RUNS);
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).blockFirst();
		}
		LOGGER.info("Measure time for {} runs...", RUNS);
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).blockFirst();
		}
		long end = System.nanoTime();
		LOGGER.info("");
		logResults("Benchmark results for blocking PDP access:", RUNS, start, end);
		LOGGER.info("");
		LOGGER.info("------------------------------------------------------------------------");
	}

	private static void runPerformanceDemoSingleSequentialReactive(PolicyDecisionPoint pdp) {
		LOGGER.info("");
		LOGGER.info("Demo Part 4: Perform a small benchmark for sequential .take(1) decisions.");

		LOGGER.info("Warming up for {} runs...", RUNS);
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
		}
		LOGGER.info("Measure time for {} runs...", RUNS);

		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
		}
		long end = System.nanoTime();
		LOGGER.info("");
		logResults("Benchmark results for .take(1) access:", RUNS, start, end);
		LOGGER.info("");
		LOGGER.info("------------------------------------------------------------------------");
	}

	private static double nanoToMs(double nanoseconds) {
		return nanoseconds / MILLION;
	}

	private static double nanoToS(double nanoseconds) {
		return nanoseconds / BILLION;
	}

	private static void logResults(String title, int runs, long start, long end) {
		LOGGER.info(title);
		LOGGER.info("Runs  : {}", runs);
		LOGGER.info("Total : {} s", decFormat.format(nanoToS((double) end - start)));
		LOGGER.info("Avg.  : {} ms", decFormat.format(nanoToMs(((double) end - start) / RUNS)));
	}

}
