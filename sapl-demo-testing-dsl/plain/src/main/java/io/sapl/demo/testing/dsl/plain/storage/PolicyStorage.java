package io.sapl.demo.testing.dsl.plain.storage;

import java.util.Map;

/**
 * minimal working example to represent some kind of storage for policy
 * definitions with a static accessor
 */
public class PolicyStorage {
    private PolicyStorage() {
    }

    private static final String POLICY_SIMPLE = """
            set "testSet"
            deny-unless-permit

            policy "policySimple"
            permit
                action == "read"
            where
                subject == "willi";
            """;

    private static final String POLICY_STREAMING = """
            policy "policyStreaming"
            permit
              resource == "heartBeatData"
            where
              subject == "ROLE_DOCTOR";
              time.secondOf(<time.now>) > 4;
            """;

    private static final String POLICY_A = """
            policy "policy_A"
            deny
                resource == "foo"
            where
                "WILLI" == subject;
            """;
    private static final String POLICY_B = """
            policy "policy_B"
            permit
                resource == "foo"
            where
                "WILLI" == subject;
            """;
    private static final String POLICY_C = """
            policy "policyStreaming"
            permit
              resource == "bar"
            where
              subject == "WILLI";
              var interval = 2;
              time.secondOf(<time.now(interval)>) >= 4;
            """;

    public static Map<String, String> getPolicyStore() {
        return Map.of("policySimple", POLICY_SIMPLE, "policyStreaming", POLICY_STREAMING, "policy_A", POLICY_A,
                "policy_B", POLICY_B, "policy_C", POLICY_C);
    }
}
