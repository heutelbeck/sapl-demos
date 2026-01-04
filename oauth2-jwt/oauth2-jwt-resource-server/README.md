# OAuth 2.0 Resource Server with SAPL

This module demonstrates SAPL integration with OAuth 2.0 JWT-based resource protection.

**This is the only module in the demo that uses SAPL.** It shows how to:
- Access JWT claims in SAPL policies
- Use OAuth2 scopes for authorization decisions
- Validate JWT tokens dynamically in policies

For complete demo instructions and policy examples, see the [parent README](../README.md).

## Quick Start

```bash
mvn spring-boot:run
```

The server runs on port **8090**.

## Protected Endpoints

| Endpoint    | Required Scope  | Description                |
|-------------|-----------------|----------------------------|
| `/books`    | `books.read`    | List of forbidden tomes    |
| `/faculty`  | `faculty.read`  | University faculty members |
| `/bestiary` | `bestiary.read` | Creature compendium        |
