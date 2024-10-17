package io.sapl.demo.testing.dsl.testng;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Arrays;
import org.testng.annotations.Factory;

import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.SaplTestException;
import io.sapl.test.dsl.interfaces.TestNode;
import io.sapl.test.dsl.setup.BaseTestAdapter;
import io.sapl.test.dsl.setup.TestCase;
import io.sapl.test.dsl.setup.TestContainer;
import io.sapl.test.grammar.sapltest.ImportType;

/**
 * Default TestAdapter that is used to execute the tests. needs to extend the
 * {@link BaseTestAdapter} with the specific Type that represents your target
 * Representation in this Case {@link TestClass}
 */
public class TestAdapter extends BaseTestAdapter<Stream<TestClass>> {

    /**
     * TestNG factory method to create tests dynamically
     * 
     * @return object array of created tests
     */
    @Factory
    public Object[] buildTests() {
        // find all sapltest files below src/test/resources
        final var dir = FileUtils.getFile("src/test/resources");

        final var paths = FileUtils.listFiles(dir, Arrays.array("sapltest"), true).stream()
                .map(file -> dir.toPath().relativize(file.toPath()).toString()).toList();

        // create tests for each file
        return paths.stream().flatMap(this::createTest).sorted(Comparator.comparing(TestClass::getTestName)).toArray();
    }

    /**
     * resolves a collection of TestNodes to stream of {@link TestClass} instances
     * 
     * @param testNodes the testNodes to resolve
     * @return stream of TestClass instances
     */
    private Stream<TestClass> getDynamicContainersFromTestNode(Collection<? extends TestNode> testNodes) {
        if (null == testNodes) {
            return Stream.empty();
        }

        return testNodes.stream().flatMap(testNode -> {
            if (testNode instanceof TestCase testCase) {
                return Stream.of(new TestClass(testCase.getIdentifier(), testCase));
            } else if (testNode instanceof TestContainer testContainer) {
                return convertTestContainerToTargetRepresentation(testContainer, false);
            }
            throw new SaplTestException("Unknown type of TestNode");
        });
    }

    /**
     * override that is called by the {@link BaseTestAdapter} to convert
     * TestContainer into a list of {@link TestClass} instances.
     * 
     * @param testContainer the TestContainer instance
     * @param shouldSetTestSourceUri not used for TestNG since there is no
     * testSourceUri
     * @return stream of the converted TestClass instances
     */
    @Override
    protected Stream<TestClass> convertTestContainerToTargetRepresentation(TestContainer testContainer,
            boolean shouldSetTestSourceUri) {
        final var identifier   = testContainer.getIdentifier();
        final var dynamicNodes = getDynamicContainersFromTestNode(testContainer.getTestNodes());

        // convert into TestClass instance with the top level identifier and arrow to
        // indicate nesting
        return dynamicNodes.map(
                testClass -> new TestClass(identifier + " -> " + testClass.getTestName(), testClass.getRunnable()));
    }

    /**
     * override this method to register FunctionLibraries and PIPs they are then
     * used in test execution when the test definition has an import of the
     * corresponding type Static registrations expect the Class type
     * (STATIC_FUNCTION_LIBRARY and STATIC_PIP) Non-Static registrations expect the
     * concrete object to be passed this enables using PIPs and FunctionLibraries
     * that require special setup see pipImport.sapltest and
     * staticFunctionLibraryImport.sapltest for an example how to import
     * src/test/resources/unit
     * 
     * @return the complete Map of fixtureRegistrations to be used in tests executed
     * with this TestAdapter.
     */
    @Override
    protected Map<ImportType, Map<String, Object>> getFixtureRegistrations() {
        return Map.of(ImportType.STATIC_FUNCTION_LIBRARY,
                Map.of("filter", FilterFunctionLibrary.class, "temporal", TemporalFunctionLibrary.class),
                ImportType.PIP, Map.of("upper", new TestPIP()));
    }
}
