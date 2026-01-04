# Using an Embedded SAPL Policy Decision Point

## Overview

The core of a system using Attribute-Based Access Control (ABAC) is the Policy Decision Point (PDP).

This demo shows how to manually construct and use an embedded PDP without any framework support like Spring Boot. It demonstrates:

- Building a PDP with the `PolicyDecisionPointBuilder` API
- Registering custom Policy Information Points (PIPs)
- Registering custom Function Libraries
- Blocking and reactive PDP invocation patterns
- Performance benchmarking

## Prerequisites

- JDK 21 or newer
- Maven

## Running the Demo

Build the demo:

```bash
cd embedded-pdp
mvn clean package
```

Run the executable JAR:

```bash
cd target
java -jar embedded-pdp-no-framework-4.0.0-SNAPSHOT-jar-with-dependencies.jar
```

### Command Line Options

```
Usage: sapl-demo-embedded [-fhV] [-p=<path>]

Options:
  -f, --filesystem    If set, policies and PDP configuration are loaded from
                      the filesystem instead of the bundled resources. Set
                      path with -p.
  -h, --help          Show this help message and exit.
  -p, --path=<path>   Sets the path for looking up policies and PDP
                      configuration if the -f parameter is set. Defaults to
                      '~/sapl/policies'
  -V, --version       Print version information and exit.
```

### Sample Output

```
[INFO] Loaded library 'echo' with 1 attributes
[INFO] Loading PDP configurations from classpath resources: '/policies'.
[INFO] Loaded 1 PDP configurations from resources.
[INFO]
[INFO] Demo Part 1: Accessing the PDP in a blocking manner using decideOnceBlocking()
[INFO] Decision for action 'read' : DENY
[INFO] Decision for action 'write': PERMIT
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 2: Accessing the PDP in a reactive manner using decideOnce().subscribe()
[INFO] Single reactive decision using decideOnce().subscribe()...
[INFO] Decision for action 'read': DENY
[INFO] Decision for action 'write': PERMIT
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 3: Perform a small benchmark for blocking decisions.
[INFO] Warming up for 20000 runs...
[INFO] Measure time for 20000 runs...
[INFO]
[INFO] Benchmark results for blocking PDP access:
[INFO] Runs  : 20000
[INFO] Total : 0,0931 s
[INFO] Avg.  : 0,0047 ms
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 4: Perform a small benchmark for sequential .take(1) decisions.
[INFO] Warming up for 20000 runs...
[INFO] Measure time for 20000 runs...
[INFO]
[INFO] Benchmark results for .take(1) access:
[INFO] Runs  : 20000
[INFO] Total : 0,064 s
[INFO] Avg.  : 0,0032 ms
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] End of demo.
```

## Key Concepts

### Building the PDP

The demo uses `PolicyDecisionPointBuilder` to construct the PDP:

```java
var components = PolicyDecisionPointBuilder.withDefaults()
        .withPolicyInformationPoint(new EchoPIP())
        .withFunctionLibrary(SimpleFunctionLibrary.class)
        .withResourcesSource()  // Load policies from classpath
        .build();

var pdp = components.pdp();
```

For filesystem-based policies:

```java
builder.withDirectorySource(Path.of("~/sapl/policies"));
```

### Custom Policy Information Point

The `EchoPIP` demonstrates a simple PIP that echoes its input:

```java
@PolicyInformationPoint(name = "echo", description = "PIP echoing the input value")
public class EchoPIP {

    @Attribute(name = "echo")
    public Flux<Value> echo(TextValue value) {
        return Flux.just(value);
    }
}
```

Used in policies as: `input.<echo.echo>`

### Custom Function Library

The `SimpleFunctionLibrary` provides custom functions:

```java
@FunctionLibrary(name = "simple", description = "some simple functions")
public class SimpleFunctionLibrary {

    @Function
    public static Value length(Value parameter) {
        return switch (parameter) {
            case ArrayValue array -> Value.of(array.size());
            case TextValue text -> Value.of(text.value().length());
            default -> Value.error("...");
        };
    }

    @Function
    public static Value append(Value... parameters) { ... }
}
```

Used in policies as: `simple.length(...)` or `simple.append(...)`

### Single vs. Streaming Decisions

The PDP offers two evaluation modes with different performance characteristics:

**Streaming decisions** via `decide(subscription)` return a `Flux<AuthorizationDecision>` designed for continuous authorization. The engine establishes reactive subscriptions to monitor policy and attribute changes, emitting updated decisions whenever relevant data changes. This is required for long-lived authorization contexts where decisions must adapt to changing conditions.

```java
pdp.decide(subscription)
    .subscribe(decision -> handleDecision(decision));
```

**Single decisions** via `decideOnceBlocking(subscription)` and `decideOnce(subscription)` are optimized for point-in-time authorization checks. When a policy evaluation does not require Policy Information Point (PIP) attribute lookups, the engine uses a "pure path" that evaluates the policy synchronously without reactive infrastructure overhead. If the policy does access PIP attributes, the engine transparently falls back to reactive evaluation internally to handle the asynchronous nature of attribute retrieval. The authorization semantics remain identical in both cases - only the performance characteristics differ. For policies that rely solely on the data present in the authorization subscription, the pure path avoids I/O and reactive scheduling overhead entirely.

```java
// Blocking - returns AuthorizationDecision directly
var decision = pdp.decideOnceBlocking(subscription);

// Non-blocking - returns Mono<AuthorizationDecision>
pdp.decideOnce(subscription)
    .subscribe(decision -> handleDecision(decision));
```

### Resource Cleanup

Always dispose of PDP components when done:

```java
components.dispose();
```

## Project Structure

```
embedded-pdp/
├── src/main/java/io/sapl/embedded/demo/
│   ├── EmbeddedPDPDemo.java       # Main demo application
│   ├── EchoPIP.java               # Custom Policy Information Point
│   └── SimpleFunctionLibrary.java # Custom Function Library
└── src/main/resources/policies/
    ├── pdp.json                   # PDP configuration
    ├── policy_1.sapl              # Policy for 'read' action (DENY)
    └── policy_2.sapl              # Policy for 'write' action (PERMIT)
```

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-pdp</artifactId>
</dependency>
```

Use the SAPL BOM for version management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.sapl</groupId>
            <artifactId>sapl-bom</artifactId>
            <version>4.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
