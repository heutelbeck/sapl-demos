# Demo: Filter Chain Authorization with SAPL in Spring WebFlux

This demonstration shows how to deploy a SAPL Policy Enforcement Point in the Spring Security filter chain of a reactive Spring WebFlux application. The PEP evaluates every HTTP exchange against SAPL policies and lets policy obligations shape both the request path and the response.

## Understanding Filter Chain Authorization

Spring Security supports two core authorization patterns: method-level security (annotations like `@PreAuthorize`) and filter chain authorization (URL-based access control). SAPL integrates with both. This demo shows the filter chain approach, where every HTTP exchange is evaluated by SAPL before reaching the controllers, and where the SAPL HTTP PEP web filter can additionally read or rewrite the request and response on the back of policy obligations. For details on how WebFlux security works, see the [Spring Security WebFlux documentation](https://docs.spring.io/spring-security/reference/reactive/index.html).

## What This Demo Shows

The application exposes four endpoints:

| Endpoint            | Authentication | Behaviour                                                                                                |
|---------------------|----------------|----------------------------------------------------------------------------------------------------------|
| `/public`           | None required  | Returns `Public information`.                                                                            |
| `/secret`           | Required       | Returns `Secret information`. The response carries an obligation-driven `X-Authorized-By: SAPL` header.  |
| `/echo-correlation` | Required       | Echoes the `X-Correlation-Id` header. The policy injects this header on permit, so the body always reads `demo-correlation-id`. |
| `/teapot`           | Required       | Always denied. The deny obligation shapes the response into HTTP 418 with a custom body.                 |

Each scenario maps to one of the SAPL HTTP signals the new starter ships: `DecisionSignal`, `HttpRequestMutationSignal`, `HttpResponseSignal`, `HttpDenialSignal`. The audit handler additionally fires on every decision via `DecisionSignal` and records what happened in an in-memory probe.

## Project Structure

```
webflux-authorizationmanager/
├── src/main/java/io/sapl/demo/webflux/
│   ├── WebfluxAuthorizationManagerDemoApplication.java
│   ├── SecurityConfiguration.java
│   ├── DemoController.java
│   └── handlers/
│       ├── AuditLogHandler.java        // DecisionSignal Consumer
│       ├── AuditProbe.java             // in-memory record for tests
│       ├── RequestHeaderHandler.java   // HttpRequestMutationSignal Consumer
│       ├── ResponseHeaderHandler.java  // HttpResponseSignal Consumer
│       └── DenyPageHandler.java        // HttpDenialSignal Consumer
├── src/main/resources/
│   └── policies/
│       └── filter_set.sapl
└── src/test/
    ├── java/.../HandlerScenariosTests.java
    └── ...
```

### The Security Configuration

The configuration applies SAPL to `ServerHttpSecurity` through the dedicated reactive configurer that the starter ships. One call wires the authorization manager, the HTTP PEP web filter, and the access-denied handler. The customizer parameter narrows the subscription to the three fields the demo policies reference (`subject..authority`, `action.method`, `resource.requestedURI`):

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ApplicationContext context,
            ObjectMapper mapper) {
        SaplServerHttpSecurityConfigurer.apply(http, context,
                c -> c.subscriptionFactory((auth, exchange) -> Mono.just(AuthorizationSubscription.of(auth,
                        Map.of("method", exchange.getRequest().getMethod().name()),
                        Map.of("requestedURI", exchange.getRequest().getURI().getPath()), mapper))));
        return http.formLogin(withDefaults()).httpBasic(withDefaults()).build();
    }
}
```

`SaplServerHttpSecurityConfigurer.apply(http, context, ...)` is `io.sapl.spring.pep.http.reactive.SaplServerHttpSecurityConfigurer.apply(...)`. The configurer pulls `ReactiveSaplAuthorizationManager`, `SaplServerAccessDeniedHandler`, and `SaplHttpPepWebFilter` from the application context.

The factory returns `Mono<AuthorizationSubscription>` so it can enrich the subscription asynchronously (resolving subject attributes from a reactive store, for example) without blocking the event loop. Synchronous customizations stay one-line via `Mono.just(...)`. To replace the factory globally instead, declare a single `@Bean ReactiveAuthorizationSubscriptionFactory` and the call collapses to `SaplServerHttpSecurityConfigurer.apply(http, context)`. To replace the manager outright, use `c.authorizationManager(...)`.

### The REST Controller

The endpoints are plain Spring WebFlux controllers. None of them know anything about SAPL:

```java
@RestController
@RequestMapping("/")
public class DemoController {

