package io.sapl.demo.testing.dsl.testng;

import org.testng.ITest;
import org.testng.annotations.Test;

/**
 * minimal running example for {@link ITest} implementation with a Runnable that
 * represents the test case
 */
public class TestClass implements ITest {

    private final String   name;
    private final Runnable runnable;

    public TestClass(String name, Runnable runnable) {
        this.name     = name;
        this.runnable = runnable;
    }

    @Test
    public void test() {
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public String getTestName() {
        return name;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
