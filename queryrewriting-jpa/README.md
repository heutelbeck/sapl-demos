# Demo: Database Query Rewriting for Row-Level Security

This demonstration shows how SAPL enforces row-level security by rewriting database queries based on user permissions. Instead of fetching all data and filtering in application code, SAPL modifies the query itself so that only authorized rows are ever retrieved from the database.

## Understanding Query Rewriting

Traditional approaches to row-level security often involve fetching all records and filtering them in memory:

```java
public List<Book> getBooks(User user) {
    List<Book> allBooks = repository.findAll();
    return allBooks.stream()
        .filter(book -> user.canAccess(book.getCategory()))
        .toList();
}
```

This approach has a few problems. The database transfers more data than necessary, consuming bandwidth and memory. The authorization logic ends up scattered throughout the service layer rather than in one place. It is also easy to forget the filter when adding new code paths, which creates security holes.

Query rewriting works differently. The database query itself is modified to include authorization constraints before execution. The application developer writes a query with a placeholder for filter parameters, and SAPL injects the appropriate values based on the current user's permissions. The database performs the filtering, returning only the rows the user is authorized to see.

### How This Demo Implements Query Rewriting

This demo uses SAPL's method-level enforcement with a custom constraint handler that manipulates query parameters. The repository method accepts an optional filter parameter:

```java
@PreEnforce
@Query("SELECT book FROM Book book"
     + " WHERE :#{#filter == null} = true"
     + " OR book.category IN :filter")
List<Book> findAll(@Param("filter") Optional<Collection<Integer>> filter);
```

The SpEL expression in the query checks whether the filter is null. If null, all books are returned. If a filter is provided, only books whose category appears in the filter list are returned.

When the method is called, SAPL evaluates the applicable policy. If the policy permits access, it may include an obligation that specifies which categories the user can access. A custom constraint handler intercepts this obligation and injects the filter values into the method arguments before the query executes.

## The Domain Model

### Library Sections

The demo models a library catalog system where books are organized into numbered sections:

| Section | Category Name               |
|---------|-----------------------------|
| 1       | Children and Young Adult    |
| 2       | Science Fiction and Fantasy |
| 3       | Science and Technology      |
| 4       | Mystery and Thriller        |
| 5       | Classics and Literature     |

### The Book Collection

The library contains fifteen books distributed across these sections:

**Children and Young Adult (Section 1)**
The Phantom Tollbooth, A Wrinkle in Time, The Giver

**Science Fiction and Fantasy (Section 2)**
Neuromancer, Snow Crash, Elric of Melniboné

**Science and Technology (Section 3)**
Gödel, Escher, Bach, Amiga Hardware Reference Manual, Code: The Hidden Language

**Mystery and Thriller (Section 4)**
The Name of the Rose, The Maltese Falcon, And Then There Were None

**Classics and Literature (Section 5)**
Kafka on the Shore, Slaughterhouse-Five, The Master and Margarita

### Library Staff

Each staff member has a role that determines which library sections they can access:

**boss** is the head librarian with access to all sections (1, 2, 3, 4, 5). This user can see all fifteen books in the catalog.

**zoe** specializes in Children and SciFi materials with access to sections 1 and 2. This user can see six books from those two sections.

**bob** manages the Science and Mystery sections with access to sections 3 and 4. This user can see six books from those sections.

**ann** curates the Classics collection with access to section 5 only. This user can see three books from that section.

**pat** is a new intern who has not yet been assigned to any sections. The empty scope means this user is denied access entirely.

All users authenticate with the password "password".

## The Security Policy

### Requirements in Natural Language

The policy implements these authorization rules:

1. Staff members who have not been assigned to any library sections should be denied access to the book catalog. This protects against accidentally granting access to new staff before their permissions are properly configured.

2. Staff members with assigned sections should be permitted to view the catalog, but should only see books from their assigned sections. The system must communicate which sections to filter by.

3. The policy should only apply to catalog viewing operations. Other operations like saving or deleting books are not covered by this policy set and should result in a not-applicable decision, allowing other policies to handle them.

### SAPL Implementation

The policy set translates these requirements into SAPL:

```sapl
set "List and filter books - query modification with PreEnforce Example"

first-applicable

for action.java.name == "findAll"

policy "deny if scope null or empty"
deny
where
     subject.principal.dataScope in [null, undefined, []];

policy "enforce filtering"
permit
obligation {
    "limitCategoriesTo" : subject.principal.dataScope
}
```

