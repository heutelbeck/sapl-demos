# SAPL Demo Testing DSL Plain

This demo shows how to execute SAPL tests programmatically using the `PlainTestAdapter` without JUnit or any test framework.

This is useful for:
- PAP (Policy Administration Point) web applications that need to run tests
- CI/CD pipelines with custom test execution
- Scenarios where tests are stored in databases instead of files
- Cases where reactive progress events are needed for UI updates

## How It Works

The demo loads policies and test definitions from resources and executes them using `PlainTestAdapter`:

```java
// Create configuration with policies and tests
var config = TestConfiguration.builder()
    .withSaplDocuments(policies)        // List of SaplDocument
    .withSaplTestDocuments(tests)       // List of SaplTestDocument
    .withDefaultAlgorithm(CombiningAlgorithm.DENY_OVERRIDES)
    .build();

// Execute with PlainTestAdapter
var adapter = new PlainTestAdapter();

// Synchronous execution
var results = adapter.execute(config);
System.out.println("All passed: " + results.allPassed());

// Or reactive execution with progress events
adapter.executeReactive(config).subscribe(event -> {
    if (event instanceof ScenarioCompleted sc) {
        System.out.println("Completed: " + sc.result().fullName());
    }
});
```

## Key Classes

- `SaplDocument` - A SAPL policy with id, name, and source code
- `SaplTestDocument` - A test definition with id, name, and source code
- `TestConfiguration` - Configuration builder for test execution
- `PlainTestAdapter` - Executes tests and returns results
- `PlainTestResults` - Aggregated test results with pass/fail counts
- `ScenarioResult` - Individual scenario result with status and duration
- `TestEvent` - Events emitted during reactive execution

## Running the Demo

```bash
mvn compile exec:java
```

Expected output:

```
=== SAPL PlainTestAdapter Demo ===

Loaded 7 policies and 2 test documents

--- Executing Tests ---

[PASS] Policy Simple should grant read access for willi on something > willi tries to read something
[PASS] Policy Simple should grant read access for willi on something > not_willi tries to read something
[PASS] Policy Simple should grant read access for willi on something > willi reads with complex subscription
[PASS] Function mocking with simple function policy > mock dayOfWeek to return MONDAY
...

--- Results ---
Total: 23  Passed: 23  Failed: 0  Errors: 0

All tests passed!
```

## PAP Integration Pattern

For a PAP web application, you would:

1. Load policies and tests from the database
2. Create `SaplDocument` and `SaplTestDocument` instances with DB IDs
3. Execute with `PlainTestAdapter.executeReactive()`
4. Push `ScenarioCompleted` events via WebSocket/SSE for live UI updates
5. Map results back to DB IDs for storage

```java
// Pseudocode for PAP integration
var policies = policyRepository.findAll().stream()
    .map(p -> new SaplDocument(p.getId(), p.getName(), p.getSource()))
    .toList();

var tests = testRepository.findAll().stream()
    .map(t -> new SaplTestDocument(t.getId(), t.getName(), t.getSource()))
    .toList();

adapter.executeReactive(config)
    .subscribe(event -> webSocketSession.send(event));
```
