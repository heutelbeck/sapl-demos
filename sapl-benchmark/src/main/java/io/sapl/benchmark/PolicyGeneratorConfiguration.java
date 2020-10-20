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

import lombok.Builder;

@Builder
public class PolicyGeneratorConfiguration {

	@Builder.Default
    private String name = "";

	@Builder.Default
    private long seed = 0L;

	@Builder.Default
    private int policyCount = 0;

	@Builder.Default
    private int logicalVariableCount = 0;

	@Builder.Default
    private int variablePoolCount = 0;

	@Builder.Default
    private double bracketProbability = 0D;

	@Builder.Default
    private double conjunctionProbability = 0D;

	@Builder.Default
    private double negationProbability = 0D;

	@Builder.Default
    private double falseProbability = 0D;

	@Builder.Default
    private String path = "";

    public PolicyGeneratorConfiguration() {
    }

    public PolicyGeneratorConfiguration(String name, long seed, int policyCount, int logicalVariableCount, int variablePoolCount, double bracketProbability, double conjunctionProbability, double negationProbability, double falseProbability, String path) {
        this.name = name;
        this.seed = seed;
        this.policyCount = policyCount;
        this.logicalVariableCount = logicalVariableCount;
        this.variablePoolCount = variablePoolCount;
        this.bracketProbability = bracketProbability;
        this.conjunctionProbability = conjunctionProbability;
        this.negationProbability = negationProbability;
        this.falseProbability = falseProbability;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getPolicyCount() {
        return policyCount;
    }

    public void setPolicyCount(int policyCount) {
        this.policyCount = policyCount;
    }

    public int getLogicalVariableCount() {
        return logicalVariableCount;
    }

    public void setLogicalVariableCount(int logicalVariableCount) {
        this.logicalVariableCount = logicalVariableCount;
    }

    public int getVariablePoolCount() {
        return variablePoolCount;
    }

    public void setVariablePoolCount(int variablePoolCount) {
        this.variablePoolCount = variablePoolCount;
    }

    public double getBracketProbability() {
        return bracketProbability;
    }

    public void setBracketProbability(double bracketProbability) {
        this.bracketProbability = bracketProbability;
    }

    public double getConjunctionProbability() {
        return conjunctionProbability;
    }

    public void setConjunctionProbability(double conjunctionProbability) {
        this.conjunctionProbability = conjunctionProbability;
    }

    public double getNegationProbability() {
        return negationProbability;
    }

    public void setNegationProbability(double negationProbability) {
        this.negationProbability = negationProbability;
    }

    public double getFalseProbability() {
        return falseProbability;
    }

    public void setFalseProbability(double falseProbability) {
        this.falseProbability = falseProbability;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
