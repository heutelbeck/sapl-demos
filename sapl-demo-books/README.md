# Demo: Database Query Rewriting for Row-Level Security

## Conceptual Goal

This demo shows how SAPL can enforce **row-level security** by rewriting database queries based on user permissions. Each user has a `dataScope`, a list of book categories they are allowed to see. SAPL policies automatically inject this filter into JPA queries, so users only retrieve the data they are authorized to access.

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

### Before You Start

If you are new to SAPL's argument modification feature, start with the simpler [sapl-demo-argumentchange](../sapl-demo-argumentchange) module. It demonstrates the same mechanism (obligation-based argument modification) with a minimal proof-of-concept. This demo applies that pattern to a realistic database access control scenario.

## How to Run and Test

### Prerequisites

You will need Java 21 or later and Maven installed.

### Running the Demo

```bash
cd sapl-demo-books
mvn spring-boot:run
```

### Demo Users

The application comes with four pre-configured users. All passwords are `password`.

**admin** has an empty dataScope, which means no restrictions. This user sees all 6 books in the database.

**tom** has dataScope `[1, 2, 3]`, so this user sees the 4 books that belong to categories 1, 2, or 3.

**sim** has dataScope `[1, 2]`, limiting visibility to 3 books in categories 1 and 2.

**kat** has a null dataScope, which the policy interprets as "no valid permissions." This user gets access denied.

### Testing

Open your browser and navigate to **http://localhost:8080/**

You will be prompted to log in. Try each user to see how the results change. Login as `admin` to see all 6 books. Login as `tom` to see only books in categories 1, 2, and 3. Login as `sim` to see only books in categories 1 and 2. Login as `kat` to see the access denied error.

To switch users, open a new private/incognito window or clear your browser session.

You can also run the automated tests:
```bash
mvn test
```

## Implementation Explained

### The Data Model

The Book entity is straightforward. It has an ID, a name, and a category field that we use for filtering:

```java
@Entity
public class Book {
    @Id
    private Long id;
    private String name;
    private Integer category;
}
```

The LibraryUser extends Spring Security's User class and adds authorization attributes. The `dataScope` field contains the list of categories this user is allowed to access:

```java
public class LibraryUser extends User implements UserDetails {
    private int department;
    private List<Integer> dataScope;
}
```

### The Repository

The repository method accepts an optional filter parameter that SAPL can populate:

```java
public interface BookRepository extends CrudRepository<Book, Long> {

    @PreEnforce
    @Query("SELECT book FROM Book book"
         + " WHERE :#{#filter == null} = true"
         + " OR book.category IN :filter")
    List<Book> findAll(@Param("filter") Optional<Collection<Integer>> filter);
}
```

The `@PreEnforce` annotation triggers SAPL policy evaluation before the query runs. The query itself handles two cases: if the filter is null or empty, it returns all books; if the filter has values, it returns only books with matching categories. The controller calls `findAll(Optional.empty())`, but SAPL can replace that argument with actual filter values.

### The Policy

```sapl
set "List and filter books - query modification with PreEnforce Example"

first-applicable

for action.java.name == "findAll"

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
    "limitCategoriesTo" : subject.principal.dataScope
}
```

This policy set uses the `first-applicable` combining algorithm, which evaluates policies in order and stops at the first match. The `for` clause scopes all policies to methods named `findAll`.

The first policy denies access when the user dataScope is null or undefined. This handles the `kat` user who has no valid permissions.

The second policy permits access without any obligation when dataScope is an empty list. This is the `admin` case, where an empty scope means "no restrictions."

The third policy catches everyone else. It permits access but attaches an obligation containing the user category list. This obligation tells the constraint handler what filter to apply.

### The Constraint Handler

The constraint handler translates the policy obligation into a method argument modification:

```java
@Service
public class EnforceCategoryFilteringConstraintHandlerProvider 
        implements MethodInvocationConstraintHandlerProvider {

    private static final String LIMIT_CATEGORIES = "limitCategoriesTo";

    @Override
    public boolean isResponsible(Value constraint) {
        // Handle obligations with "limitCategoriesTo" array
        return constraint instanceof ObjectValue ov 
            && ov.containsKey(LIMIT_CATEGORIES) 
            && ov.get(LIMIT_CATEGORIES) instanceof ArrayValue;
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        return methodInvocation -> {
            // Extract category list from obligation
            var categories = new ArrayList<Integer>();
            var constraintCategories = (ArrayValue) constraint.get(LIMIT_CATEGORIES);
            
            for (var category : constraintCategories) {
                if (category instanceof NumberValue num) {
                    categories.add(num.value().intValue());
                }
            }
            
            // Replace the method argument with the filter
            methodInvocation.setArguments(Optional.of(categories));
        };
    }
}
```

When a policy returns an obligation like `{"limitCategoriesTo": [1, 2, 3]}`, the handler first checks if it is responsible for this type of obligation by looking for the `limitCategoriesTo` key with an array value. Then it extracts the category numbers, wraps them in an Optional, and replaces the method argument. The repository query then executes with `WHERE category IN (1, 2, 3)`.

### Execution Flow

When a user requests the book list, the controller calls `repository.findAll(Optional.empty())`. The `@PreEnforce` annotation intercepts this call before it reaches the database.

SAPL evaluates the policy using the authenticated user principal, specifically checking the `dataScope` attribute. If dataScope is null, the request is denied and the user sees an error. If dataScope is empty, the request is permitted without modification, and all books are returned. If dataScope contains category numbers, the request is permitted with an obligation.

When there is an obligation, the constraint handler kicks in. It modifies the method argument from `Optional.empty()` to `Optional.of([1, 2])` or whatever categories the user has. The JPA query then executes with this filter, and only authorized books come back from the database.

## Key Takeaways

The filter is applied in SQL, not in Java, which makes this approach efficient for large datasets. The user `dataScope` attribute drives what they can access, keeping authorization data with the user. The policy expresses the logic cleanly: three simple rules handle denial, unrestricted access, and filtered access. The constraint handler bridges the gap between policy decisions and actual code changes. And most importantly, there is zero authorization code in the controller or repository. SAPL handles everything.
