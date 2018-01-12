package io.sapl.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.openconjurer.authz.api.pdp.Request;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PolicyGenerator {

	private static final int DEFAULT_BUFFER = 50;
	private final Random dice;
	private final PolicyGeneratorConfiguration config;

	public PolicyGenerator(PolicyGeneratorConfiguration config) {
		this.config = config;
		dice = new Random(config.getSeed());
	}

	private String generatePolicyString(String name) {
		final int numberOfVariables = config.getLogicalVariableCount();
		final int numberOfConnectors = numberOfVariables - 1;
		final int poolSize = config.getVariablePoolCount();

		final double negationChance = config.getNegationProbability();
		final double bracketChance = config.getBracketProbability();
		final double conjunctionChance = config.getConjunctionProbability();

		StringBuilder statement = new StringBuilder(DEFAULT_BUFFER).append("policy \"").append(name).append("\"")
				.append(System.lineSeparator()).append("permit ");

		int open = 0;
		for (int j = 0; j < numberOfVariables; ++j) {
			if (roll() <= negationChance) {
				statement.append('!');
			}
			while (roll() <= bracketChance) {
				statement.append('(');
				++open;
			}
			statement.append(getIdentifier(roll(poolSize)));
			double chance = 1.0 / (numberOfVariables - j);
			while (open > 0 && roll() < chance) {
				statement.append(')');
				--open;
			}
			if (j < numberOfConnectors) {
				if (roll() <= conjunctionChance) {
					statement.append(" && ");
				} else {
					statement.append(" || ");
				}
			}
		}

		return statement.toString();
	}

	private static String getIdentifier(int index) {
		return "resource.x" + index;
	}

	private double roll() {
		return dice.nextDouble();
	}

	private int roll(int supremum) {
		return dice.nextInt(supremum);
	}

	public void generatePolicies(String subfolder) throws FileNotFoundException, UnsupportedEncodingException {
		String path = config.getPath() + subfolder + "/";

		File folder = new File(path);
		if (folder.mkdirs()) {
			for (File file : folder.listFiles()) {
				if (file.getName().endsWith("sapl") && !file.delete()) {
					System.err.println(String.format("failed to delete: %s.", file.getAbsolutePath()));
				}
			}
		}
		for (int i = 0; i < config.getPolicyCount(); i++) {
			String name = "p_" + i;
			try (PrintWriter writer = new PrintWriter(path + name + ".sapl", StandardCharsets.UTF_8.name())) {
				writer.println(generatePolicyString(name));
			}
		}
	}

	public Collection<String> getVariables() {
		HashSet<String> variables = new HashSet<>();
		for (int i = 0; i < config.getVariablePoolCount(); i++) {
			variables.add("x" + i);
		}
		return variables;
	}

	public Request createRequestObject() {
		ObjectNode resource = JsonNodeFactory.instance.objectNode();
		for (String var : getVariables()) {
			resource = resource.put(var, roll() < config.getFalseProbability() ? false : true);
		}
		return new Request(NullNode.getInstance(), NullNode.getInstance(), resource, NullNode.getInstance());
	}
}
