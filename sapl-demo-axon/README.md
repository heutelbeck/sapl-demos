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

Further, the following patients are created on startup:

|  Id  | Name                 | Ward                                | Pre-Connected Monitors                                    |
|------|----------------------|-------------------------------------|-----------------------------------------------------------|
|  0   | Mona Vance           | Intensive Cardiac Care Unit (ICCU)  | Pulse, Blood Pressure, Body Temperature, Respiration Rate |
|  1   | Martin Pape          | Intensive Cardiac Care Unit (ICCU)  | Pulse, Blood Pressure                                     |
|  2   | Richard Lewis        | Critical Care Unit (CCU)            | Pulse,                 Body Temperature, Respiration Rate |
|  3   | Jesse Ramos          | Critical Care Unit (CCU)            |        Blood Pressure,                   Respiration Rate |
|  4   | Lester Romaniak      | Critical Care Unit (CCU)            | Pulse, Blood Pressure, Body Temperature, Respiration Rate |
|  5   | Matthew Cortazar     | Surgical intensive care Unit (SICU) | Pulse,                                   Respiration Rate |
|  6   | Timothy Favero       | Surgical intensive care Unit (SICU) | Pulse, Blood Pressure, Body Temperature, Respiration Rate |
|  7   | Louise Colley        | General Ward (GENERAL)              |                                          Respiration Rate |
|  8   | Bret Gerson          | Intensive Cardiac Care Unit (ICCU)  |                        Body Temperature                   |
|  9   | Richard Spreer       | Not Assigned (NONE)                 |        Blood Pressure                                     |


Whenever a monitoring device is connected to a patient, the monitor starts publishing events with measurements taken.