The policy set header establishes that this set uses first-applicable combining, meaning the first policy whose conditions are met determines the outcome. The set only applies when the action is the findAll method.

The first policy explicitly denies access when the user's dataScope is null, undefined, or an empty array. This handles the case of users like pat who have no assigned sections.

The second policy permits access for all other cases and attaches an obligation. The obligation instructs the constraint handler to limit the query results to the categories in the user's dataScope.

## Using the Demo

### Prerequisites

You need Java 21 or later and Maven installed on your system. The demo uses an embedded H2 database, so no separate database installation is required.

### Starting the Application

Navigate to the demo directory and start the Spring Boot application:

```bash
cd queryrewriting-jpa
mvn spring-boot:run
```

The application starts an embedded H2 database and loads the demo data automatically.

### Accessing the Application

Open your browser and navigate to http://localhost:8080/

You will be presented with a login form. Enter one of the demo usernames with the password "password".

After logging in, you will see a list of books. The list is automatically filtered based on your user account's assigned library sections.

### Switching Users

To log out and try a different user, navigate to http://localhost:8080/logout and confirm the logout. You will be redirected to the login page where you can enter different credentials.

Alternatively, open a private browsing window to maintain multiple sessions simultaneously.

### What to Expect

When logged in as boss, you see all fifteen books because the head librarian has access to every section.

When logged in as zoe, you see six books from the Children and SciFi sections.

When logged in as bob, you see six books from Science and Mystery.

When logged in as ann, you see three classics.

When logged in as pat, you receive an access denied error because the intern has no assigned sections.

## Integrating SAPL in Your Application

### Adding Dependencies

First, import the SAPL BOM (Bill of Materials) in your dependency management section to align all SAPL dependency versions:

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
```

Then add the SAPL dependencies. You need the SAPL Spring Boot Starter which includes the embedded Policy Decision Point:

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
```

### Annotating Repository Methods

Mark repository methods that require policy enforcement with the @PreEnforce annotation:

```java
@PreEnforce
@Query("SELECT book FROM Book book"
     + " WHERE :#{#filter == null} = true"
     + " OR book.category IN :filter")
List<Book> findAll(@Param("filter") Optional<Collection<Integer>> filter);
```

The @PreEnforce annotation tells SAPL to evaluate applicable policies before allowing the method to execute. SAPL constructs an authorization subscription from the method context, including the authenticated user as the subject and the method details as the action.

### Implementing the Constraint Handler

SAPL uses constraint handlers to process obligations and advice from policy decisions. The sapl-spring-security module provides several handler interfaces for different use cases:

**MethodInvocationConstraintHandlerProvider** handles constraints before method execution. Use this to modify method arguments or perform setup actions. This demo uses this type to inject filter parameters.

**ResultConstraintHandlerProvider** handles constraints after method execution. Use this to filter or transform return values.

**ErrorMappingConstraintHandlerProvider** handles constraints when mapping errors. Use this to transform exceptions based on policy decisions.

**SubscriptionHandlerProvider** handles constraints for reactive streams. Use this to modify the subscription lifecycle.

**RunnableConstraintHandlerProvider** handles constraints that trigger side effects. Use this for logging, auditing, or other actions that do not modify the data flow.

For the full list of handler interfaces and their usage, see the sapl-spring-security module documentation.

This demo implements a MethodInvocationConstraintHandlerProvider to modify the filter parameter before the query executes:

```java
@Service
public class EnforceCategoryFilteringConstraintHandlerProvider
        implements MethodInvocationConstraintHandlerProvider {

    private static final String LIMIT_CATEGORIES = "limitCategoriesTo";

    @Override
    public boolean isResponsible(Value constraint) {
        return constraint instanceof ObjectValue ov
            && ov.containsKey(LIMIT_CATEGORIES)
            && ov.get(LIMIT_CATEGORIES) instanceof ArrayValue;
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        return methodInvocation -> {
            // Extract category IDs from the obligation
            // Set them as the filter parameter
            methodInvocation.setArguments(Optional.of(categories));
        };
    }
}
```

The isResponsible method checks whether this handler should process a given constraint. It returns true only for obligations containing the expected structure.

The getHandler method returns a consumer that modifies the method invocation. Here it extracts the allowed categories from the obligation and sets them as the filter parameter.

