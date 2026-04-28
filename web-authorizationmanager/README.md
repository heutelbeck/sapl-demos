# Demo: Filter Chain Authorization with SAPL in Spring Web

This demonstration shows how to deploy a SAPL Policy Enforcement Point in the Spring Security filter chain of a servlet-based Spring Web application. The PEP evaluates every HTTP request against SAPL policies and lets policy obligations shape both the request path and the response.

## Understanding Filter Chain Authorization

Spring Security supports two core authorization patterns: method-level security (annotations like `@PreAuthorize`) and filter chain authorization (URL-based access control). SAPL integrates with both. This demo shows the filter chain approach, where every HTTP request is evaluated by SAPL before reaching the controllers, and where the SAPL HTTP PEP filter can additionally read or rewrite the request and response on the back of policy obligations. For details on how servlet filters work, see the [Spring Security Architecture documentation](https://docs.spring.io/spring-security/reference/servlet/architecture.html).

## What This Demo Shows

The application exposes four endpoints:

| Endpoint            | Authentication | Behaviour                                                                                                |
|---------------------|----------------|----------------------------------------------------------------------------------------------------------|
| `/public`           | None required  | Returns `public information`.                                                                            |
| `/secret`           | Required       | Returns `secret information`. The response carries an obligation-driven `X-Authorized-By: SAPL` header.  |
| `/echo-correlation` | Required       | Echoes the `X-Correlation-Id` header. The policy injects this header on permit, so the body always reads `demo-correlation-id`. |
| `/teapot`           | Required       | Always denied. The deny obligation shapes the response into HTTP 418 with a custom body.                 |

Each scenario maps to one of the SAPL HTTP signals the new starter ships: `DecisionSignal`, `HttpRequestMutationSignal`, `HttpResponseSignal`, `HttpDenialSignal`. The audit handler additionally fires on every decision via `DecisionSignal` and records what happened in an in-memory probe.

## Project Structure

```
web-authorizationmanager/
├── src/main/java/io/sapl/demo/web/
│   ├── WebAuthorizationManagerDemoApplication.java
│   ├── SecurityConfiguration.java
│   ├── RestService.java
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

The configuration applies SAPL to `HttpSecurity` through the dedicated configurer that the starter ships. One call wires the authorization manager, the HTTP PEP filter, and the access-denied handler. The customizer parameter narrows the subscription to the three fields the demo policies reference (`subject..authority`, `action.method`, `resource.requestedURI`):

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {
        return http.with(saplHttp(), c -> c.subscriptionFactory(
                        (auth, request) -> AuthorizationSubscription.of(auth,
                                Map.of("method", request.getMethod()),
                                Map.of("requestedURI", request.getRequestURI()), mapper)))
                   .formLogin(withDefaults())
                   .httpBasic(withDefaults())
                   .build();
    }
}
```

`saplHttp()` is `io.sapl.spring.pep.http.servlet.SaplHttpSecurityConfigurer.saplHttp()`. The configurer pulls `SaplAuthorizationManager`, `SaplAccessDeniedHandler`, and `SaplHttpPepFilter` from the application context.

The `subscriptionFactory(...)` hook is optional. Without it, the default factory ships the entire serialized request which works but is verbose. To replace the factory globally instead, declare a single `@Bean AuthorizationSubscriptionFactory` and the configurer call collapses to `http.with(saplHttp(), withDefaults())`. To replace the manager outright, use `c.authorizationManager(...)`.

### The REST Service

The endpoints are plain Spring Web controllers. None of them know anything about SAPL:

```java
@RestController
@RequestMapping("/")
public class RestService {

    @GetMapping("public")           public String publicService()    { return "public information"; }
    @GetMapping("secret")           public String secretService()    { return "secret information"; }
    @GetMapping("echo-correlation") public String echoCorrelation(@RequestHeader(name = "X-Correlation-Id", required = false) String id) { return id == null ? "no correlation id" : id; }
    @GetMapping("teapot")           public String teapot()           { return "this should never be reached"; }
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
        if (!supportedSignals.contains(Signal.HttpRequestMutationSignal.SIGNAL_TYPE))    return List.of();
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
cd web-authorizationmanager
mvn spring-boot:run
```

The server starts on port 8080.

## Exploring the Authorization Behaviour

### Public endpoint, no authentication

```bash
curl http://localhost:8080/public
```
`public information`

### Anonymous deny falls back to Spring's authentication entry point

```bash
curl -i -H 'Accept: text/plain' http://localhost:8080/secret
```
`HTTP/1.1 302 Found` with a `Location: /login` header. The SAPL deny handler is not involved on the anonymous path.

### Permit on /secret carries the SAPL response header

```bash
curl -i -u user:user http://localhost:8080/secret
```
`HTTP/1.1 200 OK` with header `X-Authorized-By: SAPL` and body `secret information`.

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
INFO  i.s.demo.web.handlers.AuditLogHandler : SAPL audit: decision=PERMIT
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

The embedded PDP is enabled by default. Place your `.sapl` policy files in `src/main/resources/policies`. Apply SAPL to a `SecurityFilterChain` with `http.with(saplHttp(), withDefaults())`.

For decision visibility add to `application.properties`:

```properties
logging.level.io.sapl=DEBUG
io.sapl.pdp.embedded.print-json-report=true
```

## Related Demos

For the reactive WebFlux equivalent, see the `webflux-authorizationmanager` project. For method-level security with `@PreEnforce` and `@PostEnforce`, see the `webflux` project.
