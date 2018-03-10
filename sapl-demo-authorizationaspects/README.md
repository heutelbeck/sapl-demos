# Tutorial  sapl-demo-authorizationaspects

This demo shows how to use the annotation `@PdpAuthorize`. 


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

The annotation has 3 attributes: `subject`, `action` and `resource`. If an annotated method is about to be executed, the `PdpAuthorizeAspect` will perform a SAPL request using the given values of these attributes.

An exemplary use can be found in the [UI-Controller](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-authorizationaspects/src/main/java/io/sapl/sapldemoauthorizationaspects/UIController) of this demo:

```java
	@GetMapping("/patient/{id}/update")
	@PdpAuthorize(action = "viewPatientUpdateForm", resource = "PatientUpdateForm")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {

		...
		
		return "updatePatient";
	}
```

If an attribute is not assigned a value (like `subject` in the example above), the aspect will automatically check the following for usable values:
 
subject: JWT authentication, <br>
action: HttpServletRequest (i.e. method.GET, method.POST, etc.), <br>
resource: HttpServletRequest (i.e. URI).

If neither manual input nor automatic source are available for an attribute, the aspect will fall back to default values. These are:

subject: the authentication provided by the Spring Security `SecurityContextHolder`, <br>
action: the name of the annotated method, <br>
resource: the name of the class the method is located in. <br>

If the response to the request is `DENY`, the execution of the annotated method will be prevented and an `AccessDeniedException` will be thrown. Otherwise the method will be executed normally.

Note: This demo  can demonstrate the usage of manual and default inputs as well as HttpServletRequests. A demo for JWT utilization can be found [here](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-jwt).

## How to use the @PdpAuthorize annotation in your own application

### Step 1:
Add the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) project as dependency to the pom.xml of your project:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Step 2:
The aspect needs to be loaded into the Spring `ApplicationContext` in order to work properly. There are two ways to achieve that:

Option A: Add the aspect as bean.

Option B (recommended): Annotate the class that utilizes the `@PdpAuthorize` annotation with

```java
@ComponentScan("io.sapl.spring.annotation")
```

The aspect itself is annotated with `@Component`. However, the automatic component scan performed by a `@SpringBootApplication` only covers the package it is in and any sub-packages. To detect the aspect as component, a manual component scan of its package is required.

### Step 3:
Annotate the method you wish to secure and write the necessary SAPL policy/policies.

Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that show you the parameters that are used by the aspect and provided to the `SAPLAuthorizator`, which can help you write your policies.

