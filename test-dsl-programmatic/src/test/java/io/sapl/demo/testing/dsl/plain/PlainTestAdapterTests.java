/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.demo.testing.dsl.plain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.api.pdp.CombiningAlgorithm;
import io.sapl.test.plain.PlainTestAdapter;
import io.sapl.test.plain.PlainTestResults;
import io.sapl.test.plain.SaplDocument;
import io.sapl.test.plain.SaplTestDocument;
import io.sapl.test.plain.ScenarioResult;
import io.sapl.test.plain.TestConfiguration;
import io.sapl.test.plain.TestEvent.ExecutionCompleted;
import io.sapl.test.plain.TestEvent.ScenarioCompleted;
import io.sapl.test.plain.TestStatus;

/**
 * Tests for PlainTestAdapter demonstration.
 * <p>
 * This test loads policies and test definitions from the classpath
 * and executes them using PlainTestAdapter.
 */
class PlainTestAdapterTests {

    private static final Logger LOG = LoggerFactory.getLogger(PlainTestAdapterTests.class);

    @Test
    void executeAllTests() {
        LOG.info("=== SAPL PlainTestAdapter Demo ===");

        // Load policies and tests
        var policies = loadPolicies();
        var tests    = loadTests();

        LOG.info("Loaded {} policies and {} test documents", policies.size(), tests.size());

        // Create configuration
        var config = TestConfiguration.builder().withSaplDocuments(policies).withSaplTestDocuments(tests)
                .withDefaultAlgorithm(CombiningAlgorithm.DENY_OVERRIDES).build();

        // Execute tests using PlainTestAdapter
        var adapter = new PlainTestAdapter();

        LOG.info("--- Executing Tests ---");

        // Collect results
        var allResults = new ArrayList<ScenarioResult>();
        PlainTestResults[] finalResults = new PlainTestResults[1];

        // Demonstrate reactive execution with progress events
        adapter.executeReactive(config).doOnNext(event -> {
            if (event instanceof ScenarioCompleted(ScenarioResult result)) {
                var status = switch (result.status()) {
                    case PASSED -> "[PASS]";
                    case FAILED -> "[FAIL]";
                    case ERROR -> "[ERROR]";
                };
                LOG.info("{} {} > {}", status, result.requirementName(), result.scenarioName());
                if (result.failureMessage() != null) {
                    LOG.info("       {}", result.failureMessage());
                }
                allResults.add(result);
            } else if (event instanceof ExecutionCompleted(PlainTestResults results)) {
                LOG.info("--- Results ---");
                LOG.info("Total: {}  Passed: {}  Failed: {}  Errors: {}", results.total(), results.passed(),
                        results.failed(), results.errors());
                finalResults[0] = results;

                if (!results.allPassed()) {
                    LOG.warn("Some tests failed:");
                    results.scenarioResults().stream().filter(r -> r.status() != TestStatus.PASSED)
                            .forEach(r -> LOG.warn("  - {} > {}: {}", r.requirementName(), r.scenarioName(),
                                    r.failureMessage()));
                }
            }
        }).blockLast();

        // Verify we got results
        assertThat(finalResults[0]).isNotNull();
        assertThat(finalResults[0].total()).isGreaterThan(0);

        // Log summary
        var results = finalResults[0];
        LOG.info("Test execution completed: {} passed, {} failed, {} errors", results.passed(), results.failed(),
                results.errors());
    }

    private static List<SaplDocument> loadPolicies() {
        var policies = new ArrayList<SaplDocument>();

        // Load policies from classpath
        policies.add(loadPolicy("policies/policySimple.sapl", "policySimple"));
        policies.add(loadPolicy("policies/policyWithSimpleFunction.sapl", "policyWithSimpleFunction"));
        policies.add(loadPolicy("policies/policyStreaming.sapl", "policyStreaming"));
        policies.add(loadPolicy("policies/policyWithObligationAndResource.sapl", "policyWithObligationAndResource"));
        policies.add(loadPolicy("policies/policyWithSinglePIP.sapl", "policyWithSinglePIP"));
        policies.add(loadPolicy("policies/policyWithSimplePIP.sapl", "policyWithSimplePIP"));
        policies.add(loadPolicy("policies/policyWithComplexPIP.sapl", "policyWithComplexPIP"));
        policies.add(loadPolicy("policiesIT/policy_A.sapl", "policiesIT/policy_A"));
        policies.add(loadPolicy("policiesIT/policy_B.sapl", "policiesIT/policy_B"));
        policies.add(loadPolicy("policiesIT/policy_C.sapl", "policiesIT/policy_C"));

        return policies;
    }

    private static SaplDocument loadPolicy(String resourcePath, String name) {
        var source = loadResource(resourcePath);
        return SaplDocument.of(name, source, resourcePath);
    }

    private static List<SaplTestDocument> loadTests() {
        var tests = new ArrayList<SaplTestDocument>();

        // Load test definitions from classpath
        tests.add(loadTest("unit/singleRequirement.sapltest", "singleRequirement"));
        tests.add(loadTest("unit/functionMocking.sapltest", "functionMocking"));
        tests.add(loadTest("unit/featureDemo.sapltest", "featureDemo"));
        tests.add(loadTest("unit/attributeMocking.sapltest", "attributeMocking"));
        tests.add(loadTest("unit/multipleRequirementsAndCentralGiven.sapltest", "multipleRequirementsAndCentralGiven"));
        tests.add(loadTest("unit/pipImport.sapltest", "pipImport"));
        tests.add(loadTest("unit/matcherValidation.sapltest", "matcherValidation"));

        return tests;
    }

    private static SaplTestDocument loadTest(String resourcePath, String name) {
        var source = loadResource(resourcePath);
        return new SaplTestDocument(name, name, source);
    }

    private static String loadResource(String path) {
        try (InputStream is = PlainTestAdapterTests.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + path, e);
        }
    }
}
