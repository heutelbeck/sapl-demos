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
package io.sapl.benchmark;

import static io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType.SIMPLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jxls.template.SimpleExporter;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.XYChart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.api.pip.AttributeException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Benchmark {

	private static final int DEFAULT_HEIGHT = 1080;

	private static final int DEFAULT_WIDTH = 1920;

	private static final String ERROR_READING_TEST_CONFIGURATION = "Error reading test configuration";

	private static final String ERROR_WRITING_BITMAP = "Error writing bitmap";

	private static final String EXPORT_PROPERTIES = "number, name, preparation, duration, request, response";

	private static final String DEFAULT_PATH = "C:/sapl/";

	private static final String HELP_DOC = "print this message";

	private static final String HELP = "help";

	private static final String USAGE = "java -jar sapl-benchmark-1.0.0-SNAPSHOT-jar-with-dependencies.jar";

	private static final String PATH = "path";

	private static final String PATH_DOC = "path for output files";

	private static final int ITERATIONS = 10;

	private static final int RUNS = 30;

	private static final double MILLION = 1000000.0D;

	private static double nanoToMs(long nanoseconds) {
		return nanoseconds / MILLION;
	}

	private static List<String> getExportHeader() {
		return Arrays.asList("Iteration", "Test Case", "Preparation Time (ms)",
				"Execution Time (ms)", "Request String", "Response String (ms)");
	}

	private static double extractMin(List<XlsRecord> data) {
		double min = Double.MAX_VALUE;
		for (XlsRecord item : data) {
			if (item.getDuration() < min) {
				min = item.getDuration();
			}
		}
		return min;
	}

	private static double extractMax(List<XlsRecord> data) {
		double max = Double.MIN_VALUE;
		for (XlsRecord item : data) {
			if (item.getDuration() > max) {
				max = item.getDuration();
			}
		}
		return max;
	}

	private static double extractAvg(List<XlsRecord> data) {
		double sum = 0;
		for (XlsRecord item : data) {
			sum += item.getDuration();
		}
		return sum / data.size();
	}

	private static double extractMdn(List<XlsRecord> data) {
		List<Double> list = data.stream().map(XlsRecord::getDuration).sorted()
				.collect(Collectors.toList());
		int index = list.size() / 2;
		if (list.size() % 2 == 0) {
			return (list.get(index) + list.get(index - 1)) / 2;
		}
		else {
			return list.get(index);
		}
	}

	public static void main(String[] args) throws URISyntaxException {

		Options options = new Options();

		options.addOption(PATH, true, PATH_DOC);
		options.addOption(HELP, false, HELP_DOC);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(HELP)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(USAGE, options);
			}
			else {
				String path = cmd.getOptionValue(PATH);
				if (Strings.isNullOrEmpty(path)) {
					path = DEFAULT_PATH;
				}
				runBenchmark(path);
			}
		}
		catch (ParseException e) {
			LOGGER.info("encountered an error running the demo: {}", e.getMessage(), e);
			System.exit(1);
		}
	}

	public static void runBenchmark(String path) throws URISyntaxException {
		ObjectMapper mapper = new ObjectMapper();
		TestSuite suite = null;
		try {
			suite = mapper.readValue(Resources.getResource("tests.json"),
					TestSuite.class);
		}
		catch (IOException e) {
			LOGGER.error(ERROR_READING_TEST_CONFIGURATION, e);
			System.exit(1);
		}

		List<XlsRecord> data = new ArrayList<>();

		XYChart chart = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		List<Double> minValues = new LinkedList<>();
		List<Double> maxValues = new LinkedList<>();
		List<Double> avgValues = new LinkedList<>();
		List<Double> mdnValues = new LinkedList<>();

		List<PolicyGeneratorConfiguration> configs = suite.getCases();
		List<String> identifier = new LinkedList<>();
		for (PolicyGeneratorConfiguration config : configs) {
			benchmarkConfiguration(path, data, chart, minValues, maxValues, avgValues,
					mdnValues, identifier, config);
		}

		writeOverviewChart(path, chart);

		writeHistogram(path, minValues, maxValues, avgValues, mdnValues, identifier);

		writeExcelFile(path, data);
	}

	private static void benchmarkConfiguration(String path, List<XlsRecord> data,
			XYChart chart, List<Double> minValues, List<Double> maxValues,
			List<Double> avgValues, List<Double> mdnValues, List<String> identifier,
			PolicyGeneratorConfiguration config) throws URISyntaxException {
		identifier.add(config.getName());
		List<XlsRecord> results = null;
		try {
			results = runTest(config, path);
		}
		catch (IOException | PolicyEvaluationException e) {
			LOGGER.error("Error running test", e);
			System.exit(1);
		}
		double[] times = new double[results.size()];
		int i = 0;
		for (XlsRecord item : results) {
			times[i] = item.getDuration();
			i++;
		}

		XYChart details = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		details.setTitle("Evaluation Time");
		details.setXAxisTitle("Run");
		details.setYAxisTitle("ms");
		details.addSeries(config.getName(), times);

		try {
			BitmapEncoder.saveBitmap(details,
					path + config.getName().replaceAll("[^a-zA-Z0-9]", ""),
					BitmapFormat.PNG);
		}
		catch (IOException e) {
			LOGGER.error(ERROR_WRITING_BITMAP, e);
			System.exit(1);
		}

		minValues.add(extractMin(results));
		maxValues.add(extractMax(results));
		avgValues.add(extractAvg(results));
		mdnValues.add(extractMdn(results));

		chart.addSeries(config.getName(), times);
		data.addAll(results);
	}

	private static void writeOverviewChart(String path, XYChart chart) {
		chart.setTitle("Evaluation Time");
		chart.setXAxisTitle("Run");
		chart.setYAxisTitle("ms");
		try {
			BitmapEncoder.saveBitmap(chart, path + "overview", BitmapFormat.PNG);
		}
		catch (IOException e) {
			LOGGER.error(ERROR_WRITING_BITMAP, e);
			System.exit(1);
		}
	}

	private static void writeHistogram(String path, List<Double> minValues,
			List<Double> maxValues, List<Double> avgValues, List<Double> mdnValues,
			List<String> identifier) {
		CategoryChart histogram = new CategoryChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		histogram.setTitle("Aggregates");
		histogram.setXAxisTitle("Run");
		histogram.setYAxisTitle("ms");
		histogram.addSeries("min", identifier, minValues);
		histogram.addSeries("max", identifier, maxValues);
		histogram.addSeries("avg", identifier, avgValues);
		histogram.addSeries("mdn", identifier, mdnValues);

		try {
			BitmapEncoder.saveBitmap(histogram, path + "histogram", BitmapFormat.PNG);
		}
		catch (IOException e) {
			LOGGER.error(ERROR_WRITING_BITMAP, e);
			System.exit(1);
		}
	}

	private static void writeExcelFile(String path, List<XlsRecord> data) {
		File file = new File(path, "overview.xls");
		try (OutputStream os = new FileOutputStream(file)) {
			SimpleExporter exp = new SimpleExporter();
			exp.gridExport(getExportHeader(), data, EXPORT_PROPERTIES, os);
		}
		catch (IOException e) {
			LOGGER.error("Error writing XLS", e);
			System.exit(1);
		}
	}

	private static List<XlsRecord> runTest(PolicyGeneratorConfiguration config,
			String path)
			throws IOException, URISyntaxException, PolicyEvaluationException {

		PolicyGenerator generator = new PolicyGenerator(config);

		String subfolder = config.getName().replaceAll("[^a-zA-Z0-9]", "");
		generator.generatePolicies(subfolder);

		Files.copy(new File(path + "pdp.json"), new File(path + subfolder + "/pdp.json"));

		List<XlsRecord> results = new LinkedList<>();

		try {
			for (int i = 0; i < ITERATIONS; i++) {
				long begin = System.nanoTime();
				PolicyDecisionPoint pdp = EmbeddedPolicyDecisionPoint.builder()
						.withFilesystemPolicyRetrievalPoint(path + subfolder, SIMPLE)
						.build();
				double prep = nanoToMs(System.nanoTime() - begin);

				for (int j = 0; j < RUNS; j++) {
					Request request = generator.createRequestObject();

					long start = System.nanoTime();
					Response response = pdp.decide(request).blockFirst();
					long end = System.nanoTime();

					double diff = nanoToMs(end - start);

					results.add(new XlsRecord(j + (i * RUNS), config.getName(), prep,
							diff, request.toString(), response.toString()));

					LOGGER.info("Total : {}ms", diff);
				}
			}
		}
		catch (IOException | AttributeException | FunctionException e) {
			LOGGER.error("Error running test", e);
		}

		return results;
	}

}
