package io.sapl.demo.testing.dsl.plain.storage;

import java.util.Map;

/**
 * minimal working example to represent some kind of storage for pdp configurations with a static accessor
 */
public class PdpConfigurationStorage {
    private PdpConfigurationStorage() {
    }

    private static final String DEFAULT = """
            {
                "algorithm": "DENY_UNLESS_PERMIT",
                "variables": {}
            }
            """;

    public static Map<String, String> getPdpConfigurationStore() {
        return Map.of("demoPDPConfig", DEFAULT);
    }
}
