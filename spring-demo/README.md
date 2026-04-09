# Spring Boot Demo

Spring Boot WebFlux demo implementing the same 28 endpoints and passing the same test suite as the [Python](https://github.com/heutelbeck/sapl-python-demos), [NestJS](https://github.com/heutelbeck/sapl-nestjs-demo), and [.NET](https://github.com/heutelbeck/sapl-dotnet-demos) SAPL demos.

Same authorization behavior, any stack.

## What It Demonstrates

| Category | Endpoints | SAPL Feature |
|----------|-----------|--------------|
| Manual PDP | `GET /api/hello` | `pdp.decideOnce()` without annotations |
| PreEnforce / PostEnforce | `/api/patient/{id}`, `/api/patients`, `/api/transfer` | Declarative method-level enforcement |
| Content Filtering | `/api/constraints/patient`, `/api/constraints/patient-full` | Built-in `filterJsonContent` (blacken, delete, replace) |
| Constraint Handlers | `/api/constraints/logged`, `/audited`, `/redacted`, `/documents`, `/timestamped` | All 7 handler types |
| Error Pipeline | `/api/constraints/error-demo` | `ErrorHandlerProvider` + `ErrorMappingConstraintHandlerProvider` |
| Resource Replacement | `/api/constraints/resource-replaced` | Policy `transform` keyword |
| Advice vs Obligations | `/api/constraints/advised`, `/unhandled` | Best-effort vs mandatory constraints |
| Streaming (SSE) | `/api/streaming/heartbeat/*` | `@EnforceTillDenied`, `@EnforceDropWhileDenied`, `@EnforceRecoverableIfDenied` |
| JWT / ABAC | `/api/exportData/{pilotId}/{sequenceId}` | `<jwt.token>` PIP with `#authentication.token.tokenValue` |
| Service-Layer Enforcement | `/api/services/*` | `@PreEnforce` / `@PostEnforce` on service methods |

## Quick Start

```bash
docker compose up -d
mvn spring-boot:run
```

The app starts on port 3000 with an embedded PDP. Keycloak (port 8080) is only needed for the JWT export tests.

## Running Tests

The unified test suite lives in [sapl-python-demos](https://github.com/heutelbeck/sapl-python-demos):

```bash
# Without JWT tests
bash /path/to/sapl-python-demos/test_demo.sh http://localhost:3000

# With JWT tests (requires Keycloak running)
bash /path/to/sapl-python-demos/test_demo.sh http://localhost:3000 --jwt
```

Expected result: 28/28 passed.

## Project Structure

```
spring-demo/
  src/main/java/io/sapl/demo/spring/
    SpringDemoApplication.java        Entry point
    SecurityConfiguration.java        WebFlux security + @EnableReactiveSaplMethodSecurity
    domain/
      Patients.java                   Patient record + static data
      Documents.java                  Document record + static data
    controller/
      HelloController.java            Manual PDP access
      BasicController.java            @PreEnforce / @PostEnforce on controllers
      ConstraintsController.java      All constraint handler demos
      StreamingController.java        SSE streaming with deny/recover signals
      ExportController.java           JWT-based ABAC
      ServicesController.java         Delegates to enforced service methods
      GlobalExceptionHandler.java     Maps exceptions to HTTP status codes
    service/
      PatientService.java             @PreEnforce / @PostEnforce on service layer
      StreamingService.java           @EnforceTillDenied / @EnforceDropWhileDenied / @EnforceRecoverableIfDenied
    handler/
      LogAccessHandler.java           RunnableConstraintHandlerProvider
      AuditTrailHandler.java          ConsumerConstraintHandlerProvider
      RedactFieldsHandler.java        MappingConstraintHandlerProvider (field redaction)
      ClassificationFilterHandler.java MappingConstraintHandlerProvider (list filtering)
      InjectTimestampHandler.java     MethodInvocationConstraintHandlerProvider
      CapTransferHandler.java         MethodInvocationConstraintHandlerProvider
      NotifyOnErrorHandler.java       ErrorHandlerProvider
      EnrichErrorHandler.java         ErrorMappingConstraintHandlerProvider
  src/main/resources/
    application.yml                   Port 3000, embedded PDP config
    policies/                         23 SAPL policies + pdp.json (shared across all demos)
  keycloak/
    realm-export.json                 Demo realm with 4 test users
  docker-compose.yml                  Keycloak 26.1
```

## Policies

All 23 `.sapl` policy files are identical across the Python, NestJS, .NET, and Spring demos. The PDP uses `PRIORITY_DENY` combining algorithm with `DENY` default and `ABSTAIN` error handling.

## Keycloak Test Users

| Username | Role | pilotId | Password |
|----------|------|---------|----------|
| clinician1 | CLINICIAN | 1 | password |
| clinician2 | CLINICIAN | 2 | password |
| participant1 | PARTICIPANT | 1 | password |
| participant2 | PARTICIPANT | 2 | password |

## Dependencies

- Spring Boot 4.0.3 (WebFlux)
- SAPL 4.0.0 (`sapl-spring-boot-starter`)
- Spring Security OAuth2 Resource Server (JWT validation)
- Lombok
