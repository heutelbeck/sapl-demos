# Demo: Spring Data MongoDB Reactive Query Manipulation

This demonstration shows how SAPL enforces row-level security with Spring Data MongoDB using built-in query manipulation. Policies automatically rewrite MongoDB queries to add filter conditions that restrict which documents users can access. Like the R2DBC demo, this uses SAPL's native database integration that handles query manipulation automatically without requiring custom constraint handlers.

## Understanding MongoDB Query Manipulation

SAPL's Spring Data MongoDB integration intercepts repository method calls and rewrites queries based on policy decisions. When a policy permits access, it can attach an obligation containing MongoDB Query Language (MQL) conditions. SAPL's infrastructure automatically combines these conditions with the original query before execution.

With this approach, you do not need to write custom constraint handler code. The obligation format uses standard MQL syntax that MongoDB developers already know. The query manipulation happens at the repository level.

### How Query Manipulation Works

When you call a repository method annotated with @QueryEnforce, SAPL evaluates the applicable policies. If the decision is permit with an obligation containing MongoDB conditions, SAPL modifies the query to include those conditions.

For example, if the original query is an empty filter:

```javascript
{}
```

And the policy obligation specifies the condition to filter by categories 1 and 2, SAPL rewrites the query to:

```javascript
{ "category": { "$in": [1, 2] } }
```

This filtering happens at the database level. MongoDB returns only the documents matching the combined query, ensuring unauthorized documents are never transferred to the application.

### The MongoDB Obligation Format

MongoDB query manipulation uses a specific obligation structure:

```json
{
    "type": "mongoQueryManipulation",
    "conditions": [ "{ \"category\": { \"$in\": [1, 2] } }" ]
}
```

The type field identifies this as a MongoDB query manipulation obligation. The conditions array contains one or more MQL filter expressions as strings. Multiple conditions are combined with a logical AND.

## The Domain Model

### Library Sections

The demo models a library catalog system where books are organized into numbered sections:

| Section | Category Name |
|---------|---------------|
| 1 | Children and Young Adult |
| 2 | Science Fiction and Fantasy |
| 3 | Science and Technology |
| 4 | Mystery and Thriller |
| 5 | Classics and Literature |

### The Book Collection

The library contains fifteen books distributed across these sections:

**Children and Young Adult (Section 1)**
The Phantom Tollbooth, A Wrinkle in Time, The Giver

**Science Fiction and Fantasy (Section 2)**
Neuromancer, Snow Crash, Elric of Melnibone

**Science and Technology (Section 3)**
Godel Escher Bach, Amiga Hardware Reference Manual, Code: The Hidden Language

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

2. Staff members with assigned sections should be permitted to view the catalog, but should only see books from their assigned sections. The policy must generate the appropriate MongoDB filter conditions to restrict the query results.

3. The policy should only apply to the findAll operation on the book repository. Other operations should result in a not-applicable decision, allowing other policies to handle them.

### SAPL Implementation

The policy set translates these requirements into SAPL:

```sapl
set "List and filter books - MongoDB query manipulation"

first-applicable

for action == "findAll"

policy "deny if scope null or empty"
deny
where
     subject.principal.dataScope in [null, undefined, []];

policy "enforce filtering"
permit
obligation {
    "type"       : "mongoQueryManipulation",
    "conditions" : [ "{ \"category\" : { \"$in\" : " + subject.principal.dataScope + " } }" ]
}
```

The policy set header establishes that this set uses first-applicable combining, meaning the first policy whose conditions are met determines the outcome. The set only applies when the action is "findAll".

The first policy explicitly denies access when the user's dataScope is null, undefined, or an empty array. This handles the case of users like pat who have no assigned sections.

The second policy permits access for all other cases and attaches an obligation. The obligation uses the mongoQueryManipulation type and constructs an MQL condition that filters documents by their category field. The $in operator matches any document whose category appears in the user's dataScope array.

## Using the Demo

### Prerequisites

You need Java 21 or later, Maven, and Docker installed on your system. The demo uses Testcontainers to run MongoDB automatically.

### Starting the Application

Navigate to the demo directory and start the Spring Boot application:

```bash
cd sapl-demo-spring-data-mongo-reactive
mvn spring-boot:run
```

The application starts a MongoDB container via Testcontainers and loads the demo data automatically.

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

