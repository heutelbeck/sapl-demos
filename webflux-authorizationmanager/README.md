# Demo: Filter Chain Authorization with SAPL in Spring WebFlux

This demonstration shows how to deploy a SAPL Policy Enforcement Point in the Spring Security filter chain of a reactive WebFlux application. The ReactiveSaplAuthorizationManager evaluates every HTTP exchange against SAPL policies before allowing it to proceed to your controllers.

## Understanding Reactive Filter Chain Authorization

Spring Security supports two core authorization patterns: method-level security (annotations like `@PreAuthorize`) and filter chain authorization (URL-based access control). SAPL integrates with both patterns. This demo shows the reactive filter chain approach, where every HTTP exchange is evaluated against SAPL policies before reaching your controllers. For details on how WebFlux security works, see the [Spring Security WebFlux documentation](https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html).

## What This Demo Shows

The application exposes two endpoints:

| Endpoint | Authentication | Behavior |
|----------|----------------|----------|
| `/public` | None required | Returns "Public information" |
| `/secret` | Required | Returns "Secret information" |

The SAPL policy uses a **permit-unless-deny** combining algorithm. Requests are allowed by default unless a policy explicitly denies them. A single policy named `deny_secret` blocks anonymous users from accessing `/secret`.

## Project Structure

```
webflux-authorizationmanager/
+-- src/main/java/io/sapl/demo/webflux/
|   +-- WebfluxAuthorizationManagerDemoApplication.java
|   +-- SecurityConfiguration.java
|   +-- DemoController.java
+-- src/main/resources/
|   +-- policies/
|       +-- filter_set.sapl
+-- src/test/
    +-- java/.../DemoControllerTests.java
    +-- resources/unit/filter_set.sapltest
```

### The Security Configuration

The configuration injects the `ReactiveSaplAuthorizationManager` provided by the SAPL Spring Security starter and wires it into the reactive filter chain:

```java
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ReactiveSaplAuthorizationManager saplAuthzManager;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(exchange ->
                        exchange.anyExchange()
                                .access(saplAuthzManager)
                    )
                   .formLogin(withDefaults())
                   .httpBasic(withDefaults())
                   .build();
    }
}
```

Every request passes through the `ReactiveSaplAuthorizationManager`, which constructs an authorization subscription from the exchange context and sends it to the PDP for evaluation.

### The Reactive Controller

The endpoints are simple Spring WebFlux controllers returning `Mono` with no security annotations:

```java
@RestController
@RequestMapping("/")
public class DemoController {

    @GetMapping(value = "/public", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> publicData() {
        return Mono.just("Public information");
    }

    @GetMapping(value = "/secret", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> secretData() {
        return Mono.just("Secret information");
    }
}
```

Authorization is handled entirely by the filter chain and SAPL policies.

### The Policy

The policy file at `src/main/resources/policies/filter_set.sapl` contains the authorization logic:

```sapl
set "filter_set"
permit-unless-deny

policy "deny_secret"
deny
    action.method == "GET"
where
    "ROLE_ANONYMOUS" in subject..authority;
    resource.contextPath == "/secret";
```

The policy set uses `permit-unless-deny`, meaning all requests are permitted unless explicitly denied. The `deny_secret` policy matches when:
- The HTTP method is GET
- The user has `ROLE_ANONYMOUS` authority (unauthenticated)
- The context path is `/secret`

## Running the Demo

### Prerequisites

You need Java 21 or later and Maven installed on your system.

### Starting the Application

Navigate to the demo directory and start the Spring Boot application:

```bash
cd webflux-authorizationmanager
mvn spring-boot:run
```

The server starts on port 8080 using Netty (the default WebFlux server).

## Exploring the Authorization Behavior

You can test the endpoints using either a browser or curl.

### Using curl

Access the public endpoint without authentication:

```bash
curl http://localhost:8080/public
```

Output: `Public information`

Try accessing the secret endpoint without authentication:

```bash
curl http://localhost:8080/secret
```

Output: HTTP 401 Unauthorized (the policy denies anonymous users)

Authenticate using basic auth with username `user` and password `user`:

```bash
curl -u user:user http://localhost:8080/secret
```

Output: `Secret information`

### Using a Browser

Navigate to `http://localhost:8080/public` and you see "Public information" immediately.

Navigate to `http://localhost:8080/secret` and the browser redirects you to a login form. Enter username `user` and password `user` to authenticate. After logging in, you see "Secret information".

Visit `http://localhost:8080/logout` to end your session and return to the anonymous state.

## Integrating SAPL in Your WebFlux Application

### Adding Dependencies

First, import the SAPL BOM (Bill of Materials) in your dependency management section:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.sapl</groupId>
            <artifactId>sapl-bom</artifactId>
            <version>${sapl.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then add the SAPL Spring Boot starter and embedded PDP:

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-pdp</artifactId>
</dependency>
```

### Configuring the Embedded PDP

The embedded PDP is enabled by default. Place your `.sapl` policy files in `src/main/resources/policies`.

For filter chain authorization, enable the policy enforcement filter in `application.properties`:

```properties
io.sapl.policyEnforcementFilter=true
```

For debugging, enable decision reporting:

```properties
logging.level.io.sapl=DEBUG
io.sapl.pdp.embedded.print-json-report=true
```

## Validating Policies with the SAPL Test Language

This demo includes policy unit tests in `.sapltest` files that verify the authorization behavior:

```
requirement "Anonymous users cannot access /secret" {

    given
        - document "filter_set"

    scenario "anonymous user denied GET /secret"
        when
            { "authority": [ { "authority": "ROLE_ANONYMOUS" } ] }
        attempts
            { "method": "GET" }
        on
            { "contextPath": "/secret" }
        expect deny;
}
```

Run the tests with Maven:

```bash
mvn test
```

## Observing Policy Decisions

The application is configured with debug logging for SAPL. Watch the console output as you make requests to see how the PDP evaluates each authorization subscription and arrives at its decision.

## Related Demos

For the servlet-based Web MVC equivalent, see the `web-authorizationmanager` project. For method-level security with `@PreEnforce` annotations in a reactive context, see the `webflux` project.
