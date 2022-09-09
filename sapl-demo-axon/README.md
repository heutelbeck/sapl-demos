# Demo Project - Attribute-based Access Control (ABAC) and Attribute Stream-based Access Control (ASBAC) using Axon Framework, Spring Boot and SAPL

This project demonstrates how to implement Attribute-based Access Control (ABAC) and Attribute Stream-based Access Control (ASBAC) 
in an event-driven application domain implemented with the Axon Framework and Spring Boot.

In particular, it demonstrates how to secure *commands* and *queries*, including *subscription queries* in a declarative style by
adding different SAPL Annotations to aggregates, domain services, or projections. These annotations automatically deploy 
policy enforcement points in the command and query handlers.

Further, the demo includes examples for customizing authorization subscriptions and enforcing obligations on the handling of 
commands and queries, such as triggering side-effects (e.g., dispatch events or commands) or modifying and filtering data 
before delivering it to users.

## Prerequisites

The only requirement for running the demo is the presence of a working install of JDK 17 and Maven. 
Also, port 8080 (for the demo application) and port 8888 (used by the embedded MongoDB) must be available when running the demo.

## Running the Demo

To run the demo, execute ``mvn spring-boot:run`` in the demos root folder. You can access the demo by navigating to [http://localhost:8080](http://localhost:8080). Here you will be greeted by a login form. And after logging in, you will be forwarded to a Swagger API page [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## The Demo Hospital Domain

The demo implements a simple hospital scenario. The central and only aggregate in the system is the ```Patient``` aggregate. 
Each patient can be registered, hospitalized, discharged, and diagnosed. Further, the staff can connect monitoring devices to patients to take measurements of vital signs, such as heart rate, blood pressure, body temperature, and respiration rate.

Whenever the application starts up, some patients are generated, diagnosed with conditions, and different monitors 
are connected to each of the patients.

To interact with the demo, you have to authenticate as a hospital staff member. The demo does not use aggregates to model staff members. 
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

The different commands and queries are secured with policy enforcement points and policies. 
The policies are not modeled after real-world hospital requirements but to demonstrate various features of SAPL and 
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

These documents can be accessed via the following queries, which are individually exposed through a REST controller: 

* ```record FetchAllPatients () {};```: Standard Query [http://localhost:8080/api/patients](http://localhost:8080/api/patients).
* ```record FetchPatient (String patientId) {};```: Standard Query [http://localhost:8080/api/patients/{id}](http://localhost:8080/api/patients/{id}).
* ```record MonitorPatient (String patientId) {};```: Subscription Query [http://localhost:8080/api/patients/{id}/stream](http://localhost:8080/api/patients/{id}/stream).


## Use Case Command 1: Simple Command Authorization

To establish a Policy Enforcement Point (PEP) for commands, any ```@CommandHandler``` method can be annotated with the ```@PreHandleEnforce``` annotation. For example, the ```HospitalisePatient``` command is handled in the ```Patient``` aggregate as follows:

```java
@CommandHandler
@PreHandleEnforce(action = "{'command':'HospitalisePatient', 'ward':#command.ward()}", 
                  resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
void handle(HospitalisePatient cmd) {
	apply(new PatientHospitalised(cmd.id(), cmd.ward()));
}
```

When handling the command, the PEP will formulate an authorization subscription. For example, when ```cheryl``` attempts to hospitalise patient ```3```in the general ward, the following subscription is generated based on the SpEL expression in the annotation:

```JSON
{
  "subject": {
    "username": "cheryl",
    "authorities": [],
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true,
    "assignedWard": "ICCU",
    "position": "DOCTOR"
  },
  "action": {
    "command": "HospitalisePatient",
    "ward": "GENERAL"
  },
  "resource": {
    "type": "Patient",
    "id": "3",
    "ward": "CCU"
  }
}
```

The following policy in the policy set ```src/main/resources/policies/patientCommandSet.sapl``` is controlling access in this scenario:

```
policy "only system/doctors may hospitalize patients but only into their own wards."
permit 	action.command == "HospitalisePatient"
where 
  subject == "SYSTEM" || (subject.position == "DOCTOR" && action.ward ==  subject.assignedWard);
```

As the subjects ward is not the same as the target ward indicated in the command, the PDP will deny execution of the command, returning the following authorization decision:

```JSON
{
  "decision": "DENY"
}
```

## Use Case Command 2: Aggregate Constraint Handlers

With SAPL, an authorization decision may contain additional constraints instructing the application to perform additional actions when granting or denying access. For an Axon application, the constraint may require that it be enforced within the ```UnitOfWork``` covering the command and that the aggregate state may change the same way as the initial command. For an aggregate, a constraint from an authorization decision may have to be executed just like an additional command that has to be successfully handled before handling the original command. 

In the demo, the command ```ConnectMonitorToPatient``` is secured in such a way. Whenever a user not from the ward where the patient is hospitalised, connects a monitor to the patient, this action must be recorded in the events of the aggregate. This requirement is expressed in the following policies in the policy set ```src/main/resources/policies/patientCommandSet.sapl```:

```
policy "all ward staff may connect and disconnect monitors."
permit 	action.command == "ConnectMonitorToPatient" | action.command == "DisconnectMonitorFromPatient"
where 
    (subject != "anonymous" && resource.ward == subject.assignedWard) || subject == "SYSTEM";

policy "all staff connect and disconnect monitors but it must be documented if they do not belong to the ward"
permit 	action.command == "ConnectMonitorToPatient" | action.command == "DisconnectMonitorFromPatient"
where 
    subject != "anonymous";
obligation
    {
      "type":"documentSuspisiousManipulation",
      "username": subject.username
    }
```

The policy set uses the ```first-applicable``` combining algorithm. Thus, if the ward of the patient and the ward of the user are the same, the action is simply permitted without additional constraints (```resource.ward == subject.assignedWard```). For any other authenticated user, an obligation is added to the permission that the suspicious manipulation must be recorded. 

The handling of the command and this obligation is implemented in the ```Patient``` aggregate:

```java
@CommandHandler
@PreHandleEnforce(action = "{'command':'ConnectMonitorToPatient'}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
void handle(ConnectMonitorToPatient cmd) {
    if (connectedMonitors.contains(cmd.monitorDeviceId()))
       throw new IllegalStateException(String.format("Monitor %s already connected to patient %s.", id, cmd.monitorDeviceId()));
    
    apply(new MonitorConnectedToPatient(cmd.id(), cmd.monitorDeviceId(), cmd.monitorType()));
}

/* ... */

@ConstraintHandler("#constraint.get('type').textValue() == 'documentSuspisiousManipulation'")
public void handleSuspiciousManipulation(JsonNode constraint) {
    apply(new SuspiciousManipulation(id, constraint.get("username").textValue()));
}
```

Once the PEP receives the decision containing the obligation, it checks if the aggregate possesses a ```@ConstraintHandler``` method where the contained SpEL expression evaluates to ```true``` for the given constraint and aggregate state. All of these handlers are invoked before invoking the command handler. This is also possible for multi-entity aggregates. 


## Use Case Query 1: Access Patient Diagnosis, the ```FetchPatient``` Query

For the ```FetchPatient``` query, the following rules are enforced:
- All doctors may see the complete medical record.
- All nurses working in the ward where the patient is hospitalised may see the complete medical record.
- All other authenticated users may access the medical record, but all except the first two letters of the ICD11 code and the diagnosis must be blackened, and access to the data must be recorded in an event.
- Unauthenticated users may not access the document.

For example, if the user ```karl``` who is a nurse in the ICCU, accesses the record of Mona Vance under http://localhost:8080/api/patients/0 the following response is sent:

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

Now, if the user ```eleanore``` who is a nurse in the general ward, accesses the same record of Mona Vance under http://localhost:8080/api/patients/0 the following response is sent:

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

The ```@PostHandleEnforce``` annotation establishes a Policy Decision Point (PDP) wrapping the ```handle(FetchPatient query)``` method. This annotation first invokes the method and then constructs an authorization decision evaluating the Spring Expression Language expressions in the annotation:

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

The policy states that access is granted (```permit```) for authenticated users (```subject != "anononymous"```) and that an assess attempt event must be emitted, and if this is not possible, access is denied (```obligation "dispatch access attempt event"```). Further, the ```transform``` expression states that the queryResult must be replaced with an object, where the ICD11 code and the diagnosis text are blackened. 

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

The PEP then replaces the query result with the object with the ```resource``` of the decision. Before returning this, the PEP checks if any handler for the obligation ```"dispatch access attempt event"``` is available. In the case of the demo, the Bean ```LogAccessEventEmitterProvider``` does support this type of obligation:

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

This query is exposed as a REST endpoint under http://localhost:8080/api/patients . While it is feasible to send an individual limited-size resource in the authorization subscription to the PDP for full inspection and transformation, objects of larger size or large collections would introduce significant traffic, latency, and load on the PDP. Here it is more sensible to instruct the PEP to modify the results locally by enforcing a matching constraints. The PEP may create an authorization subscription as follows:

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

As ```karl``` is assigned to the ICCU ward, he may see all patients, but for all patients not assigned to the ICCU, the diagnosis has to be blackened. This is expressed by the following SAPL policy in ```src/main/resources/policies/fetchAll.sapl```:

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

This results in the following authorization decision:

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

Which triggers the ```ResponseMessagePayloadFilterProvider``` to handle the obligation with the ```type``` ```filterMessagePayloadContent```. This is a constraint handler provider available by default in the SAPL Axon extension. If the ```conditions``` defined in the constraint are met for the query result, the different ```actions``` modifying the query result are applied. If the result is an array, ```Optional```, or an ```Iterable```, the conditions and actions are evaluated for each element.

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

Subscription queries are useful to monitor changes in the application state without resorting to polling. For example, in the demo, the Query ```MonitorPatient``` subscribes to any changes made to the medical data of the patient. The following query handler is exposed as a Server-Sent Events endpoint at http://localhost:8080/api/patients/{id}/stream, where ```{id}``` is the id of the patient.

```
  @QueryHandler
  @PreHandleEnforce(action = "'Monitor'", resource = "{ 'type':'patient', 'id':#query.patientId() }")
  Optional<PatientDocument> handle(MonitorPatient query) {
    return patientsRepository.findById(query.patientId());
  }
```

When ```karl``` accesseses http://localhost:8080/api/patients/0/stream, he may see the following data stream when using Chrome (note that the ```â–ˆ``` is equal to a ```█``` Chrome does not apply the correct encoding when visualizing Server-Sent-Events):

```
data:{"id":"0","name":"Mona Vance","latestIcd11Code":"1B95","latestDiagnosisText":"Brucellosis","ward":"ICCU","updatedAt":"2022-09-06T23:07:08.048Z"}

data:{"id":"0","name":"Mona Vance","latestIcd11Code":"1Bâ–ˆâ–ˆ","latestDiagnosisText":"Brâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ","ward":"NONE","updatedAt":"2022-09-06T23:08:15.054Z"}
```

In this case, first Mona was assigned to the ICCU and then got discharged by a doctor. Thus Mona was assigned to no ward. And as karl can only see medical data of patients assigned to the ICCU, the updated ```PatientDocument``` was delivered with blackened fields.

This is implemented with the same policy used for ```FetchAllPatients```. The obligation was applied to each individual subscription update. 
This is expressed in ```src/main/resources/policies/fetchAll.sapl```. This document is a policy set, which is applicable for both queries. As can been seen in the ```for``` clause of the policy set:

```
set "fetch patient list policy set"

/*
 * The 'first-applicable' combination algorithm is used here to avoid 'transformation uncertainty',
 * i.e., multiple policies which return PERMIT but do not agree on the transformation of the resource.
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

The demo includes a set of mock medical sensors attached to different patients. The following types of medical monitors are present in the demo:

```java
public enum MonitorType {
	BLOOD_PRESSURE, BODY_TEMPERATURE, RESPIRATION_RATE, HEART_RATE
}
```

For each patient, the different measurements from the different devices are available as Server-Sent Events under http://localhost:8080/api/patients/{id}/vitals/{MonitorType}/stream. For example http://localhost:8080/api/patients/0/vitals/BLOOD_PRESSURE/stream provides a stream of measurements from a blood pressure monitor connected to patient ```0```.

This stream of events is backed by the subscription query ```MonitorVitalSignOfPatient```. The access control policies attached to this query are not realistic but serve to illustrate how time-series data and dynamic authorization decisions changing over time can interact in an application. The policy set ```src\main\resources\policies\measurements.sapl``` demonstrates how to implement a simple time-based policy set. Instead of time, a SAPL PDP can use arbitrary external data streams, such as location tracking, other subscription queries, or IoT data.

```
/*
 * Import the filter library so that 'blacken' can be used directly instead of using the absolute name 'filter.blacken'.
 */
import filter.*
import time.*

/*
 * In each SAPL document, the top-level policy or policy set MUST have a unique name.
 */
set "fetch vital sign"

/*
 * The 'first-applicable' combination algorithm is used here to avoid 'transformation uncertainty',
 * i.e., multiple policies which return PERMIT but do not agree on the transformation of the resource.
 * This algorithm evaluates policies from top to bottom in the document and stops as soon as one policy 
 * yields an applicable result or errors.
 */
first-applicable

/*
 * scope the policy set to be applicable to all authorization subscriptions "Fetch" actions on "measurements".
 */
for resource.type == "measurement"

/*
 * All doctors and nurses have full access to raw data
 */
policy "permit doctors raw data" 
permit subject.position == "DOCTOR"

/*
 * All nurses only get categrorised blood pressure data. For the first 20 seconds of each minute
 */
policy "nurses get categorised blood pressure first 20s" 
permit subject.position == "NURSE" & resource.monitorType == "BLOOD_PRESSURE"
where 
	time.secondOf(<time.now>) < 20; 
obligation "catrgorise blood pressure"

/*
 * All nurses get all raw data for the second 20 seconds of each minute
 */
policy "nurses get raw blood pressure 2nd 20s" 
permit subject.position == "NURSE" & resource.monitorType == "BLOOD_PRESSURE"
where 
	time.secondOf(<time.now>) < 40; 

/*
 * All nurses are denied data the last 20 seconds of each minute
 */
policy "nurses get denied blood pressure 3rd 20s" 
deny subject.position == "NURSE" & resource.monitorType == "BLOOD_PRESSURE"

/*
 * All nurses only get categrorised body temperature data.
 */
policy "nurses get categorised body temperature"
permit subject.position == "NURSE" & resource.monitorType == "BODY_TEMPERATURE"
obligation "categorise body temperature"


/* other data feeds raw for nurses */
policy "other data feeds raw for nurses" 
permit subject.position == "NURSE"

policy "deny others data feed access" 
deny
```

One of the effects of this policy set is changing the decision every twenty seconds for nurses. For blood pressure, nurses have a period where access is denied to the data stream, a period where the data is not passed to the user without applying a categorisation filter, and finally, a phase where the raw data is forwarded to the user.

In this example, the matching ```@QueryHandler``` is secured with the ```@EnforceRecoverableUpdatesIfDenied``` which will drop events while access is denied but will allow clients to react on access denied events.

```java
@QueryHandler
@EnforceRecoverableUpdatesIfDenied(action = "'Monitor'", 
             resource = "{ 'type':'measurement', 'id':#query.patientId(), 'monitorType':#query.type() }")
Optional<VitalSignMeasurement> handle(MonitorVitalSignOfPatient query) {
  return repository.findById(query.patientId()).map(v -> v.lastKnownMeasurements().get(query.type()));
}
```

The handling of access denied for the controller is realized as follows:

```java
	private final SaplQueryGateway queryGateway;
	
	/* ... */

	@GetMapping("/api/patients/{id}/vitals/{type}/stream")
	Flux<ServerSentEvent<VitalSignMeasurement>> streamSingleVital(@PathVariable String id,
			@PathVariable MonitorType type) {
		var result = queryGateway.recoverableSubscriptionQuery(new MonitorVitalSignOfPatient(id, type),
				ResponseTypes.instanceOf(VitalSignMeasurement.class),
				ResponseTypes.instanceOf(VitalSignMeasurement.class), () -> log.info("AccessDenied"));
		return Flux.concat(result.initialResult().onErrorResume(AccessDeniedException.class, error -> {
			doOnAccessDenied(error, id, type);
			return Mono.empty();
		}), result.updates().onErrorContinue(AccessDeniedException.class,
				(error, reason) -> doOnAccessDenied(error, id, type)))
				.map(view -> ServerSentEvent.<VitalSignMeasurement>builder().data(view).build());
	}

	private void doOnAccessDenied(Throwable e, String id, MonitorType type) {
		log.warn("Access Denied on {} for patient {}. Data will resume when access is granted again. '{}'", type, id,
				e.getMessage());
	}
```

Using the ```onErrorContinue``` operator, a ```recoverableSubscriptionQuery``` sent via the ```SaplQueryGateway``` can stay subscribed to the updates, even if access is denied. The delivery of updates resumes on a permission decision by the PDP. For example, if ```karl``` accesses http://localhost:8080/api/patients/0/vitals/BLOOD_PRESSURE/stream , the result may look like this (e.g., in Chrome):

```
data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"106/71","unit":"systolic/diastolic mmHg","timestamp":"2022-09-07T13:46:32.997Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"Normal","unit":"Blood Pressure Category","timestamp":"2022-09-07T13:47:04.997Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"Normal","unit":"Blood Pressure Category","timestamp":"2022-09-07T13:47:12.996Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"107/67","unit":"systolic/diastolic mmHg","timestamp":"2022-09-07T13:47:20.997Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"108/65","unit":"systolic/diastolic mmHg","timestamp":"2022-09-07T13:47:28.996Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"109/66","unit":"systolic/diastolic mmHg","timestamp":"2022-09-07T13:47:36.996Z"}

data:{"monitorDeviceId":"mYJO75oPhLN6qhU8i8VEow","type":"BLOOD_PRESSURE","value":"Normal","unit":"Blood Pressure Category","timestamp":"2022-09-07T13:48:00.997Z"}
```

And at the same time, the application log will contain lines like this:

```
22-09-07 15:46:40 WARN  i.s.d.a.iface.rest.VitalSignsController  | Access Denied on BLOOD_PRESSURE for patient 0. Data will resume when access is granted again. 'Access Denied'
22-09-07 15:47:40 WARN  i.s.d.a.iface.rest.VitalSignsController  | Access Denied on BLOOD_PRESSURE for patient 0. Data will resume when access is granted again. 'Access Denied'
```
