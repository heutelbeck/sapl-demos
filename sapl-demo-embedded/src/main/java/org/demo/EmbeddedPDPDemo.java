/*******************************************************************************
 * Copyright 2017-2018 Dominic Heutelbeck (dheutelbeck@ftk.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.demo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.pdp.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.PolicyDecisionPointFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This demo shows how to manually construct a PDP without infrastructure
 * support. A Custom Policy Information Point and Function Library are bound to
 * the PDP. The demo runs a few performance tests and illustrates different ways
 * of invoking the PDP.
 */
@Command(description = "This demo shows how to manually construct a PDP without infrastructure support. "
		+ "A Custom Policy Information Point and Function Library are bound to the PDP. "
		+ "The demo runs a few performance tests and illustrates different ways of invoking the PDP.")
public class EmbeddedPDPDemo implements Callable<Integer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPDPDemo.class);

	@Option(names = { "-p",
			"-path" }, description = "Path to the folder in the filesystem where the demo will look for the configuration file 'pdp.json' and the '*.sapl' policy documents. Deafults to '~/sapl/policies'")
	private String path = "~/sapl/policies";

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
		// A PDP is constructed using the factory pattern
		// register the custom PIP and function library
		EmbeddedPolicyDecisionPoint pdp;
		if (path != null) {
			pdp = PolicyDecisionPointFactory.filesystemPolicyDecisionPoint(path, List.of(new EchoPIP()),
					List.of(new SimpleFunctionLibrary()));
		}
		// by default the policies are loaded from bundled resources.
		pdp = PolicyDecisionPointFactory.resourcesPolicyDecisionPoint(List.of(new EchoPIP()),
				List.of(new SimpleFunctionLibrary()));

		blockingUsageDemo(pdp);

		reactiveUsageDemo(pdp);

		runPerformanceDemoSingleBlocking(pdp);

		runPerformanceDemoSingleSequentialReactive(pdp);

		runPerformanceDemoMulti(pdp);

		runPerformanceDemoMultiAll(pdp);

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
		LOGGER.info("Single blocking decision by using .blockFirst()...");
		final AuthorizationDecision readDecision = pdp.decide(READ_SUBSCRIPTION).blockFirst();
		LOGGER.info("Decision for action 'read': {}", readDecision != null ? readDecision.getDecision() : "null");
		final AuthorizationDecision writeDecision = pdp.decide(WRITE_SUBSCRIPTION).blockFirst();
		LOGGER.info("Decision for action 'write': {}", writeDecision != null ? writeDecision.getDecision() : "null");
	}

	/**
	 * If only one result is required, the appropriate way to consume exactly one
	 * decision event is to use .take(1) and subscribe accordingly. In this demo
	 * these will be processed sequentially, as this application is not declaring
	 * schedulers.
	 */
	private static void reactiveUsageDemo(PolicyDecisionPoint pdp) {
		LOGGER.info("Single reactive decision by using .take(1) and .subscribe()...");
		pdp.decide(READ_SUBSCRIPTION).take(1)
				.subscribe(authzDecision -> handleAuthorizationDecision(ACTION_READ, authzDecision));
		pdp.decide(WRITE_SUBSCRIPTION).take(1)
				.subscribe(authzDecision -> handleAuthorizationDecision(ACTION_WRITE, authzDecision));
	}

	private static void handleAuthorizationDecision(String action, AuthorizationDecision authzDecision) {
		LOGGER.info("Decision for action '{}': {}", action, authzDecision.getDecision());
	}

	private static void runPerformanceDemoSingleBlocking(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance using sequential .blockFirst()");

		LOGGER.info("Warming up...");
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).blockFirst();
		}
		LOGGER.info("Measuring...");
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).blockFirst();
		}
		long end = System.nanoTime();
		logResults("Single Blocking Results:", RUNS, start, end);
	}

	private static void runPerformanceDemoSingleSequentialReactive(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance using sequential .take(1)");

		LOGGER.info("Warming up...");
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
		}
		LOGGER.info("Measuring...");

		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_SUBSCRIPTION).take(1).subscribe();
		}
		long end = System.nanoTime();
		logResults("Single Reactive Results:", RUNS, start, end);
	}

	protected static void runPerformanceDemoMulti(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance Multi...");

		final MultiAuthorizationSubscription multiSubscription = new MultiAuthorizationSubscription();
		multiSubscription.addAuthorizationSubscription("sub", SUBJECT, ACTION_READ, RESOURCE);

		LOGGER.info("Warming up...");
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(multiSubscription).take(1).subscribe();
		}
		LOGGER.info("Measuring...");

		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(multiSubscription).take(1).subscribe();
		}
		long end = System.nanoTime();
		logResults("MultiAuthorizationSubscription decide Results:", RUNS, start, end);
	}

	protected static void runPerformanceDemoMultiAll(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance Multi All...");

		final MultiAuthorizationSubscription multiSubscription = new MultiAuthorizationSubscription();
		multiSubscription.addAuthorizationSubscription("read", SUBJECT, ACTION_READ, RESOURCE);
		LOGGER.info("Warming up...");
		for (int i = 0; i < RUNS; i++) {
			pdp.decideAll(multiSubscription).take(1).subscribe();
		}
		LOGGER.info("Measuring...");
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decideAll(multiSubscription).take(1).subscribe();
		}
		long end = System.nanoTime();
		logResults("MultiAuthorizationSubscription decideAll Results:", RUNS, start, end);
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
