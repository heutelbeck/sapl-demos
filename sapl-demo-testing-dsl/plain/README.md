# SAPL Demo Testing DSL Plain

This demo is a sample repository for testing SAPL policies using the SAPLTest DSL without a dedicated test framework.
This can be useful for productive runtime use case where a productive dependency to a test framework should be avoided.

The project uses the default project structure of a [Maven](https://maven.apache.org/) project

The Demo emulates a productive use case by providing the policies to test and the test definition via a 
[Storage](src/main/java/io/sapl/demo/testing/dsl/plain/storage) that is intended as a placeholder for any external data
source like a file server or Database instance.

- [PdpConfigurationStorage](src/main/java/io/sapl/demo/testing/dsl/plain/storage/PdpConfigurationStorage.java) contains data for the PDP configuration.
- [PolicyStorage](src/main/java/io/sapl/demo/testing/dsl/plain/storage/PolicyStorage.java) contains all available policy definitions.
- [TestStorage](src/main/java/io/sapl/demo/testing/dsl/plain/storage/TestStorage.java) contains all SAPLTest definitions to run.

Additionally, there a concrete Implementations to define the logic needed to attach the storage system. There are
contained in the [resolvers](src/main/java/io/sapl/demo/testing/dsl/plain/resolvers) folder. One resolver for unit test
setup and one for integration test setup. These are then passed to the concrete [TestAdapter](src/main/java/io/sapl/demo/testing/dsl/plain/TestAdapter.java)
instance to register the custom resolvers.

## Executing the tests

The [Main](src/main/java/io/sapl/demo/testing/dsl/plain/Main.java) class contains the code required to construct an 
instance of the [TestAdapter](src/main/java/io/sapl/demo/testing/dsl/plain/TestAdapter.java) and start the test execution.
The results are then printed to the console, in a productive scenario you could then react to failed test cases.
Access to the Policy Coverage information is also available via the [CoverageAPIFactory](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-coverage-api/src/main/java/io/sapl/test/coverage/api/CoverageAPIFactory.java)
The console output for a successful run with coverage information printed to the console is shown below:

    [main] INFO io.sapl.test.integration.InputStringVariablesAndCombinatorSource - Loading the PDP configuration from input string
    [main] INFO io.sapl.test.integration.InputStringVariablesAndCombinatorSource - Loading the PDP configuration from input string
    [main] INFO io.sapl.test.integration.InputStringVariablesAndCombinatorSource - Loading the PDP configuration from input string
    [main] INFO io.sapl.test.integration.InputStringVariablesAndCombinatorSource - Loading the PDP configuration from input string
    [main] INFO io.sapl.test.integration.InputStringVariablesAndCombinatorSource - Loading the PDP configuration from input string
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - All tests passed
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - policy hits
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policy_B
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policy_A
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - testSet||policySimple
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - policy set hits
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - testSet
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - policy condition hits
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policy_B||0||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policy_A||0||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming||0||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming||1||false
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming||2||false
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming||2||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - ||policyStreaming||1||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - testSet||policySimple||0||true
    [main] INFO io.sapl.demo.testing.dsl.plain.Main - testSet||policySimple||0||false
    
    Process finished with exit code 0

