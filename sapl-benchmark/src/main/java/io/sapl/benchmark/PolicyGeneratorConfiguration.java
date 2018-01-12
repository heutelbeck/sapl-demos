package io.sapl.benchmark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyGeneratorConfiguration {
	private String name;

	private long seed;
	private int policyCount;
	private int logicalVariableCount;
	private int variablePoolCount;

	private float bracketProbability;
	private float conjunctionProbability;
	private float negationProbability;
	private float falseProbability;

	private String path;
}
