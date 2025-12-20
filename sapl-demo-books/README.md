# Demo: Database Query Rewriting for Row-Level Security

## Conceptual Goal

This demo shows how SAPL can enforce row-level security by rewriting database queries based on user permissions. Each user has a dataScope, a list of book categories they are allowed to see. SAPL policies automatically inject this filter into JPA queries, so users only retrieve the data they are authorized to access.

### Why Query Rewriting?

Traditional approaches to row-level security often look like this:

```java
public List<Book> getBooks(User user) {
    List<Book> allBooks = repository.findAll();
    return allBooks.stream()
        .filter(book -> user.canAccess(book.getCategory()))
        .toList();
}
```

This approach has problems. It fetches all data from the database, then filters in memory, which is inefficient for large datasets. Authorization logic ends up scattered through service code. And it is easy to forget the filter when writing new code paths, creating security holes.

With SAPL query rewriting, the database query itself is modified to include the filter. Only authorized rows are ever fetched from the database. Authorization rules live in policies, completely separate from your application code.

## How to Run and Test

### Prerequisites

You will need Java 21 or later and Maven installed.

### Running the Demo

```bash
cd sapl-demo-books
mvn spring-boot:run
```

### Demo Users

The application comes with four pre-configured users. All passwords are password.

**admin** has an empty dataScope, which means no restrictions. This user sees all 6 books in the database.

**tom** has dataScope [1, 2, 3], so this user sees the 4 books that belong to categories 1, 2, or 3.

**sim** has dataScope [1, 2], limiting visibility to 3 books in categories 1 and 2.

**kat** has a null dataScope, which the policy interprets as no valid permissions. This user gets access denied.

### Testing

Open your browser and navigate to http://localhost:8080/

You will be prompted to log in. Try each user to see how the results change. Login as admin to see all 6 books. Login as tom to see only books in categories 1, 2, and 3. Login as sim to see only books in categories 1 and 2. Login as kat to see the access denied error.

To switch users, open a new private/incognito window or clear your browser session.

You can also run the automated tests:
```bash
mvn test
```

## Related Demos

For the same security scenario using SAPLs built-in R2DBC query manipulation (no custom constraint handler needed), see [sapl-demo-spring-data-r2dbc](../sapl-demo-spring-data-r2dbc). For the MongoDB equivalent, see [sapl-demo-spring-data-mongo-reactive](../sapl-demo-spring-data-mongo-reactive).
