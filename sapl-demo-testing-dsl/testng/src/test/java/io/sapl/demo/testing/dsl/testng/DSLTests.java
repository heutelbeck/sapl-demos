package io.sapl.demo.testing.dsl.testng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.testng.annotations.Factory;

import io.sapl.test.SaplTestException;
import io.sapl.test.dsl.interfaces.TestNode;
import io.sapl.test.dsl.setup.BaseTestAdapter;
import io.sapl.test.dsl.setup.TestCase;
import io.sapl.test.dsl.setup.TestContainer;

public class DSLTests extends BaseTestAdapter<List<TestClass>> {

    @Factory
    public Object[] buildTests() {
        final var tests = new ArrayList<>(createTest("IdentifierDefinedTest", """
                test "policyWithSimpleFunction" {
                     scenario "test_policyWithSimpleFunction"
                     register
                        - library TemporalFunctionLibrary
                     when subject "willi" attempts action "read" on resource "something"
                     then expect permit;
                }"""));
        tests.addAll(createTest("test.sapltest"));
        return tests.toArray();
    }

    private List<TestClass> getDynamicContainersFromTestNode(List<? extends TestNode> testNodes) {
        if (testNodes == null) {
            return Collections.emptyList();
        }

        return testNodes.stream().flatMap(testNode -> {
            if (testNode instanceof TestCase testCase) {
                return Stream.of(new TestClass(testCase.getIdentifier(), testCase));
            } else if (testNode instanceof TestContainer testContainer) {
                return getDynamicContainersFromTestNode(testContainer.getTestNodes()).stream()
                        .map(testClass -> new TestClass(
                                testContainer.getIdentifier() + " -> " + testClass.getTestName(),
                                testClass.getRunnable()));
            }
            throw new SaplTestException("Unknown type of TestNode");
        }).toList();
    }

    @Override
    protected List<TestClass> convertTestContainerToTargetRepresentation(TestContainer testContainer) {
        return getDynamicContainersFromTestNode(testContainer.getTestNodes());
    }
}
