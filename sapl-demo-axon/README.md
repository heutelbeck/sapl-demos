# Demo Project - Attribute-based Access Control (ABAC) using Axon Framework, Spring Boot and SAPL

This project demonstrates how to implement Attribute-based Access Control (ABAC) and Attribute Stream-based Access Control (ASBAC) 
in an event-driven application domain implemented with the Axon Framework and Spring Boot.

In particular, it demonstrates how to secure *commands* and *queries*, including *subscription queries* in a declarative style by
adding different SAPL Annotations to aggregates, domain services, or projections. These annotations will automatically deploy 
policy enforcement points in the different command and query handlers.

Further, the demo includes examples for customizing authorization subscriptions and enforcing obligations on the handling of 
commands and queries, such as triggering side-effects (e.g., dispatch events or commands) or modifying and filtering of data 
before delivering it to users.

## Prerequisites

The only requirement for running the demo is the presence of a working install of JDK 17 and Maven. 
Also, port 8080 (for the demo application) and port 8888 (used by the embedded MongoDB) have to be available when running the demo.

## Running the Demo

To run the demo, execute ``mvn spring-boot:run`` in the demos root folder. You can then access the demo by navigating to [http://localhost:8080](http://localhost:8080). Here you will be greeted by a login form. And after logging in you will be forwarded to a Swagger API page [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## 

## The Demo Hospital Domain

The demo implements a simple hospital scenario. The central and only aggregate in the system is the ```Patient``` aggregate. 
Each patient can be registered, hospitalized, discharged, and diagnosed. Further, the staff can connect monitoring devices to patients to take measurements of vital signs, such as heart rate, blood pressure, body temperature, and respiration rate.

Whenever the application starts up, a number of random patients are generate, diagnosed with a random condition, and different monitors 
are connected to each of the patients.

In order to interact with the demo you have to authenticate as a staff member of the hospital. Staff is not modeled using aggregates. 
You can impersonate the following pre-configured staff members by logging in:

| Username   | Password | Position       | Ward                                |
|------------|----------|----------------|-------------------------------------|
|  karl      | pwd      | NURSE          | Intensive Cardiac Care Unit (ICCU)  |
|  cheryl    | pwd      | DOCTOR         | Intensive Cardiac Care Unit (ICCU)  |
|  phyllis   | pwd      | NURSE          | Critical Care Unit (CCU)            |
|  neil      | pwd      | DOCTOR         | Critical Care Unit (CCU)            |
|  eleanore  | pwd      | NURSE          | General Ward (GENERAL)              |
|  david     | pwd      | DOCTOR         | Surgical intensive care Unit (SICU) |
|  donna     | pwd      | ADMINISTRATOR  | Not Assigned (NONE)                 |

Whenever a monitoring device is connected to a patient, the monitor starts publishing events with measurements taken.

The different commands and queries are secured with different policy enforcement points and policies. 
The policies are not modeled after real-world hospital requirements, but to demonstrate different features of SAPL and 
the Axon Framework integration.

### Use Case Query 1: Access Patient Diagnosis 

The Patient aggregate is projected into a MongoDB document:

```java
@Document
@JsonInclude(Include.NON_NULL)
public record PatientDocument (
	@Id
	String  id,
	String  name,
	String  latestIcd11Code,      // International Classification of Diseases 11th Revision
	String  latestDiagnosisText,  // Clear text explaining the diagnosis going along with the ICD11 code
	Ward    ward,
	Instant updatedAt) { };
```

These documents can be accessed via the following queries which are individually exposed through a REST controller: 

* ```record FetchAllPatients () {};```: Standard Query [http://localhost:8080/api/patients](http://localhost:8080/api/patients).
* ```record FetchPatient (String patientId) {};```: Standard Query [http://localhost:8080/api/patients/{id}](http://localhost:8080/api/patients/{id}).
* ```record FetchPatient (String patientId) {};```: Subscription Query [http://localhost:8080/api/patients/{id}/stream](http://localhost:8080/api/patients/{id}/stream).


