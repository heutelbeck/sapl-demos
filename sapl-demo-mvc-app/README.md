# Demo: Full Stack MVC Application

This demo application how to use the Spring aspect oriented programming model to easily establish policy enforcement points in your application code by using the   `@PdpAuthorize` annotation. 

## What does it do?

The demo consists of a full stack Spring MVC application, where the controllers establish policy enforcement points using the `@PdpAuthorize` annotation.
  
## Try it

Run the application: `mvn spring-boot:run`. The demo application will be accessible under [http:\\localhost:8080](http:\\localhost:8080).

Login with one of the users: Dominic, Julia, Peter, Alina, Thomas, Brigitte, Janosch, Janina or Horst. 
The Password is always "password".

## The SAPL annotations 

In this environment in a blocking Servlet Container SAPL offers two annotations to establish Policy Enforcement Points at method level.

* `@PreEnforce`: The annotation deploys a PEP as a wrapper for the method, which retrieves a single decision from a SAPL PolicyDecision Point (PDP) and based on the decision it will enforce constraints (advice, obligations) and grand or deny access. If granted, the method will be called, else an `AccessDeniedException` will be raised.

* `@PostEnforce`: The annotation deploys a PEP as a wrapper for the method. It will call the method and then, potentially based on the value of the object returned by the method retrieve a single decision from a SAPL PolicyDecision Point (PDP). Based on the decision it will enforce constraints (advice, obligations) and grand or deny access. If granted, the return value of the method will be returned, else an `AccessDeniedException` will be raised.

If no parameters are provided to the annotations, the PEP will formulate a authorization subscription based on the `Principal` in the `SecurityContext` and based on the method invocation itself. Potentially taking into account the context of a web request.

You can customize the subscription by adding `subject`, `action` or `resource` to the annotation, which use Spring Expression Language.

Some examples can be ound in the [`PatientRepository`](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-authorizationaspects/src/main/java/io/sapl/sapldemoauthorizationaspects/UIController) of this demo:

```java
public interface PatientRepository {

	@PostEnforce(resource = "returnObject")
	Optional<Patient> findById(Long id);

	@PreEnforce
	Optional<Patient> findByName(String name);

...
```

The corresponding policy can be found [here](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-mvc-app/src/main/resources/policies/patient_repository_policyset.sapl).
