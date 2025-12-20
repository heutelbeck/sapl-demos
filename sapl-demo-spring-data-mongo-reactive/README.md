# Demo: Spring Data MongoDB Reactive Query Manipulation

This demo shows SAPL's built-in query manipulation for reactive MongoDB repositories. Policies can automatically rewrite MongoDB queries to add filter conditions and control which fields are returned.

## How It Works

The `@QueryEnforce` annotation on repository methods triggers policy evaluation. When a policy permits access, it can include obligations that SAPL's MongoDB integration understands natively:

```sapl
obligation {
    "type": "mongoQueryManipulation",
    "conditions": [ "{'active': {'$eq': true}}" ],
    "selection": { "type": "blacklist", "columns": ["firstname"] }
}
```

This adds a MongoDB filter condition and excludes the firstname field from results. No custom Java constraint handler is needed.

## Prerequisites

Java 21 or later and Maven.

## Running the Demo

```bash
cd sapl-demo-spring-data-mongo-reactive
mvn spring-boot:run
```

The application uses Flapdoodle embedded MongoDB with pre-loaded test data.

## Demo Users

Two users are configured:

- **admin** / admin (ROLE_ADMIN)
- **user** / user (ROLE_USER)

## Endpoints

Open your browser and navigate to any endpoint. You will be prompted to log in.

| Endpoint                                        | Description                                           |
|-------------------------------------------------|-------------------------------------------------------|
| http://localhost:8080/findAll                   | Returns all users, filtered by policy                 |
| http://localhost:8080/findAllByAgeAfter/25      | Returns users older than the specified age            |
| http://localhost:8080/fetchingByQueryMethod/son | Returns users whose lastname contains the search term |

## Query Manipulation Features

The policies in this demo show two capabilities:

**Conditions** add MongoDB query filters. For example, `"conditions": ["{'role': {'$eq': 'USER'}}"]` excludes admin records. The condition syntax follows MongoDB query syntax.

**Selection** controls which fields are returned. Use `"type": "blacklist"` to exclude specific fields or `"type": "whitelist"` to include only specific fields.

## Related Demos

For a conceptual introduction to SAPL's argument modification feature, see [sapl-demo-argumentchange](../sapl-demo-argumentchange). For a simpler row-level security example using custom constraint handlers, see [sapl-demo-books](../sapl-demo-books). For the SQL/R2DBC equivalent of this demo, see [sapl-demo-spring-data-r2dbc](../sapl-demo-spring-data-r2dbc).
