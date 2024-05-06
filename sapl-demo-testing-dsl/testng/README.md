# SAPL Demo Testing DSL TestNG

This demo is a sample repository for testing SAPL policies using the SAPLTest DSL with the TestNG test framework.

It's using the default project structure of a [Maven](https://maven.apache.org/) project

All policies that should be tested are contained in corresponding [resources](src/main/resources) folder.
The Test Definitions are then located in the test [resources](src/test/resources) folder.

## Executing the tests

The central entry point for test Execution is the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/testng/TestAdapter.java)
which extends the [BaseTestAdapter](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-test/src/main/java/io/sapl/test/dsl/setup/BaseTestAdapter.java)
contained in the [sapl-test](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-test) module.

### IDE Support

In case you are using a modern IDE like Eclipse or IntelliJ IDEA that supports the TestNG Plugin, you can just navigate to
the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/testng/TestAdapter.java) and should see a run button next to the
class name, you can use to run the tests. A normal TestNG Configuration will also see the [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/testng/TestAdapter.java)
and merge it with other discovered test cases.

### Maven Support

See the configuration in the [POM](pom.xml).
There a dependency to the [sapl-test](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-test) module is 
added, to be able to access the [BaseTestAdapter](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-test/src/main/java/io/sapl/test/dsl/setup/BaseTestAdapter.java). Additionally, there is some special configuration for the Maven
Surefire Plugin to print the full test name to the console. currently there is no Plugin for TestNG that achieves
the same pretty printing as shown for [JUnit](../junit/README.md). So an arrow (->) is used to indicate the nesting.

Running the Maven "test" goal in the project root, is executing all tests defined by files with a `.sapltest` extension 
below the test [resources](src/test/resources) folder.

```
mvn test
```

the output shows in console:

    [INFO] -------------------------------------------------------
    [INFO]  T E S T S
    [INFO] -------------------------------------------------------
    [INFO] Running TestSuite
    [INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.193 s -- in TestSuite
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\attributeMocking.sapltest -> Policy with complex PIP should only permit read when pip.attributeWithParams emits true -> willi tries to read something with specific parent value and exact parameter matchers attribute mocking -- Time elapsed: 0.789 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\streaming.sapltest -> ROLE_DOCTOR can access when secondOf time.Now is larger than 4 -> subject ROLE_DOCTOR can access 2 times with attribute timing -- Time elapsed: 0.033 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\functionMocking.sapltest -> Policy Simple should grant read access for willi on something -> willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource -- Time elapsed: 0.019 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\streaming.sapltest -> ROLE_DOCTOR can access when secondOf time.Now is larger than 4 -> subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking -- Time elapsed: 0.007 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\attributeMocking.sapltest -> Policy with simple PIP should permit read for subject.<test.upper> equal to WILLI -> willi tries to read something with specific parent value attribute mocking -- Time elapsed: 0.007 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\pipImport.sapltest -> uses uppercase PIP to decide -> klaus can read if there is a FOO value with subject as key in AuthorizationSubscription -- Time elapsed: 0.005 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\streaming.sapltest -> ROLE_DOCTOR can access when secondOf time.Now is larger than 4 -> subject ROLE_DOCTOR can access 3 times without attribute timing and with time.secondOf mocking mocking with times called verification -- Time elapsed: 0.005 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\singleRequirement.sapltest -> Policy Simple should grant read access for willi on something -> willi tries to read something -- Time elapsed: 0.012 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\streaming.sapltest -> ROLE_DOCTOR can access when secondOf time.Now is larger than 4 -> subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking with multiple values -- Time elapsed: 0.005 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.integration\policySetLoading.sapltest -> should grant read access for WILLI on foo depending on policy set and PDP Config -> WILLI tries to read foo using concrete policies definition with default pdp configuration -- Time elapsed: 0.054 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\attributeMocking.sapltest -> Policy with simple PIP should permit read for subject.<test.upper> equal to WILLI -> willi tries to read something with PIP returning error -- Time elapsed: 0.006 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.integration\policySetLoading.sapltest -> should grant read access for WILLI on foo depending on policy set and PDP Config -> WILLI tries to read foo using single identifier and variable override -- Time elapsed: 0.109 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\staticFunctionLibraryImport.sapltest -> blacken from FunctionLibrary is applied -> willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource non exact match -- Time elapsed: 0.032 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\multipleRequirementsAndCentralGiven.sapltest -> Policy Simple should deny write access for willi on something -> not_willi tries to write something -- Time elapsed: 0.003 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\streaming.sapltest -> ROLE_DOCTOR can access when secondOf time.Now is larger than 4 -> subject ROLE_DOCTOR can access 2 times without attribute timing -- Time elapsed: 0.005 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\pipImport.sapltest -> uses uppercase PIP to decide -> klaus can not read using actual uppercase PIP -- Time elapsed: 0.004 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\attributeMocking.sapltest -> Policy with simple PIP should permit read for subject.<test.upper> equal to WILLI -> willi tries to read something with simple attribute mocking -- Time elapsed: 0.003 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.integration\policySetLoading.sapltest -> should grant read access for WILLI on foo depending on policy set and PDP Config -> WILLI tries to read foo using single identifier and variable + combining algorithm override -- Time elapsed: 0.015 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\attributeMocking.sapltest -> Policy with complex PIP should only permit read when pip.attributeWithParams emits true -> willi tries to read something with specific parent value and mixed parameter matchers attribute mocking -- Time elapsed: 0.007 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\pipImport.sapltest -> uses uppercase PIP to decide -> klaus can read if there is a BAR value with subject as key in environment -- Time elapsed: 0.003 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\singleRequirement.sapltest -> Policy Simple should grant read access for willi on something -> not_willi tries to read something -- Time elapsed: 0.004 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\multipleRequirementsAndCentralGiven.sapltest -> Policy Simple should grant read access for willi on something -> willi tries to read something -- Time elapsed: 0.003 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.integration\policySetLoading.sapltest -> should grant read access for WILLI on foo depending on policy set and PDP Config -> WILLI tries to read foo using concrete policies definition with concrete pdp configuration -- Time elapsed: 0.008 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.integration\policySetLoading.sapltest -> should grant read access for WILLI on foo depending on policy set and PDP Config -> WILLI tries to read foo using single identifier -- Time elapsed: 0.010 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\multipleRequirementsAndCentralGiven.sapltest -> Policy Simple should grant read access for willi on something -> not_willi tries to read something -- Time elapsed: 0.002 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\pipImport.sapltest -> uses uppercase PIP to decide -> willi can read using actual uppercase PIP -- Time elapsed: 0.002 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\staticFunctionLibraryImport.sapltest -> blacken from FunctionLibrary is applied -> willi with authority ROLE_ADMIN can execute action with java.name=findById on complex resource with obligation and transformed resource -- Time elapsed: 0.003 s
    [INFO] io.sapl.demo.testing.dsl.testng.TestClass.unit\multipleRequirementsAndCentralGiven.sapltest -> Policy Simple should deny write access for willi on something -> willi tries to write something -- Time elapsed: 0.001 s
    [INFO]
    [INFO] Results:
    [INFO]
    [INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  7.042 s
    [INFO] Finished at: 2024-05-06T21:23:35+02:00

