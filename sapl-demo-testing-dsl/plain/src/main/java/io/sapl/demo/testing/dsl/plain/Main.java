/*
 * Copyright (C) 2017-2025 Dominic Heutelbeck (dominic@heutelbeck.com)
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.sapl.test.plain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.api.pdp.CombiningAlgorithm;
import io.sapl.test.plain.TestEvent.ExecutionCompleted;
import io.sapl.test.plain.TestEvent.ScenarioCompleted;

/**
 * Demonstrates programmatic test execution using PlainTestAdapter.
 * <p>
 * This demo loads the same policies and tests used in the JUnit demo
 * but executes them programmatically without JUnit.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("=== SAPL PlainTestAdapter Demo ===\n");

        // Load policies and tests
        var policies = loadPolicies();
        var tests = loadTests();

        LOG.info("Loaded {} policies and {} test documents\n", policies.size(), tests.size());

        // Create configuration
        var config = TestConfiguration.builder()
                .withSaplDocuments(policies)
                .withSaplTestDocuments(tests)
                .withDefaultAlgorithm(CombiningAlgorithm.DENY_OVERRIDES)
                .build();

        // Execute tests using PlainTestAdapter
        var adapter = new PlainTestAdapter();

        LOG.info("--- Executing Tests ---\n");

        // Demonstrate reactive execution with progress events
        adapter.executeReactive(config).subscribe(event -> {
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
            } else if (event instanceof ExecutionCompleted(PlainTestResults results)) {
                LOG.info("\n--- Results ---");
                LOG.info("Total: {}  Passed: {}  Failed: {}  Errors: {}",
                        results.total(), results.passed(), results.failed(), results.errors());

                if (results.allPassed()) {
                    LOG.info("\nAll tests passed!");
                } else {
                    LOG.error("\nSome tests failed!");
                    // List failures
                    results.scenarioResults().stream()
                            .filter(r -> r.status() != TestStatus.PASSED)
                            .forEach(r -> LOG.error("  - {} > {}: {}",
                                    r.requirementName(), r.scenarioName(), r.failureMessage()));
                }
            }
        });
    }

    private static List<SaplDocument> loadPolicies() {
        var policies = new ArrayList<SaplDocument>();

        // Load policies from classpath (same as junit demo)
        policies.add(loadPolicy("policies/policySimple.sapl", "policySimple"));
        policies.add(loadPolicy("policies/policyWithSimpleFunction.sapl", "policyWithSimpleFunction"));
        policies.add(loadPolicy("policies/policyStreaming.sapl", "policyStreaming"));
        policies.add(loadPolicy("policies/policyWithObligationAndResource.sapl", "policyWithObligationAndResource"));
        policies.add(loadPolicy("policiesIT/policy_A.sapl", "policy_A"));
        policies.add(loadPolicy("policiesIT/policy_B.sapl", "policy_B"));
        policies.add(loadPolicy("policiesIT/policy_C.sapl", "policy_C"));

        return policies;
    }

    private static SaplDocument loadPolicy(String resourcePath, String name) {
        var source = loadResource(resourcePath);
        return new SaplDocument(name, name, source);
    }

    private static List<SaplTestDocument> loadTests() {
        var tests = new ArrayList<SaplTestDocument>();

        // Load test definitions from classpath
        tests.add(loadTest("unit/singleRequirement.sapltest", "singleRequirement"));
        tests.add(loadTest("unit/functionMocking.sapltest", "functionMocking"));

        return tests;
    }

    private static SaplTestDocument loadTest(String resourcePath, String name) {
        var source = loadResource(resourcePath);
        return new SaplTestDocument(name, name, source);
    }

    private static String loadResource(String path) {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + path, e);
        }
    }
}
