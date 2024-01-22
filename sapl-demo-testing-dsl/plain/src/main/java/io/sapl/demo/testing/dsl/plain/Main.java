package io.sapl.demo.testing.dsl.plain;

import io.sapl.test.coverage.api.CoverageAPIFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
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

        if(!results.isEmpty()) {
            System.out.println("There are failed tests:");
            results.forEach((key, value) -> System.out.printf("Test '%s' failed with error: %s", key, value));
        } else {
            System.out.println("All tests passed");
            final var api = CoverageAPIFactory.constructCoverageHitReader(Paths.get("target").resolve("sapl-coverage"));
            System.out.println("policy hits");
            api.readPolicyHits().forEach(policyHit -> System.out.println(policyHit.toString()));
            System.out.println("policy set hits");
            api.readPolicySetHits().forEach(policySetHit -> System.out.println(policySetHit.toString()));
            System.out.println("policy condition hits");
            api.readPolicyConditionHits().forEach(policyConditionHit -> System.out.println(policyConditionHit.toString()));
        }
    }
}