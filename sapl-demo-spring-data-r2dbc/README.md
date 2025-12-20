# Demo: Spring Data R2DBC Query Manipulation

This demo shows SAPL's built-in query manipulation for Spring Data R2DBC repositories. Policies can automatically rewrite SQL queries to add WHERE conditions, control which columns are returned, and apply transformations to column values.

## How It Works

The `@QueryEnforce` annotation on repository methods triggers policy evaluation. When a policy permits access, it can include obligations that SAPL's R2DBC integration understands natively:

```sapl
obligation {
    "type": "r2dbcQueryManipulation",
    "conditions": [ "active = true" ],
    "selection": { "type": "blacklist", "columns": ["firstname"] },
    "transformations": { "lastname": "UPPER" }
}
```

This adds a WHERE clause, excludes the firstname column from results, and transforms lastname to uppercase. No custom Java constraint handler is needed.

## Prerequisites

Java 21 or later and Maven.

## Running the Demo

```bash
cd sapl-demo-spring-data-r2dbc
mvn spring-boot:run
```

The application uses an embedded H2 database with pre-loaded test data.

## Demo Users

Two users are configured:

- **admin** / admin (ROLE_ADMIN)
- **user** / user (ROLE_USER)

## Endpoints

Open your browser and navigate to any endpoint. You will be prompted to log in.

| Endpoint                                           | Description                                         |
|----------------------------------------------------|-----------------------------------------------------|
| http://localhost:8080/findAll                      | Returns all persons, filtered by policy             |
| http://localhost:8080/findAllByAgeAfter/25         | Returns persons older than the specified age        |
| http://localhost:8080/fetchingByQueryMethod/Lumpur | Returns persons whose city contains the search term |

## Query Manipulation Features

The policies in this demo show three capabilities:

**Conditions** add WHERE clauses to filter rows. For example, `"conditions": ["role = 'USER'"]` excludes admin records.

**Selection** controls which columns are returned. Use `"type": "blacklist"` to exclude specific columns or `"type": "whitelist"` to include only specific columns.

**Transformations** modify column values. For example, `"transformations": {"lastname": "UPPER"}` returns the lastname in uppercase.

## Related Demos

For a conceptual introduction to SAPL's argument modification feature, see [sapl-demo-argumentchange](../sapl-demo-argumentchange). For a simpler row-level security example using custom constraint handlers, see [sapl-demo-books](../sapl-demo-books).
