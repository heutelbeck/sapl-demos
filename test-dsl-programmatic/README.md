# SAPL Testing with PlainTestAdapter (Programmatic Execution)

This demo shows how to execute SAPL tests programmatically using `PlainTestAdapter`.

## Project Structure

```
test-dsl-programmatic/
├── src/main/resources/
│   ├── policies/           # Policies under test
│   ├── policiesIT/         # Policies for integration tests
│   └── unit/               # Test definitions (.sapltest)
└── src/test/java/
    └── io/sapl/demo/testing/dsl/plain/
        └── PlainTestAdapterTests.java
```

## Running the Tests

```bash
mvn test
```

## How It Works

The [PlainTestAdapterTests](src/test/java/io/sapl/demo/testing/dsl/plain/PlainTestAdapterTests.java) demonstrates loading policies and test definitions from resources, then executing them with `PlainTestAdapter`:

```java
// Load policies and test definitions
var policies = loadPolicies();  // List<SaplDocument>
var tests = loadTests();        // List<SaplTestDocument>

// Create configuration
var config = TestConfiguration.builder()
    .withSaplDocuments(policies)
    .withSaplTestDocuments(tests)
    .withDefaultAlgorithm(CombiningAlgorithm.DENY_OVERRIDES)
    .build();

// Execute with reactive progress events
var adapter = new PlainTestAdapter();
adapter.executeReactive(config)
    .doOnNext(event -> {
        if (event instanceof ScenarioCompleted(ScenarioResult result)) {
            log.info("[{}] {}", result.status(), result.fullName());
        }
    })
    .blockLast();
```

This approach is useful for scenarios where tests are loaded dynamically (e.g., from a database) or when reactive progress events are needed.

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
    <artifactId>sapl-test</artifactId>
</dependency>
```