The different commands and queries are secured with different policy enforcement points and policies. 
The policies are not modeled after real-world hospital requirements, but to demonstrate different features of SAPL and 
the Axon Framework integration.

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
* ```record MonitorPatient (String patientId) {};```: Subscription Query [http://localhost:8080/api/patients/{id}/stream](http://localhost:8080/api/patients/{id}/stream).

## Use Case Query 1: Access Patient Diagnosis, the ```FetchPatient``` Query

First, for the ```FetchPatient``` query the folowing rules are enforced:
- All doctors may see the complete medical record.
- All nurses working in the ward where the patient is hospitalised may see the complete medical record.
- All other authenticated users may access the medical record, but all except the first two letters of the ICD11 code and the diagnosis must be blackened and access to the record must be recorded in an event.
- Unauthenticated users may not access the document.

For example, if the user ```karl``` who is a nurse in the ICCU, accesses the record of Mona Vance under ```http://localhost:8080/api/patients/0``` the following respone is sent:

```JSON
{
  "id": "0",
  "name": "Mona Vance",
  "latestIcd11Code": "1B95",
  "latestDiagnosisText": "Brucellosis",
  "ward": "ICCU",
  "updatedAt": "2022-09-05T20:06:42.919Z"
}
```

Now, if the user ```eleanore``` who is a nurse in the general ward, accesses the same record of  of Mona Vance under ```http://localhost:8080/api/patients/0``` the following respone is sent:

```JSON
{
  "id": "0",
  "name": "Mona Vance",
  "latestIcd11Code": "1B██",
  "latestDiagnosisText": "Br█████████",
  "ward": "ICCU",
  "updatedAt": "2022-09-05T20:06:42.919Z"
}
```

Additionally, an ```AccessAttempt``` event is published to the event store. The demo uses an embedded MongoDB at runtime. You can connect to it via port 8888 and inspect the ```domainevents``` collection. There you can find an event similar to this:

```JSON
{
    "_id" : "63166fc7590d2000f31a9a6a",
    "aggregateIdentifier" : "af6622f9-b0e6-4d7a-82a9-1992d5cd272f",
    "type" : null,
    "sequenceNumber" : 0,
    "serializedPayload" : "{\"message\":\"Access to a protected resource was attempted/continued by {\\\"username\\\":\\\"eleanore\\\",\\\"authorities\\\":[],\\\"accountNonExpired\\\":true,\\\"accountNonLocked\\\":true,\\\"credentialsNonExpired\\\":true,\\\"enabled\\\":true,\\\"assignedWard\\\":\\\"GENERAL\\\",\\\"position\\\":\\\"NURSE\\\"}. Access was  GRANTED. Means of access: class io.sapl.demo.axon.query.patients.api.PatientQueryAPI$FetchPatient\",\"decision\":{\"decision\":\"PERMIT\",\"resource\":{\"id\":\"0\",\"name\":\"Mona Vance\",\"latestIcd11Code\":\"1B██\",\"latestDiagnosisText\":\"Br█████████\",\"ward\":\"ICCU\",\"updatedAt\":\"2022-09-05T21:52:51.388Z\"},\"obligations\":[\"dispatch access attempt event\"]},\"cause\":{\"queryName\":\"io.sapl.demo.axon.query.patients.api.PatientQueryAPI$FetchPatient\",\"responseType\":{\"expectedResponseType\":\"io.sapl.demo.axon.query.patients.api.PatientDocument\"},\"payload\":{\"patientId\":\"0\"},\"identifier\":\"dfdaef45-67f0-4fb7-895a-be0847b792a1\",\"metaData\":{\"subject\":\"{\\\"username\\\":\\\"eleanore\\\",\\\"authorities\\\":[],\\\"accountNonExpired\\\":true,\\\"accountNonLocked\\\":true,\\\"credentialsNonExpired\\\":true,\\\"enabled\\\":true,\\\"assignedWard\\\":\\\"GENERAL\\\",\\\"position\\\":\\\"NURSE\\\"}\"},\"payloadType\":\"io.sapl.demo.axon.query.patients.api.PatientQueryAPI$FetchPatient\"}}",
    "timestamp" : "2022-09-05T21:53:11.586Z",
    "payloadType" : "io.sapl.demo.axon.query.constraints.LogAccessEventEmitterProvider$AccessAttempt",
    "payloadRevision" : null,
    "serializedMetaData" : "{}",
    "eventIdentifier" : "af6622f9-b0e6-4d7a-82a9-1992d5cd272f"
}
```

This is achieved by securing the ```@QueryHandler``` method for ```FetchPatient``` with a SAPL annotation:

```java
@QueryHandler
@PostHandleEnforce(action = "'Fetch'", resource = "{ 'type':'patient', 'value':#queryResult }")
Optional<PatientDocument> handle(FetchPatient query) {
	return patientsRepository.findById(query.patientId());
}
```

The ```@PostHandleEnforce``` annotation establishes a Policy Decision Point (PDP) wrapping the ```handle(FetchPatient query)``` method. This annotation first invokes the method, and then constructs an authorizuation decision evaluating the Sprinf Expression Language expressions in the annotation:

```JSON
{
  "subject": {
    "username": "eleanore",
    "authorities": [],
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true,
    "assignedWard": "GENERAL",
    "position": "NURSE"
  },
  "action": "Fetch",
  "resource": {
    "type": "patient",
    "value": {
      "id": "0",
      "name": "Mona Vance",
      "latestIcd11Code": "1B95",
      "latestDiagnosisText": "Brucellosis",
      "ward": "ICCU",
      "updatedAt": "2022-09-05T22:02:14.239Z"
    }
  }
}
```

As you can see, the ```@PostHandleEnforce``` annotation makes the query result available as ```#queryResult``` in the SpEL expression. With this authorization subscription, the following policy in ```src/main/resources/fetchPatient.sapl``` is triggered:

```
policy "authenticated users may see filtered" 
permit subject != "anononymous"
obligation "dispatch access attempt event"
transform 
  // Subtractive template with filters removing content
  resource.value |- { 
                       @.latestIcd11Code : blacken(2,0,"\u2588"),
		       @.latestDiagnosisText : blacken(2,0,"\u2588")
                    }
```

The policy states thet access is granted (```permit```) for authenticated users (```subject != "anononymous"```) and that an assess attempt event must be emitted, and if this is not possible, access is denied (```obligation "dispatch access attempt event"```). Further, the ```transform``` expression states, that the queryResult must be replaced with an object, where the ICD11 code and the diagnosis text are blackened. 

Thus, the PDP sends an authorization decision:

```JSON
{
  "decision": "PERMIT",
  "resource": {
    "id": "0",
    "name": "Mona Vance",
    "latestIcd11Code": "1B██",
    "latestDiagnosisText": "Br█████████",
    "ward": "ICCU",
    "updatedAt": "2022-09-05T22:02:14.239Z"
  },
  "obligations": [
    "dispatch access attempt event"
  ],
  "advice": []
}
```

The PEP then replaces the query result with the object with the ```resource``` of the decision. Before retuning this, the PEP checks if any handler for the obligation ```"dispatch access attempt event"``` is available. In the case of the demo, the Bean ```LogAccessEventEmitterProvider``` does support this type of obligation:

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class LogAccessEventEmitterProvider implements OnDecisionConstraintHandlerProvider {

	public record AccessAttempt(String message, AuthorizationDecision decision, Message<?> cause) {
	};

	private final ReactorEventGateway eventGateway;

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint.isTextual() && "dispatch access attempt event".equals(constraint.textValue());
	}

	@Override
	public BiConsumer<AuthorizationDecision, Message<?>> getHandler(JsonNode constraint) {
		return (decision, cause) -> {
			var message = "Access to a protected resource was attempted/continued by ";
			var subject = cause.getMetaData().get("subject");
			if (subject != null)
				message += subject;
			else
				message += "an unknwon actor";

			message += ". Access was ";

			if (decision.getDecision() == Decision.PERMIT)
				message += " GRANTED. ";
			else
				message += " DENIED. ";

			message += "Means of access: "+cause.getPayloadType();
			eventGateway.publish(new AccessAttempt(message, decision, cause)).subscribe();
			log.debug("Published access log event to event bus: {}",message);

		};
	}

}
```

## Use Case Query 2: Access Collections of Patients, the ```FetchAllPatients``` Query

The ```FetchAllPatients``` query is handled by the following query handler:

```java
	@QueryHandler
	@PreHandleEnforce(action = "'FetchAll'", resource = "{ 'type':'patient' }")
	Iterable<PatientDocument> handle(FetchAllPatients query) {
		return patientsRepository.findAll();
	}
