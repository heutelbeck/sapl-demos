# Submodule  sapl-demo-annotation

This demo shows how to use the annotations `@PdpAuthorize` and `@PdpAuthorizeHttp`. 

## sapl-spring-boot-starter

Obtaining a decision from SAPL Policies we need a `PolicyDecisionPoint`(`PDP`). A `PDP` as a `bean`  is  available as dependency for
a Spring Boot Starter Project, configured in the submodule [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter)
from project <https://github.com/heutelbeck/sapl-policy-engine> .
Remote or embedded `PDP` can be integrated into a Spring Boot Project with:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## sapl-spring


In conjunction with SAPL requests we need a [SAPLAuthorizator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLAuthorizator.java), information about an authenticated user, objects of the domain model,
the system environment, HttpServletRequest parameters, the requested URI, et cetera.
The submodule [sapl-spring](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring) from <https://github.com/heutelbeck/sapl-policy-engine> provides these interfaces and classes
and itself is loaded as dependency within the dependency to [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-annotation#sapl-spring-boot-starter).


## Spring Features

General spring features in this submodule are:

* [Spring Boot](https://projects.spring.io/spring-boot/)
* Standard SQL database: [H2](http://www.h2database.com) (In-Memory), programmable via JPA
* [Hibernate](http://hibernate.org/)
* web interfaces (Rest, UI) with Spring MVC
* model classes (Patient, User, Relation), CrudRepositories in JPA
* [Spring Security](https://projects.spring.io/spring-security/)
* Thymeleaf


## The @PdpAuthorize Annotation

The `@PdpAuthorize` annotation is used to regulate the execution of a method based on the response of a SAPL-policy check. The annotation is located [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorize.java). The corresponding aspect that performs the policy check can be found [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorizeAspect.java).

An exemplary use can be found in the [UI-Controller](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-annotation/src/main/java/io/sapl/sapldemoannotation/UIController) of this demo:

```java
	@GetMapping("/patient/{id}/update")
	@PdpAuthorize(action = "viewPatientUpdateForm", resource = "PatientUpdateForm")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {

		...
		
		return "updatePatient";
	}
```

The annotation has 3 attributes: `subject`, `action` and `resource`. If an annotated method is about to be executed, the `PdpAuthorizeAspect` will perform a SAPL request using the given values of these attributes. If an attribute is not assigned a value (like `subject` in the example above), the aspect will use a default value instead. These are as follows:

subject: the authentication provided by the Spring Security `SecurityContextHolder`, <br>
action: the name of the annotated method, <br>
resource: the name of the class the method is located in. <br>

If the response to the request is `DENY`, the execution of the annotated method will be prevented and an `AccessDeniedException` will be thrown. Otherwise the method will be executed normally.

## The @PdpAuthorizeHttp Annotation

The `@PdpAuthorizeHttp` annotation is used to regulate the execution of a method based on the response of a SAPL-policy check. The annotation is located [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorizeHttp.java). The corresponding aspect that performs the policy check can be found [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorizeHttpAspect.java).

An exemplary use can be found in the [UI-Controller](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-annotation/src/main/java/io/sapl/sapldemoannotation/UIController) of this demo:

```java
	@PdpAuthorizeHttp
	@DeleteMapping("/patient")
	public String delete(HttpServletRequest request, @RequestParam("id") int id) {
		
		...
		
		return REDIRECT_PROFILES;
	}
```

The annotation has no attributes. If an annotated method is about to be executed, the `PdpAuthorizeHttpAspect` will perform a SAPL request using the `HttpServletRequest` parameter of the method to extract the necessary values. These are as follows:

subject: the authentication provided by the Spring Security `SecurityContextHolder`, <br>
action: the request method (i.e. GET, POST, etc.), <br>
resource: the requested URL. <br>

If the response to the request is `DENY`, the execution of the annotated method will be prevented and an `AccessDeniedException` will be thrown. Otherwise the method will be executed normally.



Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizator`, which can help you write your policies.