    @GetMapping(value = "/public", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> publicData() { return Mono.just("Public information"); }

    @GetMapping(value = "/secret", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> secretData() { return Mono.just("Secret information"); }

    @GetMapping(value = "/echo-correlation", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> echoCorrelation(@RequestHeader(name = "X-Correlation-Id", required = false) String id) {
        return Mono.just(id == null ? "no correlation id" : id);
    }

    @GetMapping(value = "/teapot", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> teapot() { return Mono.just("this should never be reached"); }
}
```

### The Constraint Handlers

Each handler is a Spring `@Component` that implements `ConstraintHandlerProvider`. The starter auto-discovers all such beans and feeds them to the enforcement planner.

For example, `RequestHeaderHandler` claims the `request-header` obligation and turns it into a header injection on the request:

```java
@Component
public class RequestHeaderHandler implements ConstraintHandlerProvider {
    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!ConstraintResponsibility.isResponsible(constraint, "request-header")) return List.of();
        if (!supportedSignals.contains(Signal.HttpRequestMutationSignal.TYPE))    return List.of();
        // read name + value from the obligation, return a Consumer<MutableHttpRequest>
        ...
    }
}
```

`ResponseHeaderHandler`, `DenyPageHandler`, and `AuditLogHandler` follow the same shape; they only differ in the signal they attach to and what the handler does with the value.

### The Policy

The policy file at `src/main/resources/policies/filter_set.sapl` uses the SAPL 4 syntax:

```sapl
set "filter_set"
priority deny or permit errors abstain

policy "audit_all"
permit
advice
    { "type": "audit:log" }

policy "deny_secret_anonymous"
deny
    action.method == "GET";
    "ROLE_ANONYMOUS" in subject..authority;
    resource.requestedURI == "/secret";
advice
    { "type": "audit:log" }

policy "stamp_secret_response"
permit
    action.method == "GET";
    resource.requestedURI == "/secret";
obligation
    { "type": "response-header", "name": "X-Authorized-By", "value": "SAPL" }

policy "tag_echo_request"
permit
    action.method == "GET";
    resource.requestedURI == "/echo-correlation";
obligation
    { "type": "request-header", "name": "X-Correlation-Id", "value": "demo-correlation-id" }

policy "deny_teapot_with_custom_page"
deny
    action.method == "GET";
    resource.requestedURI == "/teapot";
obligation
    { "type": "deny-page", "status": 418, "body": "I'm a teapot. Brew tea instead." }
advice
    { "type": "audit:log" }
```

The `priority deny or permit` combining algorithm collects obligations and advice from all policies that voted for the winning decision. The `audit:log` advice is therefore repeated on the deny policies so audit fires on both permits and denies; otherwise the audit advice from `audit_all` would be dropped whenever a deny policy wins.

## Running the Demo

You need Java 21 or later and Maven. From the demo directory:

```bash
cd webflux-authorizationmanager
mvn spring-boot:run
```

The server starts on port 8080.

## Exploring the Authorization Behaviour

### Public endpoint, no authentication

```bash
curl http://localhost:8080/public
```
`Public information`

### Anonymous deny on /secret returns 401

```bash
curl -i -H 'Accept: text/plain' http://localhost:8080/secret
```
`HTTP/1.1 401 Unauthorized` with a `WWW-Authenticate: Basic ...` header. The SAPL deny handler is not involved on the anonymous path.

### Permit on /secret carries the SAPL response header

```bash
curl -i -u user:user http://localhost:8080/secret
```
`HTTP/1.1 200 OK` with header `X-Authorized-By: SAPL` and body `Secret information`.

### Permit on /echo-correlation injects a header the controller observes

```bash
curl -u user:user http://localhost:8080/echo-correlation
```
`demo-correlation-id`. The client sent no `X-Correlation-Id`; the policy obligation injected it before the controller ran, and the controller echoes back what it sees.

### Authenticated deny on /teapot uses the custom deny page

```bash
curl -i -u user:user http://localhost:8080/teapot
```
`HTTP/1.1 418 ` with body `I'm a teapot. Brew tea instead.`

### Watching the audit log

The application logs every decision through the `AuditLogHandler` at INFO level. Each request above produces a line like:

```
INFO  i.s.demo.webflux.handlers.AuditLogHandler : SAPL audit: decision=PERMIT
```

## Validating Policies with the SAPL Test Language

The project includes policy unit tests in `.sapltest` files that run via `mvn test`:

```
requirement "Anonymous users are denied access to /secret" {

    given
        - document "filter_set"

    scenario "anonymous user accessing /secret is denied"
        when
            { "authorities": [{"authority": "ROLE_ANONYMOUS"}] }
        attempts
            { "method": "GET" }
        on { "requestedURI": "/secret" }
        expect deny;
}
```

Run them with:

```bash
mvn test
```

## Integrating SAPL in Your Application

Import the SAPL BOM and add the starter:

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

<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-pdp</artifactId>
</dependency>
```

The embedded PDP is enabled by default. Place your `.sapl` policy files in `src/main/resources/policies`. Apply SAPL to a `SecurityWebFilterChain` with `SaplServerHttpSecurityConfigurer.apply(http, context)`.

For decision visibility add to `application.properties`:

```properties
logging.level.io.sapl=DEBUG
io.sapl.pdp.embedded.print-json-report=true
```

## Related Demos

For the blocking servlet equivalent, see the `web-authorizationmanager` project. For method-level security with `@PreEnforce` and `@PostEnforce`, see the `webflux` project.
