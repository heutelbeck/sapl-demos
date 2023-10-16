package io.sapl.test;

import io.sapl.test.dsl.setup.TestBuilder;
import java.util.Collection;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class DynamicTests {
    @TestFactory
    Collection<DynamicTest> saplDslTests() {
        return TestBuilder.buildTests(new TestPIP(), "test.sapltest");
    }
}
