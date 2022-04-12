# SAPL Axon Demo

This demo shows how to add policy enforcement to an axon application by using the SAPL Axon Backend Integration and the
SAPL Axon Client-Side Utilities.

Within a small example domain the different features demonstrate how to ensure commands, queries and subscription
queries oby SAPL policies.

The demo exposes REST endpoints which trigger axon commands, queries and subscription queries. When the consecutive axon
actions are processed by the respective handlers the corresponding policies are evaluated and enforced.

## Structure of the application

The demo is centered around a simple medical record domain which you can change and read via a REST interface.

The `domain` package contains an aggregate with entity, a projection, and API classes as commonly found when modelling
and implementing with the [Axon Framework](https://docs.axoniq.io/reference-guide/). Most importantly, it is
demonstrated how to add [SAPL](https://sapl.io/) features to your axon application.

In the `constraints` package two constraint handler implementations of
available [SAPL Axon Backend Integration](https://github.com/FTKeV/fapra-2021/tree/main/sapl-axon/sapl-axon-backend/src/main/java/io/sapl/axon/constraints)
interfaces demonstrate how to use SAPL constraint handling in your Axon application.

The most important part of the `configuration` package is how to configure your spring application, such that relevant
SAPL functionality is readily available.

[Spring Profiles](https://docs.spring.io/spring-boot/docs/1.2.0.M1/reference/html/boot-features-profiles.html) are
attached to logical group classes and beans and in addition this allows to run the application with different
properties. This is further elaborated in the section _Configuration options_.

## Running the application

The demo expects the Java 11 runtime.

Before starting the demo you need to update the local dependencies of your repository, such that you include the
required modules of this multi-module project. The demo relies on the modules for:

* SAPL Axon Backend Integration
* SAPL Axon Client-Side Utilities

To build the demo and the required dependencies simply install the project parent:

```shell
sapl-demo-axon$ mvn -P sapl-axon -Dmaven.test.skip=true -f ../pom.xml install
```

---

### Configuration options

There are several options how to run the application:

- monolithic or distributed (microservices)
- [MongoDb](https://www.mongodb.com/)
  or [AxonServer](https://docs.axoniq.io/reference-guide/axon-server/installation/local-installation/axon-server-se)
  as an event store
- blocking or async implementation for SAPL enforcement on command side

These options can be combined activating
corresponding  [Spring profiles](https://docs.spring.io/spring-boot/docs/1.2.0.M1/reference/html/boot-features-profiles.html)
, which are attached throughout the application. To facility running the app Spring profile groups are available in
the _application.properties_ file.

**Monolith + MongoDB** - Spring profiles:

- `singleapp-blocking` (default profile): uses `DefaultSAPLCommandHandlerEnhancer` to enforce policies on command side
- `singleapp-async`: uses `SaplCommandBus` to enforce policies on command side

**Microservices + AxonServer**<br>
The app is divided into backend and client which communicate over an AxonServer.

backend - Spring profiles:

- `backend-blocking-axonserver`: uses `DefaultSAPLCommandHandlerEnhancer` to enforce policies on command side
- `backend-async-axonserver` (default in docker-compose.yml): uses `SaplCommandBus` to enforce policies on command side

client - Spring profile:

- `client-axonserver`

---

### Running the app as monolith + blocking command side + MongoDB

The default Spring profile starts the app as monolith and with a blocking implementation for policy enforcement on the
command side. Also bootstraps an embedded MongoDb which is used as event store.<br>
The simplest way to run the demo is to use spring boot:

```shell
sapl-demo-axon$ mvn spring-boot:run
```

### Running the demo as microservices + async command side + AxonServer

First you need to build the image

```shell
sapl-demo-axon$ mvn spring-boot:build-image
```

then go into the docker folder und start the multi-container Docker applications

```shell
sapl-demo-axon/docker$ docker-compose up
```

## How to use the REST API

The demo expects request at `localhost:8080`.

Documentation of the REST API http://localhost:8080/swagger-ui/index.html.

The REST API is structured in a way that each endpoint will trigger either 
a command, query or subscription query which is processed by the AxonFramwork.
Respective policies are enforced as the application uses the SAPL axon integration . In order to bind actions to users (subject)
authentication is required when sending a http request to endpoints. Different users are preconfigured
and help to demonstrate features of the SAPL axon integration in combination with
the SAPL policy set (file:`example_policyset.sapl`). 

**Preconfigured users**

- `admin`:  perform all actions without constraints

Password for all users: **pwd**


### Endpoints and relevant demo scenarios

**POST /medicalrecords**

Create a new medical record with following body parameters:

- id (String): the medical record id
- name (String): the name of the patient

Sends a `CreateMedicalRecordCommand` which is handled by the `MedicalRecord` aggregate.
The command is pre-enforced.
```java
@PreEnforce
@CommandHandler
public MedicalRecord(CreateMedicalRecord command)
```
Example:
``` shell
curl -u admin:pwd -X POST -F id=1 -F name=Mueller localhost:8080/medicalrecords
```

**PUT /medicalrecords/{id}**

Update the medical record with the given id and following parameters:

- pulse (double): A numeric value for the current pulse
- oxygenSaturation (double): A numeric value for the current oxygen saturation

Sends a `UpdateMedicalRecordCommand` which is handled by the `MedicalRecord` aggregate.

Example:
``` shell
curl -u admin:pwd -X PUT -F pulse=82 -F oxygenSaturation=95 localhost:8080/medicalrecords/1
```

**GET /medicalrecords/{id}**

Get the medical record with given id

Issues a `FetchMedicalRecordSummaryQuery` via the queryGateway and returns the corresponding `MedicalRecordSummary`.

Example:
``` shell
curl -u user2:pwd localhost:8080/medicalrecords/1
```

**GET /medicalrecords/{id}/subscribe**

Subscribe to the medical record projection via the query Gateway to receive updates.

Issues a `FetchMedicalRecordSummaryQuery` in from as a subscription query and returns updates of `MedicalRecordSummary`.

Example:
``` shell
curl -u admin:pwd localhost:8080/medicalrecords/1/subscribe
```

**GET /medicalrecords/{id}/pulse/subscribe**

Subscribe to the medical record projection via the query Gateway to receive updates.

Issues a `FetchMedicalRecordSummaryQuery` in from as a subscription query and returns updates of `MedicalRecordSummary`.

Example:
``` shell
curl -u admin:pwd localhost:8080/medicalrecords/1/pulse/subscribe
```

**GET /medicalrecords/{id}/oxygensaturation/subscribe**

Subscribe to the medical record projection via the query Gateway to receive updates.

Issues a `FetchMedicalRecordSummaryQuery` in from as a subscription query and returns updates of `MedicalRecordSummary`.

Example:
``` shell
curl -u admin:pwd localhost:8080/medicalrecords/1/oxygensaturation/subscribe
```

**POST /medicalrecords/{id}/bloodcount**

Create an empty blood count examination and add it to an existing medical record.

Path parameter

- id (String): medical record id

Body parameter

- examinationId (int): id of the blood count examination

Sends a `CreateBloodCountCommand`which is routed to the  `MedicalRecord` aggregate's `BloodCount`entity.

Example:
``` shell
curl -u admin:pwd -X POST -F examinationID=1 localhost:8080/medicalrecords/1/bloodcount
```

**PUT /medicalrecords/{id}/bloodcount/{examinationId}**

Updates the blood count examination with examinationId of a medicalrecord with a given id.

Path parameters

- id (String): medical record id
- examinationId (int): examination it od the blood count

Body parameters

- hematocrit (double): the hematocrit value

Sends a `UpdateBloodCountCommand`which is routed to the  `MedicalRecord` aggregate's `BloodCount`entity.

Example:
``` shell
curl -u admin:pwd -X PUT -F hematocit=55 localhost:8080/medicalrecords/1/bloodcount/1
```


## Scenarios with Constraint Handlers
MessageConsumerConstraintHandlerProvider:
- update the MedicalRecord of patient with id=42 via a UpdateMedicalRecordCommand and authenticate as a user of your choice
- the decision will have an obligation appended because patient with id=42 participates in a pharmaceutical study
- his/her medical data must be logged for later auditing
- you should see a log message

ConstraintHandler method in MedicalRecord
- update the MedicalRecord of a patient of your choice via a UpdateMedicalRecordCommand and authenticate as "doctorOnProbation" with password "pwd"
- this user is a doctor on probation. She should only apply medical treatment to patients with a clinical record appended. If it is not available the record will be created.
- due to the handler's location inside the MedicalRecord aggregate the constraint handling logic has access to the boolean property "hasClinicalRecordAvailable" which is false by default
- you should see a log message
- at the next similar request the record will be available

(Result-) MappingConstraintHandlerProvider
- request the PulseRecord of a patient of your choice and authenticate as "externalService" with password "pwd"
- this external service must not be granted access to the detailed pulse data of patients but only a value indicating the range the pulse value falls in (too low = 0.0, normal = 1.0, too high = 2.0) so that the service (e.g. a device) can send alerts in case of threshold violations
- the pulse value returned during query handling will be mapped by the associated handler and returned