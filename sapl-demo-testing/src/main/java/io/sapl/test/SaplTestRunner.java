package io.sapl.test;


import io.sapl.test.setup.Runner;

public class SaplTestRunner {
    public static void main(String[] args) {
        Runner.run(new TestPIP());
    }
}
