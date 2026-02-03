# SAPL Testing with the SAPLTest DSL (JUnit Integration)

This demo shows how to test SAPL policies using the SAPLTest DSL with JUnit.

## Project Structure

```
test-dsl-junit/
├── src/main/resources/
│   ├── policies/           # Policies under test
│   └── policiesIT/         # Policies for integration tests
├── src/test/java/
│   └── io/sapl/demo/testing/dsl/junit/
│       ├── TestAdapter.java   # JUnit test adapter (required)
│       └── TestPIP.java       # Custom PIP for tests
└── src/test/resources/
    ├── unit/               # Unit test definitions (.sapltest)
    └── integration/        # Integration test definitions (.sapltest)
```

## Running the Tests

```bash
mvn test
```

## How It Works

A class extending `JUnitTestAdapter` must be present in the test sources for JUnit to discover and execute `.sapltest` files as tests. The [TestAdapter](src/test/java/io/sapl/demo/testing/dsl/junit/TestAdapter.java) serves this purpose and also registers custom PIPs and function libraries:

```java
public class TestAdapter extends JUnitTestAdapter {
    @Override
    protected Map<ImportType, Map<String, Object>> getFixtureRegistrations() {
        return Map.of(
            ImportType.STATIC_FUNCTION_LIBRARY,
            Map.of("filter", FilterFunctionLibrary.class, 
                   "temporal", TemporalFunctionLibrary.class),
            ImportType.PIP, 
            Map.of("upper", new TestPIP()));
    }
}
```

## Policy Coverage

The `sapl-maven-plugin` collects policy coverage during test execution and generates reports. It can enforce minimum coverage thresholds and fail the build if they are not met.

After running `mvn verify`, the HTML coverage report is available at:

```
target/sapl-coverage/html/index.html
```

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-test-junit</artifactId>
    <scope>test</scope>
</dependency>
```
