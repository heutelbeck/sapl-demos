# Tutorial  sapl-demo-authorizationaspects

This demo shows how to use the annotation `@PdpAuthorize`. 

## Try it
Login with one of the users: Dominik, Julia, Peter, Alina, Thomas, Brigitte, Janosch, Janina or Horst. 
The Password is always "password". You can change this by providing another password in the application.properties. Not the plain value is needed but the bcrypted one (for example see https://www.dailycred.com/article/bcrypt-calculator).


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

The `@PdpAuthorize` annotation is used to establish a Policy Enforcement Point in the . The annotation is located [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorize.java). The corresponding aspect that performs the policy check can be found [here](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/annotation/PdpAuthorizeAspect.java).

The annotation has 3 attributes: `subject`, `action` and `resource`. If an annotated method is about to be executed, the `PdpAuthorizeAspect` will perform a SAPL request using the given values of these attributes.

An exemplary use can be found in the [UI-Controller](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-authorizationaspects/src/main/java/io/sapl/sapldemoauthorizationaspects/UIController) of this demo:

```java
	@GetMapping("/profiles")
	@PdpAuthorize(subject = "user", action = "getProfiles", resource = "profiles")	
	public String profileList(HttpServletRequest request, Model model, Authentication authentication) {
		
		...
		
		return "profiles";
	}
```
The corresponding policy (found [here](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-authorizationaspects/src/main/resources/policies(httpPolicy.sapl)) is:

```java
policy "permit_getting_profiles"
permit
    action == "getProfiles"
where 
	 subject == "user";
    resource == "profiles";
```

In this example the response will always be `PERMIT`, due to the assigned values being plain Strings. This is intended and merely meant to represent the basic relation between annotation and policy. It is, of course, possible to assign String Objects instead.

If an attribute is not assigned a value, the aspect will automatically check the following for usable values:
 
subject: JWT authentication, <br>
action: HttpServletRequest (i.e. GET, POST, etc.), <br>
resource: HttpServletRequest (i.e. URI).

Another example from the UI-Controller, without manual inputs:

```java
	@PdpAuthorize
	@PostMapping("/profiles")
	public String createProfile(HttpServletRequest request, @ModelAttribute(value = "newPatient") Patient newPatient) {
		
		...
		
		return REDIRECT_PROFILES;
	}
```

The corresponding policy is:

```java
policy "permit_doctor_post_profiles"
permit
    action == "POST"
where
  "DOCTOR" in subject..authority;
  resource == "/profiles";
```

When using HttpServletRequests as source for action or resource, it is sufficient to simply use the Rest method ("POST") for the action and the URI ("/profiles") for the resource. This is due to the [Sapl Mapper](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc#the-sapl-mapper).
In this example neither manual input nor JWT are available as a source for a subject value (Note: This demo  can demonstrate the usage of manual and default inputs as well as HttpServletRequests. A demo for JWT utilization can be found [here](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-jwt)).

If neither manual input nor automatic source are available for an attribute, the aspect will fall back to default values. These are:

subject: the authentication provided by the Spring Security `SecurityContextHolder`, <br>
action: the name of the annotated method, <br>
resource: the name of the class the method is located in. <br>

If the response to the request is `DENY`, the execution of the annotated method will be prevented and an `AccessDeniedException` will be thrown. Otherwise the method will be executed normally.


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
Annotate the method you wish to secure and write the necessary SAPL policy/policies.

Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that show you the parameters that are used by the aspect and provided to the `SAPLAuthorizator`, which can help you write your policies.

