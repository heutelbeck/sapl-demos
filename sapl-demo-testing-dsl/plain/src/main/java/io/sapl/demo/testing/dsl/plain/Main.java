package io.sapl.demo.testing.dsl.plain;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sapl.test.coverage.api.CoverageAPIFactory;

public class Main {

    public static void main(String[] args) throws IOException {
        Logger    logger      = LoggerFactory.getLogger(Main.class);
        final var testAdapter = new TestAdapter();

        final var results = new HashMap<String, Throwable>();

        results.putAll(testAdapter.executeTests("test.sapltest"));
        results.putAll(testAdapter.executeTests("IdentifierDefinedTest", """
                test "policyWithSimpleFunction" {
                    scenario "test_policyWithSimpleFunction"
                    register
                        - library TemporalFunctionLibrary
                    when subject "willi" attempts action "read" on resource "something"
                    then expect permit;
                }"""));

        if (!results.isEmpty()) {
            logger.info("There are failed tests:");
            results.forEach((key, value) -> logger.info("Test '{}' failed with error: {}", key, value));
        } else {
            logger.info("All tests passed");
            final var api = CoverageAPIFactory.constructCoverageHitReader(Paths.get("target").resolve("sapl-coverage"));
            logger.info("policy hits");
            api.readPolicyHits().forEach(policyHit -> logger.info(policyHit.toString()));
            logger.info("policy set hits");
            api.readPolicySetHits().forEach(policySetHit -> logger.info(policySetHit.toString()));
            logger.info("policy condition hits");
            api.readPolicyConditionHits().forEach(policyConditionHit -> logger.info(policyConditionHit.toString()));
        }
    }
}
