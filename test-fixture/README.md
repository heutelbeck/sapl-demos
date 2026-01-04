# SAPL Testing with Java Test Fixture

This demo shows how to test SAPL policies using the `SaplTestFixture` fluent API in Java.

## Project Structure

```
test-fixture/
├── src/main/resources/
│   ├── policies/           # Policies under test
│   └── policiesIT/         # Policies for integration tests
└── src/test/java/
    └── io/sapl/test/
        ├── APolicySimpleTest.java
        ├── BPolicyWithSimpleFunctionTest.java
        ├── CPolicyWithSimplePIPTest.java
        ├── ...
        └── TestPIP.java
```

## Running the Tests

```bash
mvn test
```

## How It Works

Tests use the `SaplTestFixture` fluent API. For single policy tests:

```java
@Test
void whenSubjectIsWilliAndActionIsRead_thenPermit() {
    SaplTestFixture.createSingleTest()
        .withPolicyFromResource("/policies/policySimple.sapl")
        .whenDecide(AuthorizationSubscription.of("willi", "read", "document"))
        .expectPermit()
        .verify();
}
```

For integration tests with multiple policies:

```java
@Test
void whenEvaluatingCombinedPolicies_thenPermit() {
    SaplTestFixture.createIntegrationTest()
        .withConfigurationFromResources("policiesIT")
        .whenDecide(AuthorizationSubscription.of("WILLI", "read", "foo"))
        .expectPermit()
        .verify();
}
```

Mocking PIPs and functions:

```java
@Test
void whenMockingAttribute_thenPermit() {
    SaplTestFixture.createSingleTest()
        .withPolicyFromResource(POLICY)
        .givenAttribute("mock", "test.upper", any(), args(), Value.of("WILLI"))
        .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
        .expectPermit()
        .verify();
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
    <artifactId>sapl-test</artifactId>
    <scope>test</scope>
</dependency>
```
