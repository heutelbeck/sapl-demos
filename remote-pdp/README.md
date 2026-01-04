# Using a Remote SAPL Policy Decision Point

## Overview

This demo shows how to connect to a remote SAPL Policy Decision Point (PDP) server without any framework support like Spring Boot. It demonstrates:

- Connecting to a SAPL Server using HTTP or RSocket
- Single and multi-authorization subscriptions
- The `RemotePolicyDecisionPoint.builder()` API

## Prerequisites

- JDK 21 or newer
- Maven
- A running SAPL PDP server (e.g., [SAPL Server LT](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-server-lt))

## Running the Demo

### Start a PDP Server

First, start a SAPL Server LT instance. See the [SAPL Server LT documentation](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-server-lt) for instructions.

### Build the Demo

```bash
cd remote-pdp
mvn clean package
```

### Run the Demo

```bash
cd target
java -jar remote-pdp-4.0.0-SNAPSHOT-jar-with-dependencies.jar
```

By default, the demo connects to `https://localhost:8443` using the default SAPL Server LT demo credentials.

### Command Line Options

```
Usage: <main class> [-h=<host>] [-k=<clientKey>] [-s=<clientSecret>]

Options:
  -h, -host=<host>       Hostname of the policy decision point including prefix
                         and port. E.g. 'https://example.org:8443'.
  -k, -key=<clientKey>   Client key for the demo application, to be obtained
                         from the PDP administrator.
  -s, -secret=<clientSecret>
                         Client secret for the demo application, to be obtained
                         from the PDP administrator.
```

**Example with custom server:**
```bash
java -jar remote-pdp-4.0.0-SNAPSHOT-jar-with-dependencies.jar \
  -h=https://myserver:8443 \
  -k=myClientKey \
  -s=myClientSecret
```

**Using RSocket transport:**
```bash
java -jar remote-pdp-4.0.0-SNAPSHOT-jar-with-dependencies.jar \
  -h=rsocket://localhost:7000
```

### Sample Output (No Server Running)

```
[main] WARN io.sapl.pdp.remote.RemoteHttpPolicyDecisionPoint - ------------------------------------------------------------------
[main] WARN io.sapl.pdp.remote.RemoteHttpPolicyDecisionPoint - !!! ATTENTION: don't not use insecure sslContext in production !!!
[main] WARN io.sapl.pdp.remote.RemoteHttpPolicyDecisionPoint - ------------------------------------------------------------------
[main] INFO org.demo.RemotePDPDemo - Subscription: AuthorizationSubscription[subject="Willi", action="eat", resource="icecream", environment=undefined]
[main] INFO org.demo.RemotePDPDemo - Multi: MultiAuthorizationSubscription { ... }
[reactor-http-nio-2] ERROR io.sapl.pdp.remote.RemoteHttpPolicyDecisionPoint - Error : Connection refused: localhost/127.0.0.1:8443
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: IdentifiableAuthorizationDecision[subscriptionId=, decision=AuthorizationDecision[decision=INDETERMINATE, ...]]
```

### Sample Output (Server Running with Matching Policies)

```
[main] INFO org.demo.RemotePDPDemo - Subscription: AuthorizationSubscription[subject="Willi", action="eat", resource="icecream", environment=undefined]
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: IdentifiableAuthorizationDecision[subscriptionId=id-1, decision=AuthorizationDecision[decision=PERMIT, ...]]
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: IdentifiableAuthorizationDecision[subscriptionId=id-2, decision=AuthorizationDecision[decision=DENY, ...]]
```

## Key Concepts

### Building the Remote PDP Client

**HTTP Transport:**
```java
PolicyDecisionPoint pdp = RemotePolicyDecisionPoint.builder()
    .http()
    .baseUrl("https://localhost:8443")
    .basicAuth(clientKey, clientSecret)
    .withUnsecureSSL()  // Only for testing with self-signed certificates!
    .build();
```

**RSocket Transport:**
```java
PolicyDecisionPoint pdp = RemotePolicyDecisionPoint.builder()
    .rsocket()
    .host("localhost")
    .port(7000)
    .basicAuth(clientKey, clientSecret)
    .withUnsecureSSL()
    .build();
```

### Single Authorization Subscription

```java
var subscription = AuthorizationSubscription.of("Willi", "eat", "icecream");
pdp.decide(subscription)
    .doOnNext(decision -> log.info("Decision: {}", decision))
    .blockFirst();
```

### Multi-Authorization Subscription

Send multiple authorization requests in a single subscription:

```java
var multiSubscription = new MultiAuthorizationSubscription()
    .addAuthorizationSubscription("id-1", "bs@simpsons.com", "read",
        "file://example/med/record/patient/BartSimpson")
    .addAuthorizationSubscription("id-2", "ms@simpsons.com", "read",
        "file://example/med/record/patient/MaggieSimpson");

pdp.decide(multiSubscription)
    .doOnNext(decision -> log.info("Decision: {}", decision))
    .blockFirst();
```

Each decision includes the `subscriptionId` to correlate responses with requests.

### Continuous Decision Stream

For reactive scenarios where policies may change:

```java
pdp.decide(subscription)
    .subscribe(decision -> handleDecision(decision));
// Stream continues until cancelled or server terminates
```

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-pdp-remote</artifactId>
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

## Security Note

The demo uses `withUnsecureSSL()` to accept self-signed certificates for local testing. **Never use this in production!** For production deployments, configure proper SSL/TLS certificates.
