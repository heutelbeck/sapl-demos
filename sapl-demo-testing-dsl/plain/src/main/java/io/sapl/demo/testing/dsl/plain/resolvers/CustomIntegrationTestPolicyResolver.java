package io.sapl.demo.testing.dsl.plain.resolvers;

import io.sapl.demo.testing.dsl.plain.storage.PdpConfigurationStorage;
import io.sapl.demo.testing.dsl.plain.storage.PolicyStorage;
import io.sapl.test.dsl.interfaces.IntegrationTestConfiguration;
import io.sapl.test.dsl.interfaces.IntegrationTestPolicyResolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * defines a custom resolver used in integration tests
 * provides a simple example how to add custom logic to resolve policy definitions and PDP configurations
 * see the {@link IntegrationTestPolicyResolver} interface to learn about when each method is used.
 */
public class CustomIntegrationTestPolicyResolver implements IntegrationTestPolicyResolver {
    private final Map<String, IntegrationTestConfiguration> integrationTestConfiguration = new HashMap<>();

    public void addIntegrationTestConfiguration(final String identifier, final List<String> policies, final String pdpConfiguration) {
        final var resolvedPolicies = policies.stream().map(this::resolvePolicyByIdentifier).toList();
        final var resolvedPdpConfiguration = resolvePDPConfigurationByIdentifier(pdpConfiguration);

        final var config =  new IntegrationTestConfiguration() {

            @Override
            public List<String> getDocumentInputStrings() {
                return resolvedPolicies;
            }

            @Override
            public String getPDPConfigurationInputString() {
                return resolvedPdpConfiguration;
            }
        };
        integrationTestConfiguration.put(identifier, config);
    }

    @Override
    public String resolvePolicyByIdentifier(String identifier) {
        return PolicyStorage.getPolicyStore().get(identifier);
    }

    @Override
    public String resolvePDPConfigurationByIdentifier(String identifier) {
        return PdpConfigurationStorage.getPdpConfigurationStore().get(identifier);
    }

    @Override
    public IntegrationTestConfiguration resolveConfigurationByIdentifier(String identifier) {
        return integrationTestConfiguration.get(identifier);
    }
}
