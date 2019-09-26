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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.PDPConfigurationException;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.api.pdp.multirequest.IdentifiableResponse;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pip.AttributeException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder;
import reactor.core.publisher.Flux;

public class EmbeddedPDPDemo {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPDPDemo.class);

	private static final String USAGE = "java -jar sapl-demo-embedded-2.0.0-SNAPSHOT-jar-with-dependencies.jar";

	private static final String HELP_DOC = "print this message";

	private static final String HELP = "help";

	private static final String POLICYPATH = "policypath";

	private static final String POLICYPATH_DOC = "ANT style pattern providing the path to the polices. "
			+ "The default path is 'classpath:policies'. The pattern will be extended by '/*.sapl' by the PDP/PRP. "
			+ "So all files with the '.sapl' suffix will be loaded.";

	private static final String SUBJECT = "willi";

	private static final String ACTION_READ = "read";

	private static final String ACTION_WRITE = "write";

	private static final String RESOURCE = "something";

	private static final Request READ_REQUEST = buildRequest(SUBJECT, ACTION_READ,
			RESOURCE);

	private static final Request WRITE_REQUEST = buildRequest(SUBJECT, ACTION_WRITE,
			RESOURCE);

	private static final int RUNS = 25_000;

	private static final double BILLION = 1_000_000_000.0D;

	private static final double MILLION = 1_000_000.0D;

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(POLICYPATH, true, POLICYPATH_DOC);
		options.addOption(HELP, false, HELP_DOC);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(HELP)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(USAGE, options);
			}
			else {
				EmbeddedPDPDemo.runDemo(cmd.getOptionValue(POLICYPATH));
			}
		}
		catch (ParseException | IOException | AttributeException | FunctionException
				| URISyntaxException | PolicyEvaluationException | PDPConfigurationException e) {
			LOGGER.info("encountered an error running the demo: {}", e.getMessage(), e);
			System.exit(1);
		}

	}

	private static void runDemo(String path) throws IOException, AttributeException,
			FunctionException, URISyntaxException, PolicyEvaluationException, PDPConfigurationException {
		Builder builder = EmbeddedPolicyDecisionPoint.builder();
		if (path != null) {
			builder = builder.withFilesystemPolicyRetrievalPoint(path,
					Builder.IndexType.SIMPLE);
		}
		builder.withPolicyInformationPoint(new EchoPIP())
				.withFunctionLibrary(new SimpleFunctionLibrary());
		final EmbeddedPolicyDecisionPoint pdp = builder.build();

		blockingUsageDemo(pdp);
		reactiveUsageDemo(pdp);

		// to compare single requests with multi-requests, only run one of the following
		// methods at once to avoid later methods benefit from earlier methods (e.g. class loading)
		runPerformanceDemoSingle(pdp);
//		runPerformanceDemoMulti(pdp);
//		runPerformanceDemoMultiAll(pdp);
	}

	private static void blockingUsageDemo(PolicyDecisionPoint pdp) {
		LOGGER.info("Blocking...");
		final Response readResponse = pdp.decide(READ_REQUEST).blockFirst();
		LOGGER.info("Decision for action 'read': {}",
				readResponse != null ? readResponse.getDecision() : "null");
		final Response writeResponse = pdp.decide(WRITE_REQUEST).blockFirst();
		LOGGER.info("Decision for action 'write': {}",
				writeResponse != null ? writeResponse.getDecision() : "null");
	}

	private static void reactiveUsageDemo(PolicyDecisionPoint pdp) {
		LOGGER.info("Reactive...");
		final Flux<Response> readResponse = pdp.decide(READ_REQUEST);
		readResponse.subscribe(response -> handleResponse(ACTION_READ, response));
		final Flux<Response> writeResponse = pdp.decide(WRITE_REQUEST);
		writeResponse.subscribe(response -> handleResponse(ACTION_WRITE, response));
	}

	private static void handleResponse(String action, Response response) {
		LOGGER.info("Decision for action '{}': {}", action, response.getDecision());
	}

	private static void runPerformanceDemoSingle(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance Single...");

		final Response response = pdp.decide(READ_REQUEST).blockFirst();
		LOGGER.info("{}", response);

		int[] count = { 0 };
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide(READ_REQUEST).take(1).subscribe(resp -> count[0]++);
			//pdp.decide(READ_REQUEST).blockFirst();
			//count[0]++;
		}
		long end = System.nanoTime();
		LOGGER.info("Single");
		LOGGER.info("Runs  : {}", RUNS);
		LOGGER.info("Count  : {}", count[0]);
		LOGGER.info("Total : {}s", nanoToS((double) end - start));
		LOGGER.info("Avg.  : {}ms", nanoToMs(((double) end - start) / RUNS));
	}

	private static void runPerformanceDemoMulti(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance Multi...");

		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addRequest("req", SUBJECT, ACTION_READ, RESOURCE);
		final IdentifiableResponse identifiableResponse = pdp.decide(multiRequest).blockFirst();
		LOGGER.info("{}", identifiableResponse.getResponse());

		int[] count = { 0 };
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			//pdp.decide(multiRequest).take(1).subscribe(resp -> count[0]++);
			pdp.decide(multiRequest).blockFirst();
			count[0]++;
		}
		long end = System.nanoTime();
		LOGGER.info("Multi");
		LOGGER.info("Runs  : {}", RUNS);
		LOGGER.info("Count  : {}", count[0]);
		LOGGER.info("Total : {}s", nanoToS((double) end - start));
		LOGGER.info("Avg.  : {}ms", nanoToMs(((double) end - start) / RUNS));
	}

	private static void runPerformanceDemoMultiAll(PolicyDecisionPoint pdp) {
		LOGGER.info("Performance Multi All...");

		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addRequest("read", SUBJECT, ACTION_READ, RESOURCE);
		final MultiResponse multiResponse = pdp.decideAll(multiRequest).blockFirst();
		LOGGER.info("{}", multiResponse.getResponseForRequestWithId("read"));

		int[] count = { 0 };
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decideAll(multiRequest).take(1).subscribe(resp -> count[0]++);
			//pdp.decideAll(multiRequest).blockFirst();
			//count[0]++;
		}
		long end = System.nanoTime();
		LOGGER.info("Multi All");
		LOGGER.info("Runs  : {}", RUNS);
		LOGGER.info("Count  : {}", count[0]);
		LOGGER.info("Total : {}s", nanoToS((double) end - start));
		LOGGER.info("Avg.  : {}ms", nanoToMs(((double) end - start) / RUNS));
	}

	private static double nanoToMs(double nanoseconds) {
		return nanoseconds / MILLION;
	}

	private static double nanoToS(double nanoseconds) {
		return nanoseconds / BILLION;
	}

	private static Request buildRequest(Object subject, Object action, Object resource) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return new Request(mapper.valueToTree(subject), mapper.valueToTree(action),
				mapper.valueToTree(resource), null);
	}

}
