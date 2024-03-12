package io.sapl.demo.testing.dsl.plain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.sapl.test.SaplTestException;
import io.sapl.test.dsl.interfaces.TestNode;
import io.sapl.test.dsl.setup.BaseTestAdapter;
import io.sapl.test.dsl.setup.TestCase;
import io.sapl.test.dsl.setup.TestContainer;

public class TestAdapter extends BaseTestAdapter<List<TestCase>> {
    @Override
    protected List<TestCase> convertTestContainerToTargetRepresentation(TestContainer testContainer) {
        return getDynamicContainersFromTestNode(testContainer.getTestNodes());
    }

    private List<TestCase> getDynamicContainersFromTestNode(Collection<? extends TestNode> testNodes) {
        if (testNodes == null) {
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

    public Map<String, Throwable> executeTests(String filename) {
        final var tests = createTest(filename);
        return runTests(tests);
    }

    public Map<String, Throwable> executeTests(String identifier, String input) {
        final var tests = createTest(identifier, input);
        return runTests(tests);
    }
}
