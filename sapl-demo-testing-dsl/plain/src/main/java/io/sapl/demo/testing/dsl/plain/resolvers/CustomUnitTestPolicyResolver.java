package io.sapl.demo.testing.dsl.plain.resolvers;

import io.sapl.demo.testing.dsl.plain.storage.PolicyStorage;
import io.sapl.test.dsl.interfaces.UnitTestPolicyResolver;

/**
 * defines a custom resolver used in unit tests provides a simple example how to
 * add custom logic to resolve policy definitions see the
 * {@link UnitTestPolicyResolver} interface to learn when this is used.
 */
public class CustomUnitTestPolicyResolver implements UnitTestPolicyResolver {

    @Override
    public String resolvePolicyByIdentifier(String identifier) {
        return PolicyStorage.getPolicyStore().get(identifier);
    }
}
