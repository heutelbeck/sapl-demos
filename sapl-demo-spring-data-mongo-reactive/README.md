# Demo: Spring Data MongoDB Reactive Query Manipulation

This demo shows how SAPL can enforce row-level security with Spring Data MongoDB using built-in query manipulation. Policies automatically rewrite MongoDB queries to add filter conditions, restricting which documents users can access. This demo implements the same security scenario as [sapl-demo-books](../sapl-demo-books), but uses SAPL's native MongoDB support instead of a custom constraint handler.

## How It Works

Each user has a dataScope attribute containing the book categories they can access. When a user requests the book list, SAPL evaluates a policy and, if access is permitted, attaches an obligation that SAPL's MongoDB integration processes automatically:

```sapl
obligation {
    "type": "mongoQueryManipulation",
    "conditions": [ "{ 'category': { '': [1,2,3] } }" ]
}
```

SAPL rewrites the MongoDB query to include this filter. No custom Java code is needed to handle the obligation.

## Prerequisites

Java 21 or later and Maven.

## Running the Demo

```bash
cd sapl-demo-spring-data-mongo-reactive
mvn spring-boot:run
```

The application uses Flapdoodle embedded MongoDB with pre-loaded book data.

## Demo Users

The application comes with four pre-configured users. All passwords are password.

**admin** has an empty dataScope, which means no restrictions. This user sees all 6 books in the database.

**tom** has dataScope [1, 2, 3], so this user sees the 4 books that belong to categories 1, 2, or 3.

**sim** has dataScope [1, 2], limiting visibility to 3 books in categories 1 and 2.

**kat** has a null dataScope, which the policy interprets as no valid permissions. This user gets access denied.

## Testing

Open your browser and navigate to http://localhost:8080/

You will be prompted to log in. Try each user to see how the results change.

To switch users, open a new private/incognito window or clear your browser session.

You can also run the automated tests:
```bash
mvn test
```

## Implementation Details

### The Repository

The repository uses @QueryEnforce to trigger SAPL evaluation:

```java
@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, Long> {

    @QueryEnforce(action = "'findAll'")
    @Query("{}")
    Flux<Book> findAllBooks();
}
```

### The Policy

```sapl
set "List and filter books - MongoDB query manipulation"

first-applicable

for action == "findAll"

policy "deny if scope null"
deny
where
  subject.principal.dataScope in [null, undefined];

policy "empty scope means no limit"
permit
where
  subject.principal.dataScope == [];

policy "enforce filtering"
permit
obligation {
    "type": "mongoQueryManipulation",
    "conditions": [ "{ 'category': { '': " + subject.principal.dataScope + " } }" ]
}
```

The policy works like this: if the user dataScope is null, access is denied. If dataScope is empty, all books are returned. Otherwise, the MongoDB query is rewritten to filter by the allowed categories.

## Related Demos

For the same scenario using JPA with a custom constraint handler, see [sapl-demo-books](../sapl-demo-books). For the R2DBC/SQL equivalent, see [sapl-demo-spring-data-r2dbc](../sapl-demo-spring-data-r2dbc).