Then add the SAPL dependencies. You need sapl-spring-security for the Spring Security integration and sapl-pdp for the embedded Policy Decision Point:

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-security</artifactId>
</dependency>
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-pdp</artifactId>
</dependency>
```

### Annotating Repository Methods

Mark repository methods that require policy enforcement with the @QueryEnforce annotation:

```java
@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, Long> {

    @QueryEnforce(subject = "#authentication", action = "findAll")
    @Query("{}")
    Flux<Book> findAllBooks();
}
```

The @QueryEnforce annotation tells SAPL to evaluate applicable policies before allowing the method to execute. The subject parameter specifies that the current authentication should be used as the subject. The action parameter defines the action name that policies will match against.

The @Query annotation provides the base MongoDB query. An empty object means "select all documents" before any policy conditions are applied. SAPL will add filter conditions based on the policy decision.

### How the Authorization Subscription is Built

When the findAllBooks method is called, SAPL extracts the authentication specified by the subject parameter and uses it as the subject in the authorization subscription. The action is set to the literal string "findAll" as specified in the annotation. The resource defaults to the repository's entity type.

Note that MongoDB uses a different action format than R2DBC. The action is specified as a plain string rather than a Java method descriptor object. Your policies must match this format accordingly.

### Configuring the Embedded PDP

Enable the embedded Policy Decision Point in your application.properties:

```properties
io.sapl.pdp.embedded.enabled=true
io.sapl.pdp.embedded.pdp-config-type=RESOURCES
io.sapl.pdp.embedded.policies-path=/policies
```

The key properties are:

**io.sapl.pdp.embedded.enabled** activates the embedded PDP. Set to true for applications that bundle policies in their resources.

**io.sapl.pdp.embedded.pdp-config-type** determines where to load policies from. Use RESOURCES to load from the classpath (bundled in the JAR) or FILESYSTEM to load from a directory on disk.

**io.sapl.pdp.embedded.policies-path** specifies the path containing .sapl policy files. For RESOURCES, this is relative to the classpath root.

For debugging, enable decision reporting:

```properties
io.sapl.pdp.embedded.print-text-report=true
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
        attempts "findAll"
        on "books"
        expect deny;

    scenario "deny when dataScope is empty array"
        when
            { "principal": { "dataScope": [] } }
        attempts "findAll"
        on "books"
        expect deny;
}
```

This requirement verifies the denial rule. The first scenario tests that a user whose dataScope is null receives a deny decision. The second scenario confirms the same for an empty array. Notice that the action is specified as a simple string "findAll" rather than a Java method descriptor, matching how MongoDB actions are represented.

### Testing MongoDB Query Manipulation Obligations

When testing permit decisions with MongoDB query manipulation, you can verify that the obligation has the correct structure:

```
requirement "Policy Set should permit access with MongoDB query manipulation obligation" {

    given
        - document "book_listing_set"

    scenario "permit boss with all sections"
        when
            { "principal": { "dataScope": [1, 2, 3, 4, 5] } }
        attempts "findAll"
        on "books"
        expect decision is permit,
            with obligation containing key "type"
            with value matching text "mongoQueryManipulation";

    scenario "permit zoe with sections 1 and 2"
        when
            { "principal": { "dataScope": [1, 2] } }
        attempts "findAll"
        on "books"
        expect decision is permit,
            with obligation containing key "type"
            with value matching text "mongoQueryManipulation";

    scenario "obligation has conditions array"
        when
            { "principal": { "dataScope": [1, 2] } }
        attempts "findAll"
        on "books"
        expect decision is permit,
            with obligation containing key "conditions";
}
```

These scenarios verify that the obligation contains the required mongoQueryManipulation type and includes a conditions array. The test uses partial matching to verify key structural elements without asserting the exact MQL string, which makes the tests more resilient to formatting changes.

### Verifying Policy Scope

Policies should only match their intended actions. These scenarios confirm that the policy set returns not-applicable for operations it should not govern:

```
requirement "Policy Set should not apply to other actions" {

    given
        - document "book_listing_set"

    scenario "not-applicable for delete action"
        when
            { "principal": { "dataScope": [1, 2, 3] } }
        attempts "delete"
        on "books"
        expect not-applicable;

    scenario "not-applicable for save action"
        when
            { "principal": { "dataScope": [1, 2, 3] } }
        attempts "save"
        on "books"
        expect not-applicable;

    scenario "not-applicable for java action object format"
        when
            { "principal": { "dataScope": [1, 2, 3] } }
        attempts
            { "java": { "name": "findAll" } }
        on "books"
        expect not-applicable;
}
```

The third scenario is worth noting. It verifies that the policy only matches the string action format "findAll" and returns not-applicable for the Java method descriptor format. This confirms the policy is correctly scoped to MongoDB's action representation.

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

For the same security scenario using JPA with a custom constraint handler that manipulates method arguments, see the sapl-demo-books project. For the R2DBC/SQL equivalent using SAPL's native R2DBC query manipulation, see sapl-demo-spring-data-r2dbc.
