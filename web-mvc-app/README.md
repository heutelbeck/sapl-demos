# Demo: Spring MVC Application with Method-Level Security

This demo showcases a complete Spring Boot MVC application demonstrating SAPL's method-level security annotations, custom Policy Information Points (PIPs), constraint handlers, and content filtering.

## What This Demo Demonstrates

- **Method-Level Security**: Using `@PreEnforce` and `@PostEnforce` annotations for fine-grained access control
- **Custom Policy Information Points**: Accessing database data during policy evaluation
- **Constraint Handlers**: Implementing obligations and advice (logging, email notifications)
- **Content Filtering**: Transforming response data based on user roles (blackening, field removal)
- **Complex Authorization Scenarios**: Role-based access with "breaking the glass" emergency override

## Running the Demo

```bash
mvn spring-boot:run
```

Access the application at [http://localhost:8080](http://localhost:8080).

### Test Users

Login with any of the following users (password is always `password`):

| User      | Role(s)           | Description                    |
|-----------|-------------------|--------------------------------|
| Dominic   | DOCTOR            | Attending doctor               |
| Julia     | DOCTOR            | Doctor                         |
| Peter     | NURSE             | Nurse                          |
| Alina     | NURSE             | Nurse                          |
| Thomas    | ADMIN             | Administrator                  |
| Brigitte  | ADMIN             | Administrator                  |
| Janosch   | VISITOR           | Visitor (relative of patient)  |
| Janina    | VISITOR           | Visitor                        |
| Horst     | VISITOR           | Visitor                        |

## Key Concepts

### Method-Level Security Annotations

SAPL provides two annotations for method-level policy enforcement:

#### `@PreEnforce`

Evaluates the policy **before** method execution. If denied, throws `AccessDeniedException`.

```java
@PreEnforce
List<Patient> findAll();
```

#### `@PostEnforce`

Evaluates the policy **after** method execution, allowing the return value to be used in the authorization decision and potentially transformed.

```java
@PostEnforce(resource = "returnObject")
Optional<Patient> findById(Long id);
```

The `resource = "returnObject"` parameter makes the method's return value available to the policy for content filtering.

If no parameters are provided, the PEP formulates an authorization subscription based on the `Principal` in the `SecurityContext` and the method invocation context.

### Custom Policy Information Point

The `PatientPIP` class demonstrates how to provide custom attributes for policy evaluation:

```java
@Service
@PolicyInformationPoint(name = "patient", description = "retrieves patient information")
public class PatientPIP {

    @Attribute(name = "relatives")
    public Flux<Value> getRelations(NumberValue patientId, Map<String, Value> variables) {
        // Returns list of relatives for the given patient ID
    }

    @Attribute(name = "patientRecord")
    public Flux<Value> getPatientRecord(NumberValue patientId, Map<String, Value> variables) {
        // Returns the patient record for the given ID
    }
}
```

These attributes are accessed in policies using expressions like:

```
resource.id.<patient.relatives>
action.java.arguments[0].<patient.patientRecord>
```

### Custom Constraint Handlers

The demo includes constraint handlers for obligations and advice:

- **`LoggingConstraintHandlerProvider`**: Logs access events when triggered by policies
- **`EmailConstraintHandlerProvider`**: Sends email notifications (simulated) for sensitive operations

### Policy Examples

**Content Filtering with Blackening:**
```
policy "administrator access to patient data"
permit action.java.name == "findById"
where "ROLE_ADMIN" in subject..authority;
transform resource |- {
    @.icd11Code : blacken(2,0,"\u2588"),
    @.diagnosisText : blacken(0,0,"\u2588")
}
```

**Field Removal for Visitors:**
```
policy "visiting relatives access patient data"
permit action.java.name == "findById"
where
    "ROLE_VISITOR" in subject..authority;
    subject.name in resource.id.<patient.relatives>;
transform resource |- {
    @.medicalRecordNumber : remove,
    @.icd11Code : remove,
    @.diagnosisText : remove
}
```

**Breaking the Glass (Emergency Override):**
```
policy "breaking the glass"
permit action.java.name == "updateDiagnosisTextById"
where ("ROLE_DOCTOR" in subject..authority);
obligation {
    "type": "sendEmail",
    "recipient": patient.attendingDoctor,
    "subject": "Data of your patient was changed.",
    "message": "Doctor " + subject.name + " changed the data."
}
```

## Project Structure

```
src/main/java/io/sapl/mvc/demo/
├── config/
│   ├── MvcConfig.java           # MVC configuration
│   └── WebSecurityConfig.java   # Spring Security configuration
├── constraints/
│   ├── EmailConstraintHandlerProvider.java
│   └── LoggingConstraintHandlerProvider.java
├── controller/
│   └── UIController.java        # Web UI controller
├── domain/
│   ├── Patient.java             # Patient entity
│   ├── PatientRepository.java   # Repository with @PreEnforce/@PostEnforce
│   └── Relation.java            # Patient-visitor relationship
├── pip/
│   └── PatientPIP.java          # Custom Policy Information Point
└── MvcDemoApplication.java      # Application entry point

src/main/resources/policies/
├── pdp.json                           # PDP configuration
├── patient_repository_policyset.sapl  # Repository access policies
├── ui_controller_policyset.sapl       # Controller access policies
└── ui_elements_policyset.sapl         # UI element visibility policies
```

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
```

The `sapl-spring-boot-starter` provides:
- Embedded Policy Decision Point (enabled by default)
- Auto-discovery of `@PolicyInformationPoint` and `@FunctionLibrary` beans
- Method security annotations (`@PreEnforce`, `@PostEnforce`)
- Constraint handler infrastructure

## Configuration

The application uses default SAPL configuration. Key properties (all optional with sensible defaults):

```yaml
io.sapl.pdp:
  embedded:
    pdp-config-type: RESOURCES      # Load policies from classpath
    policies-path: /policies        # Path to policy files
    print-text-report: true         # Enable human-readable decision reports
```
