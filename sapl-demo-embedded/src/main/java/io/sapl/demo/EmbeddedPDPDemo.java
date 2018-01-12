package io.sapl.demo;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openconjurer.authz.api.functions.FunctionException;
import org.openconjurer.authz.api.interpreter.PolicyEvaluationException;
import org.openconjurer.authz.api.pip.AttributeException;
import org.openconjurer.authz.pdp.embedded.EmbeddedPolicyDecisionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedPDPDemo {

	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedPDPDemo.class);

	private static final String USAGE = "java -jar sapl-demo-embedded-1.0.0-SNAPSHOT-jar-with-dependencies.jar";
	private static final String HELP_DOC = "print this message";
	private static final String HELP = "help";
	private static final String POLICYPATH = "policypath";
	private static final String POLICYPATH_DOC = "ANT style pattern providing the path to the polices. "
			+ "The default path is 'classpath:policies'. The pattern will be extended by '/*.sapl' by the PDP/PRP. "
			+ "So all files with the '.sapl' suffix will be loaded.";

	private static final int RUNS = 100000;
	private static final double BILLION = 1000000000.0D;
	private static final double MILLION = 1000000.0D;

	private static double nanoToMs(double nanoseconds) {
		return nanoseconds / MILLION;
	}

	private static double nanoToS(double nanoseconds) {
		return nanoseconds / BILLION;
	}

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
			} else {
				runDemo(cmd.getOptionValue(POLICYPATH));
			}
		} catch (ParseException | IOException | PolicyEvaluationException | AttributeException | FunctionException e) {
			LOG.info("encountered an error running the demo: {}", e.getMessage(), e);
			System.exit(1);
		}

	}

	private static void runDemo(String path)
			throws IOException, PolicyEvaluationException, AttributeException, FunctionException {
		EmbeddedPolicyDecisionPoint pdp = new EmbeddedPolicyDecisionPoint(path);
		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			pdp.decide("willi", "read", "something");
			// log.info("response: " + response.toString());
		}
		long end = System.nanoTime();
		LOG.info("Start : {}", start);
		LOG.info("End   : {}", end);
		LOG.info("Runs  : {}", RUNS);
		LOG.info("Total : {}s", nanoToS((double) end - start));

		LOG.info("Avg.  : {}ms", nanoToMs(((double) end - start) / RUNS));
	}

}