```

This query is exposed as a REST endpoint under [http://localhost:8080/api/patients](http://localhost:8080/api/patients). While it is feasible to send an idividual limited size resource in the authorization subscription to the PDP for full inspection and transformation, objects of larger size or large collections, would introduce significant traffic, latency, and load on the PDP. Here it is more sensible to instruct the PEP to modify the results locally by enforcing a matching contstraint. The PEP may create an authorization subscription as follows:

```JSON
{
  "subject": {
    "username": "karl",
    "authorities": [],
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true,
    "assignedWard": "GENERAL",
    "position": "ICCU"
  },
  "action": "FetchAll",
  "resource": {
    "type": "patient"
  }
}
```

As ```karl``` is assigned to the ICCU ward, he may see all partients, but for all patients not assigned to the ICCU, the diagnosis has to be blackened. This is expressed by the following SAPL policy in ```src/main/resources/policies/fetchAll.sapl```:

```
policy "authenticated users may see filtered" 
permit subject != "anononymous"
obligation
  {
    "type" : "filterMessagePayloadContent",
    "conditions" : [ 
                     {
                        "type"  : "!=",
                        "path"  : "$.ward",
		        "value" : subject.assignedWard
                     }
		   ],
    "actions"    : [
                     { 
                       "type" : "blacken", 
                       "path" : "$.latestIcd11Code", 
                       "discloseLeft": 2
                     },
                     { 
                       "type" : "blacken", 
                       "path" : "$.latestDiagnosisText",
                       "discloseLeft": 2						  
                     }
                   ]
  }
