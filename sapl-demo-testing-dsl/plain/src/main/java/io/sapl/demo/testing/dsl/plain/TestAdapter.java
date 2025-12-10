package io.sapl.demo.testing.dsl.plain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.deprecated.SaplTestException;
import io.sapl.test.dsl.interfaces.IntegrationTestPolicyResolver;
import io.sapl.test.dsl.interfaces.TestNode;
import io.sapl.test.dsl.interfaces.UnitTestPolicyResolver;
import io.sapl.test.dsl.setup.BaseTestAdapter;
import io.sapl.test.dsl.setup.TestCase;
import io.sapl.test.dsl.setup.TestContainer;
import io.sapl.test.grammar.sapltest.ImportType;

/**
 * Default TestAdapter that is used to execute the tests. needs to extend the
 * {@link BaseTestAdapter} with the specific Type that represents your target
 * Representation in this Case {@link TestCase} to just have a runnable test
 * without a framework setup.
 */
public class TestAdapter extends BaseTestAdapter<List<TestCase>> {

    /**
     * use the special constructor provided by {@link BaseTestAdapter} to customize
     * policy loading behaviour
     * 
     * @param customUnitTestPolicyResolver        the custom resolver used for unit
     *                                            tests
     * @param customIntegrationTestPolicyResolver the custom resolver used for
     *                                            integration tests
     */
    public TestAdapter(final UnitTestPolicyResolver customUnitTestPolicyResolver,
            final IntegrationTestPolicyResolver customIntegrationTestPolicyResolver) {
        super(customUnitTestPolicyResolver, customIntegrationTestPolicyResolver);
    }

    /**
     * helper method to destructure nested test definitions
     * 
     * @param testNodes collection of test nodes being either {@link TestCase} or
     *                  {@link TestContainer} instances.
     * @return a list of {@link TestCase} instances
     */
    private List<TestCase> getDynamicContainersFromTestNode(Collection<? extends TestNode> testNodes) {
        if (null == testNodes) {
            return Collections.emptyList();
        }

        return testNodes.stream().flatMap(testNode -> {
            if (testNode instanceof TestCase testCase) {
                return Stream.of(testCase);
            } else if (testNode instanceof TestContainer testContainer) {
                return getDynamicContainersFromTestNode(testContainer.getTestNodes()).stream();
            }
            throw new SaplTestException("Unknown type of TestNode");
        }).toList();
    }

    /**
     * run the created tests
     * 
     * @param tests the tests to run
     * @return a Map containing the identifier and {@link Throwable} of each failed
     *         tests
     */
    private Map<String, Throwable> runTests(List<TestCase> tests) {
        final var failedTests = new HashMap<String, Throwable>();

        tests.forEach(test -> {
            try {
                test.run();
            } catch (Exception e) {
                failedTests.put(test.getIdentifier(), e);
            }
        });
        return failedTests;
    }

    /**
     * used in {@link Main} class to start test execution logic
     * 
     * @param identifier the identifier for the test input
     * @param input      the test definition
     * @return the map created by {@link TestAdapter#runTests(List)}
     */
    public Map<String, Throwable> executeTests(String identifier, String input) {
        final var tests = createTest(identifier, input);
        return runTests(tests);
    }

    /**
     * override that is called by the {@link BaseTestAdapter} to convert
     * TestContainer into a list of {@link TestCase} instances.
     * 
     * @param testContainer          the TestContainer instance
     * @param shouldSetTestSourceUri not used for plain setup since there is no
     *                               testSourceUri
     * @return list of the converted {@link TestCase} instances
     */
    @Override
    protected List<TestCase> convertTestContainerToTargetRepresentation(TestContainer testContainer,
            boolean shouldSetTestSourceUri) {
        return getDynamicContainersFromTestNode(testContainer.getTestNodes());
    }

    /**
     * override this method to register FunctionLibraries and PIPs they are then
     * used in test execution when the test definition has an import of the
     * corresponding type Static registrations expect the Class type
     * (STATIC_FUNCTION_LIBRARY and STATIC_PIP) Non-Static registrations expect the
     * concrete object to be passed this enables using PIPs and FunctionLibraries
     * that require special setup
     * 
     * @return the complete Map of fixtureRegistrations to be used in tests executed
     *         with this TestAdapter.
     */
    @Override
    protected Map<ImportType, Map<String, Object>> getFixtureRegistrations() {
        return Map.of(ImportType.STATIC_FUNCTION_LIBRARY,
                Map.of("filter", FilterFunctionLibrary.class, "temporal", TemporalFunctionLibrary.class));
    }
}
