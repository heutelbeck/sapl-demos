# SAPL Demo Testing

This demo is a sample repository for testing SAPL policies executed by [Maven](https://maven.apache.org/).

## Folder structure

```
.
|   pom.xml
|---- src
    |---- main     
        |---- java
        |---- resources
            |---- policies
                | policyDocument1.sapl
                | policyDocument2.sapl
                | ...
    |---- test     
        |---- java
            |---- unit
                | FirstTest.java
                | SecondTest.java
                | ...
            |---- integration
                | IntegrationTest.java
                | ...
        |---- resources
            | simplelogger.properties
```

It's using the default project structure of a [Maven](https://maven.apache.org/) project

## Executing the tests

The configuration of the test execution and the [sapl-maven-plugin](https://github.com/heutelbeck/sapl-policy-engine/sapl-maven-plugin) is in the `pom.xml`.

For a detailed description of the available parameters for the [sapl-maven-plugin](https://github.com/heutelbeck/sapl-policy-engine/sapl-maven-plugin) consult the [SAPL Docs](https://sapl.io/docs/sapl-reference.html#code-coverage-reports-via-the-sapl-maven-plugin)


Running the Maven "test" goal is executing the JUnit Tests containing the SAPL policy tests.

```
maven test
```

Running the Maven "verify" goal is executing, in addition, the [sapl-maven-plugin](https://github.com/heutelbeck/sapl-policy-engine/sapl-maven-plugin). The plugin generates the configured reports and breaks the maven lifecycle if the configured SAPL coverage criteria are not met.

```
maven verify
```


## Reviewing the results

    [INFO] --- sapl-maven-plugin:2.0.0:report-coverage-information (coverage) @ sapl-demo-testing ---
    [INFO]
    [INFO]
    [INFO] Measured SAPL coverage information:
    [INFO]
    [INFO] Policy Set Hit Ratio is: 100.0
    [INFO] Policy Hit Ratio is: 100.0
    [INFO] Policy Condition Hit Ratio is: 65.625
    [INFO]
    [INFO]
    [INFO] Open this file in a Browser to view the HTML coverage report:
    [INFO] file:///C:/Users/Nikolai/eclipse-sapl-workspace/sapl-demos/sapl-demo-testing/target/sapl-coverage/html/index.html
    [INFO]
    [INFO]
    [INFO] All coverage criteria passed
    [INFO]
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------


The [sapl-maven-plugin](https://github.com/heutelbeck/sapl-policy-engine/sapl-maven-plugin) will print the expected and actual ratio of the SAPL coverage criteria in the Maven log.

In addition, if you don't have disabled the generation of the HTML report, the path to the generated report is printed in the maven log too.