### Configuring the Embedded PDP

The embedded PDP is enabled by default and loads policies from `src/main/resources/policies`. No configuration is required for the default setup.

For custom configuration, you can set these properties in `application.yml`:

```yaml
io.sapl.pdp:
  embedded:
    pdp-config-type: RESOURCES
    policies-path: /policies
```

**io.sapl.pdp.embedded.pdp-config-type** determines where to load policies from. Use RESOURCES to load from the classpath (bundled in the JAR), DIRECTORY to load from a directory on disk, MULTI_DIRECTORY for multi-tenant subdirectories, or BUNDLES for multi-tenant .saplbundle files.

**io.sapl.pdp.embedded.policies-path** specifies the path containing .sapl policy files. For RESOURCES, this is relative to the classpath root.

**io.sapl.pdp.embedded.config-path** specifies the path containing the optional pdp.json configuration file, which can define the combining algorithm and variables.

For debugging, you can enable decision reporting:

```yaml
io.sapl.pdp:
  embedded:
    print-text-report: true
    print-json-report: true
    print-trace: true
```

### Placing Policies

Place your .sapl policy files in src/main/resources/policies. The embedded PDP discovers all files with the .sapl extension in this directory.

## Validating Policies with the SAPL Test Language

The SAPL test language provides a declarative way to verify that policies behave as expected. Tests are written in .sapltest files and executed as JUnit tests through an adapter class.

### Writing Readable Policy Tests

Policy tests read like specifications. Each test file contains requirements, and each requirement contains scenarios that verify specific behaviors:

```
requirement "Policy Set should deny access when dataScope is null, undefined, or empty" {

    given
        - document "book_listing_set"

    scenario "deny when dataScope is null"
        when
            { "principal": { "dataScope": null } }
        attempts
            { "java": { "name": "findAll" } }
        on "books"
        expect deny;

    scenario "deny when dataScope is empty array"
        when
            { "principal": { "dataScope": [] } }
        attempts
            { "java": { "name": "findAll" } }
        on "books"
        expect deny;
}
```

This requirement verifies the denial rule. The first scenario tests that a user whose dataScope is null receives a deny decision. The second scenario confirms the same for an empty array.

### Testing Permitted Access with Obligations

When testing permit decisions, you can verify that the correct obligations are attached:

```
requirement "Policy Set should permit access with filtering obligation when dataScope has values" {

    given
        - document "book_listing_set"

    scenario "permit boss with all sections and obligation"
        when
            { "principal": { "dataScope": [1, 2, 3, 4, 5] } }
        attempts
            { "java": { "name": "findAll" } }
        on "books"
        expect decision is permit, with obligation equals {
            "limitCategoriesTo": [1, 2, 3, 4, 5]
        };

    scenario "permit ann with section 5 only"
        when
            { "principal": { "dataScope": [5] } }
        attempts
            { "java": { "name": "findAll" } }
        on "books"
        expect decision is permit, with obligation equals {
            "limitCategoriesTo": [5]
        };
}
```

These scenarios verify that the head librarian receives an obligation containing all five sections, while the classics curator receives an obligation containing only section 5.

### Verifying Policy Scope

Policies should only match their intended actions. These scenarios confirm that the policy set returns not-applicable for operations it should not govern:

```
requirement "Policy Set should not apply to other actions" {

    given
        - document "book_listing_set"

    scenario "not-applicable for delete action"
        when
            { "principal": { "dataScope": [1, 2, 3] } }
        attempts
            { "java": { "name": "delete" } }
        on "books"
        expect not-applicable;

    scenario "not-applicable for save action"
        when
            { "principal": { "dataScope": [1, 2, 3] } }
        attempts
            { "java": { "name": "save" } }
        on "books"
        expect not-applicable;
}
```

### Running Policy Tests

Create an adapter class that extends JUnitTestAdapter:

```java
public class SaplTests extends JUnitTestAdapter {
}
```

Place your .sapltest files in src/test/resources. The adapter automatically discovers and executes them as JUnit dynamic tests:

```bash
mvn test
```

The test output shows each requirement and scenario, making it easy to understand what behaviors are being verified.

## Related Demos

For the same security scenario using SAPL's built-in reactive SQL (R2DBC) query manipulation without a custom constraint handler, see the queryrewriting-sql-reactive project. For the reactive MongoDB equivalent, see queryrewriting-mongodb-reactive.
