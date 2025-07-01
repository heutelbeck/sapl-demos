package io.sapl.demo.testing.dsl.plain;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.demo.testing.dsl.plain.resolvers.CustomIntegrationTestPolicyResolver;
import io.sapl.demo.testing.dsl.plain.resolvers.CustomUnitTestPolicyResolver;
import io.sapl.demo.testing.dsl.plain.storage.TestStorage;
import io.sapl.test.coverage.api.CoverageAPIFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        // initialize the test adapter and a Map to hold failed tests
        final var testAdapter = getConfiguredTestAdapter();
        final var failedTests = new HashMap<String, Throwable>();

        // load all test definitions from the TestStorage, execute test for each
        // definition and put failed tests into map
        TestStorage.getTestStore()
                .forEach((identifier, test) -> failedTests.putAll(testAdapter.executeTests(identifier, test)));

        // evaluate the results
        if (!failedTests.isEmpty()) {
            LOGGER.info("There are failed tests:");
            failedTests.forEach((key, value) -> LOGGER.info("Test '%s' failed with error: '%s'".formatted(key, value)));
        } else {
            LOGGER.info("All tests passed");
        }
        printCoverage();
    }

    /**
     * holds the logic to construct the actual {@link TestAdapter} instance by
     * initializing the {@link CustomUnitTestPolicyResolver} and
     * {@link CustomIntegrationTestPolicyResolver} beforehand and passing them to
     * the
     * {@link TestAdapter#TestAdapter(UnitTestPolicyResolver, IntegrationTestPolicyResolver)}
     * 
     * @return the created test adapter
     */
    private static TestAdapter getConfiguredTestAdapter() {
        final var customUnitTestPolicyResolver        = new CustomUnitTestPolicyResolver();
        final var customIntegrationTestPolicyResolver = new CustomIntegrationTestPolicyResolver();
        customIntegrationTestPolicyResolver.addIntegrationTestConfiguration("demoSet",
                List.of("policy_A", "policy_B", "policy_C"), "demoPDPConfig");

        return new TestAdapter(customUnitTestPolicyResolver, customIntegrationTestPolicyResolver);
    }

    /**
     * helper method to show how to print coverage hit information after test
     * execution
     * 
     * @throws IOException when the
     *                     {@link io.sapl.test.coverage.api.CoverageHitReader}
     *                     throws
     */
    private static void printCoverage() throws IOException {
        final var coverageHitReader = CoverageAPIFactory
                .constructCoverageHitReader(Paths.get("target").resolve("sapl-coverage"));
        LOGGER.info("policy hits");
        coverageHitReader.readPolicyHits().forEach(policyHit -> LOGGER.info(policyHit.toString()));
        LOGGER.info("policy set hits");
        coverageHitReader.readPolicySetHits().forEach(policySetHit -> LOGGER.info(policySetHit.toString()));
        LOGGER.info("policy condition hits");
        coverageHitReader.readPolicyConditionHits()
                .forEach(policyConditionHit -> LOGGER.info(policyConditionHit.toString()));
    }
}
