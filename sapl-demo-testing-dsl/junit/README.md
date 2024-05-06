# SAPL Demo Testing DSL Junit

This demo is a sample repository for testing SAPL policies using the SAPLTest DSL with the JUnit test framework.

It's using the default project structure of a [Maven](https://maven.apache.org/) project

All policies that should be tested are contained in corresponding [resources](src/main/resources) folder.
The Test Definitions are then located in the test [resources](src/test/resources) folder.

## Executing the tests

The central entry point for test Execution is the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/junit/TestAdapter.java)
which extends the [JUnitTestAdapter](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-test-junit/src/main/java/io/sapl/test/junit/JUnitTestAdapter.java)
contained in the [sapl-test-junit](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-test-junit) module.

### IDE Support

In case you are using a modern IDE like Eclipse or IntelliJ IDEA that supports the Junit5 Plugin, you can just navigate to
the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/junit/TestAdapter.java) and should see a run button next to the
class name, you can use to run the tests. A normal JUnit Configuration will also see the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/junit/TestAdapter.java)
and merge it with other discovered test cases.

### Maven Support

See the configuration in the [POM](pom.xml).
There a dependency to the sapl-test-junit module is added, to be able to access the [JUnitTestAdapter](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-test-junit/src/main/java/io/sapl/test/junit/JUnitTestAdapter.java).
Additionally, there is some special configuration for the Maven Surefire Plugin to support pretty printing of nested
test cases using a specific plugin.

Running the Maven "test" goal in the project root, is executing all tests defined by files with a `.sapltest` extension 
below the test [resources](src/test/resources) folder.

```
mvn test
```

the pretty printed output looks like this:

    [INFO] -------------------------------------------------------
    [INFO]  T E S T S
    [INFO] -------------------------------------------------------
    [INFO] +--should grant read access for WILLI on foo depending on policy set and PDP Config - 3.052 ss
    [INFO] |  +-- [OK] WILLI tries to read foo using single identifier - 0.762 ss
    [INFO] |  +-- [OK] WILLI tries to read foo using concrete policies definition with default pdp configuration - 0.010 ss
    [INFO] |  +-- [OK] WILLI tries to read foo using concrete policies definition with concrete pdp configuration - 0.012 ss
    [INFO] |  +-- [OK] WILLI tries to read foo using single identifier and variable override - 0.017 ss
    [INFO] |  '-- [OK] WILLI tries to read foo using single identifier and variable + combining algorithm override - 0.020 ss
    [INFO] +--Policy with simple PIP should permit read for subject.<test.upper> equal to WILLI - 3.052 ss
    [INFO] |  +-- [OK] willi tries to read something with simple attribute mocking - 0.109 ss
    [INFO] |  +-- [OK] willi tries to read something with specific parent value attribute mocking - 0.026 ss
    [INFO] |  '-- [OK] willi tries to read something with PIP returning error - 0.004 ss
    [INFO] +--Policy with complex PIP should only permit read when pip.attributeWithParams emits true - 3.052 ss
    [INFO] |  +-- [OK] willi tries to read something with specific parent value and exact parameter matchers attribute mocking - 0.013 ss
    [INFO] |  '-- [OK] willi tries to read something with specific parent value and mixed parameter matchers attribute mocking - 0.007 ss
    [INFO] +--Policy Simple should grant read access for willi on something - 3.052 ss
    [INFO] |  +-- [OK] willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource - 0.020 ss
    [INFO] |  +-- [OK] willi tries to read something - 0.009 ss
    [INFO] |  +-- [OK] not_willi tries to read something - 0.003 ss
    [INFO] +--Policy Simple should deny write access for willi on something - 3.052 ss
    [INFO] |  +-- [OK] willi tries to write something - 0.002 ss
    [INFO] |  '-- [OK] not_willi tries to write something - 0.003 ss
    [INFO] +--uses uppercase PIP to decide - 3.052 ss
    [INFO] |  +-- [OK] willi can read using actual uppercase PIP - 0.010 ss
    [INFO] |  +-- [OK] klaus can not read using actual uppercase PIP - 0.005 ss
    [INFO] |  +-- [OK] klaus can read if there is a BAR value with subject as key in environment - 0.004 ss
    [INFO] |  '-- [OK] klaus can read if there is a FOO value with subject as key in AuthorizationSubscription - 0.004 ss
    [INFO] |  +-- [OK] willi tries to read something - 0.004 ss
    [INFO] |  '-- [OK] not_willi tries to read something - 0.005 ss
    [INFO] +--blacken from FunctionLibrary is applied - 3.052 ss
    [INFO] |  +-- [OK] willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource - 0.004 ss
    [INFO] |  '-- [OK] willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource non exact match - 0.016 ss
    [INFO] +--ROLE_DOCTOR can access when secondOf time.Now is larger than 4 - 3.052 ss
    [INFO] |  +-- [OK] subject ROLE_DOCTOR can access 2 times without attribute timing - 0.013 ss
    [INFO] |  +-- [OK] subject ROLE_DOCTOR can access 2 times with attribute timing - 0.016 ss
    [INFO] |  +-- [OK] subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking - 0.007 ss
    [INFO] |  +-- [OK] subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking with multiple values - 0.006 ss
    [INFO] |  '-- [OK] subject ROLE_DOCTOR can access 3 times without attribute timing and with time.secondOf mocking mocking with times called verification - 0.005 ss
    [INFO]
    [INFO] Results:
    [INFO]
    [INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  7.627 s
    [INFO] Finished at: 2024-05-06T21:07:47+02:00

