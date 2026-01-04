# SAPL Demo: OAuth 2.0 with JWT

This demo demonstrates how to integrate SAPL with OAuth 2.0 and JSON Web Tokens (JWT) for fine-grained authorization in a Spring Security application.

## Overview

The demo consists of three applications:

| Application              | Port | Description                                     |
|--------------------------|------|-------------------------------------------------|
| **Authorization Server** | 9000 | Spring Authorization Server issuing JWT tokens  |
| **Resource Server**      | 8090 | SAPL-protected REST API with JWT-based policies |
| **Client Application**   | 8080 | Web UI for OAuth 2.0 authorization code flow    |

Only the **Resource Server** uses SAPL. The other two applications provide the OAuth 2.0 infrastructure.

## What This Demo Demonstrates

- **JWT Claims in SAPL Policies**: Accessing token claims, scopes, and expiration in authorization decisions
- **Multiple JWT Access Patterns**: Different ways to access JWT data in policies
- **Token Validation**: Using SAPL's JWT Policy Information Point for token validation
- **Dynamic Policies**: Policies that react to token expiration in real-time

## Running the Demo

### Prerequisites

Add an alias for `auth-server` to your hosts file:

**Windows** (`C:\Windows\System32\drivers\etc\hosts`):
```
127.0.0.1 auth-server
```

**Linux/macOS** (`/etc/hosts`):
```
127.0.0.1 auth-server
```

### Start the Applications

Open three terminals and start each application in order:

**Terminal 1 - Authorization Server** (must start first):
```bash
cd oauth2-jwt-authorization-server
mvn spring-boot:run
```

**Terminal 2 - Resource Server** (after authorization server is ready):
```bash
cd oauth2-jwt-resource-server
mvn spring-boot:run
```

**Terminal 3 - Client Application**:
```bash
cd oauth2-jwt-client-application
mvn spring-boot:run
```

### Access the Demo

1. Open [http://localhost:8080](http://localhost:8080)
2. Login with username `user1` and password `password`
3. Authorize the client application to access your data
4. Browse the protected resources (books, faculty, bestiary)

## SAPL Integration Details

### Resource Server Configuration

The resource server enables SAPL method security and configures JWT validation:

```java
@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
public class WebSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

### Protected Endpoints

The controller uses `@PreEnforce` annotations to protect endpoints:

```java
@RestController
public class MiskatonicUniversityController {

    @GetMapping("/books")
    @PreEnforce(action = "'read'", resource = "'books'")
    public String[] books(Principal principal) {
        return new String[] { "Necronomicon", "Nameless Cults", "Book of Eibon" };
    }

    @GetMapping("/faculty")
    @PreEnforce(action = "'read'", resource = "'faculty'")
    public String[] getMessages() { ... }

    @GetMapping("/bestiary")
    @PreEnforce(action = "'read'", resource = "'bestiary'")
    public String[] getBestiary() { ... }
}
```

### JWT Access Patterns in Policies

The demo shows three ways to access JWT data in SAPL policies:

#### 1. OAuth2 Scopes as Spring Security Authorities

Spring Security maps JWT scopes to authorities with `SCOPE_` prefix:

```
policy "Scopes as Authority in Principal"
permit resource == "books"
where 
    "SCOPE_books.read" in subject..authority;
```

#### 2. Direct Access to Token Claims

Access claims directly from the serialized principal:

```
policy "Reading scopes from JWT claims"
permit resource == "faculty"
where
    "faculty.read" in subject.token.claims.scope;
```

#### 3. Parsing Raw JWT Token

Use the JWT function library to parse the raw token:

```
policy "Reading scopes from raw JWT"
permit resource == "bestiary"
where
    "bestiary.read" in jwt.parseJwt(subject.principal.tokenValue).payload.scope;
```

### Token Validation Policy

SAPL can validate tokens and create policies that react to token expiration:

```
policy "Policy with token timeout"
permit resource == "mysteries"
where
    subject.principal.tokenValue.<jwt.valid>;
```

This policy uses the `jwt.valid` attribute which dynamically tracks token validity over time.

## Project Structure

```
oauth2-jwt/
├── oauth2-jwt-authorization-server/   # OAuth2 Authorization Server (no SAPL)
├── oauth2-jwt-client-application/     # OAuth2 Client (no SAPL)
└── oauth2-jwt-resource-server/        # Resource Server with SAPL
    └── src/main/resources/policies/
        ├── pdp.json                   # PDP configuration
        └── jwt_based_policy_set.sapl  # JWT-based policies
```

## Dependencies (Resource Server)

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

## Configuration (Resource Server)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://auth-server:9000
```

The embedded PDP is enabled by default via `sapl-spring-boot-starter`.

## Acknowledgement

This demo is derived from the sample projects of the [Spring Authorization Server](https://github.com/spring-projects/spring-authorization-server).