```

Which results in the following authorization decision:

```JSON
{
  "decision": "PERMIT",
  "obligations": [
    {
      "type": "filterMessagePayloadContent",
      "conditions": [
        {
          "type": "!=",
          "path": "$.ward",
          "value": "ICCU"
        }
      ],
      "actions": [
        {
          "type": "blacken",
          "path": "$.latestIcd11Code",
          "discloseLeft": 2
        },
        {
          "type": "blacken",
          "path": "$.latestDiagnosisText",
          "discloseLeft": 2
        }
      ]
    }
  ]
}
```

Which triggers the ```ResponseMessagePayloadFilterProvider``` to handle the obligation with the ```type``` ```filterMessagePayloadContent```. This is a constraint handler provider available by default in the SAPL Axon extension. If the ```conditions``` defined in the constraint are met for the query result, the diffferent ```actions``` modifying the query result are applied. If the result is an array, ```Optiona```, or an ```Iterable```, the conditions and actions are evaluated for each individual element.

In the case of ```karl``` the REST service will return:

```JSON
[
  {
    "id": "0",
    "name": "Mona Vance",
    "latestIcd11Code": "1B95",
    "latestDiagnosisText": "Brucellosis",
    "ward": "ICCU",
    "updatedAt": "2022-09-06T22:15:36.321Z"
  },
  {
    "id": "1",
    "name": "Martin Pape",
    "latestIcd11Code": "6A25.3",
    "latestDiagnosisText": "Manic mood symptoms in primary psychotic disorders",
    "ward": "ICCU",
    "updatedAt": "2022-09-06T22:15:36.355Z"
  },
  {
    "id": "2",
    "name": "Richard Lewis",
    "latestIcd11Code": "6D██",
    "latestDiagnosisText": "Fa████████████████████████████████████",
    "ward": "CCU",
    "updatedAt": "2022-09-06T22:15:36.395Z"
  }
  ...
]
```

## Use Case Query 3: Subscription Query for Medical Records, the ```MonitorAllPatients``` SubscriptionQuery

#### The ```MonitorPatient``` Subscription Query

Subscription queries are useful to monitor changes of the application state withour resorting to polling. For example, in the demo the Query ```MonitorPatient``` subscribes to any changes made to the medical data of the patient. The following query handler is exposed as a Server-Sent Events endpoint at: [http://localhost:8080/api/patients/{id}/stream]([http://localhost:8080/api/patients](http://localhost:8080/api/patients/{id}/stream)), where ```{id}``` is the id of the patient.

```
  @QueryHandler
  @PreHandleEnforce(action = "'Monitor'", resource = "{ 'type':'patient', 'id':#query.patientId() }")
  Optional<PatientDocument> handle(MonitorPatient query) {
    return patientsRepository.findById(query.patientId());
  }
```

When ```karl``` accesseses [http://localhost:8080/api/patients/0/stream]([http://localhost:8080/api/patients](http://localhost:8080/api/patients/0/stream)), he may see the following data stream when using Chrome (note that the ```â–ˆ``` is equal to a ```█``` Chrome does not apply the correct encoding when visualizing Server-Sent-Events):

```
data:{"id":"0","name":"Mona Vance","latestIcd11Code":"1B95","latestDiagnosisText":"Brucellosis","ward":"ICCU","updatedAt":"2022-09-06T23:07:08.048Z"}

data:{"id":"0","name":"Mona Vance","latestIcd11Code":"1Bâ–ˆâ–ˆ","latestDiagnosisText":"Brâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ","ward":"NONE","updatedAt":"2022-09-06T23:08:15.054Z"}
```

In this case, first Mona was aggigned to the ICCU and then got discharged by a doctor. Thus Mona was assigned to no ward. And as karl can only see medical data of parients assigned to the ICCU, the updated ```PatientDocument``` was delivered with blackened fields.

This is implemented with the same policy used for ```FetchAllPatients``` the obligation was applied to each individual subscription update. 
This is expressed in ```src/main/resources/policies/fetchAll.sapl```. This document is a policy set, which is applicable for both queries. As can bee seen in the ```for``` clause of the policy set:

```
set "fetch patient list policy set"

/*
 * The 'first-applicable' combination algorithm is used here in oder to avoid 'transformation uncertainty',
 * i.e., multiple policies which return PERMIT but do not agree about transformation of the resource.
 * This algorithm evaluates policies from top to bottom in the document and stops as soon as one policy 
 * yields an applicable result or errors.
 */
first-applicable

/*
 * scope the policy set to be applicable to all authorization subscriptions "Fetch" actions on "patient".
 */
for resource.type == "patient" & (action == "FetchAll" | action == "Monitor")

/*
 * All doctors have full access to medical data of the patients.
 * All nurses working in the same ward where the patient is hospitalized have full access. 
 */
policy "permit doctors full patient access" 
permit subject.position == "DOCTOR"

/*
 * All other authenticated staff members have limited access.
 */
policy "authenticated users may see filtered" 
permit subject != "anononymous"
obligation
obligation
  {
    "type" : "filterMessagePayloadContent",
    "conditions" : [ 
                     {
                        "type"  : "!=",
                        "path"  : "$.ward",
		        "value" : subject.assignedWard
                     }
		   ],
    "actions"    : [
                     { 
                       "type" : "blacken", 
                       "path" : "$.latestIcd11Code", 
                       "discloseLeft": 2
                     },
                     { 
                       "type" : "blacken", 
                       "path" : "$.latestDiagnosisText",
                       "discloseLeft": 2						  
                     }
                   ]
  }
```

## Use Case Query 4: Subscription Query for Vital Signs, the ```MonitorVitalSignOfPatient``` SubscriptionQuery

## Use Case Command 1: Simple Authorization

## Use Case Command 2: Aggregate Constraint Handlers




