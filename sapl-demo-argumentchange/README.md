# Demo: Full Stack MVC Application

## What does it do?

The demo consists of a full-stack Spring MVC application, where the controllers establish policy enforcement points using the `@PdpAuthorize` annotation.
  
## Try it

Run the application: `mvn spring-boot:run`. The demo application will be accessible under [http:\\localhost:8080](http:\\localhost:8080).

Login with one of the users: Dominic, Julia, Peter, Alina, Thomas, Brigitte, Janosch, Janina or Horst. 
The Password is always "password".

## The SAPL annotations 

In this environment, in a blocking Servlet Container, SAPL offers two annotations to establish Policy Enforcement Points at the method level.

* `@PreEnforce`: The annotation deploys a PEP as a wrapper for the method, which retrieves a single decision from a SAPL PolicyDecision Point (PDP). The PEP will enforce constraints (advice, obligations) and grand or deny access based on the decision. If granted, the PEP calls the method, and else it raises an `AccessDeniedException`.

* `@PostEnforce`: The annotation deploys a PEP as a wrapper for the method. It will call the method and then, potentially based on the object's value returned by the method, retrieve a single decision from a SAPL PolicyDecision Point (PDP). The PEP will enforce constraints (advice, obligations) and grand or deny access based on the decision. If granted, the PEP calls the method, and else it raises an `AccessDeniedException`.

If the developer does not provide any parameters in the annotations, the PEP will formulate an authorization subscription based on the `Principal` in the `SecurityContext` and the method invocation itself. The PEP will take into account the context of a web request, if available.

You can customize the subscription by adding `subject`, `action`, or `resource` to the annotation, which uses Spring Expression Language.

Some examples can be found in the [`PatientRepository`](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-authorizationaspects/src/main/java/io/sapl/sapldemoauthorizationaspects/UIController) of this demo:

```java
public interface PatientRepository {

    @PostEnforce(resource = "returnObject")
    Optional<Patient> findById(Long id);

    @PreEnforce
    Optional<Patient> findByName(String name);

    // ...
}
```

The corresponding policy can be found [here](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-mvc-app/src/main/resources/policies/patient_repository_policyset.sapl).
