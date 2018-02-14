# Submodule  sapl-demo-permissionevaluator

**Contents**

* [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#sapl-spring-boot-starter)
* [sapl-spring](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#sapl-spring)
* [Spring Features](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#spring-features)
* [Spring Security Features](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#spring-security-features)
* [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#saplpermissionevaluator)
* [Function Library](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#function-library)
* [Best Practice](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#best-practice)



The submodule sapl-demo-permissionevaluator makes extensive use of the [PermissionEvaluator Interface](https://docs.spring.io/spring-security/site/docs/5.0.2.BUILD-SNAPSHOT/reference/htmlsingle/#el-permission-evaluator) from [Spring Security](https://projects.spring.io/spring-security/).
The most important features of 'sapl-demo-permeval' are given below.

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
the system environment, HttpServletRequest parameters, the requested URI, et cetera,  and last but not least we need a customized
PermissionEvaluator, the [SAPLPermissionEvaluator].
The submodule [sapl-spring](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring) from <https://github.com/heutelbeck/sapl-policy-engine> provides these interfaces and classes
and itself is loaded as dependency within the dependency to [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator#sapl-spring-boot-starter).





## Spring Features

General spring features in this submodule are:

* [Spring Boot](https://projects.spring.io/spring-boot/)
* Standard SQL database: [H2](http://www.h2database.com) (In-Memory), programmable via JPA
* [Hibernate](http://hibernate.org/)
* web interfaces (Rest, UI) with Spring MVC
* model classes (Patient, User, Relation), CrudRepositories in JPA
* [Spring Security](https://projects.spring.io/spring-security/)
* Thymeleaf

## Spring Security Features


We refer to the [Spring Security](https://projects.spring.io/spring-security/) webpage and
its [reference manual](https://docs.spring.io/spring-security/site/docs/5.0.1.BUILD-SNAPSHOT/reference/htmlsingle/).

Successfully implemented features are presented below:

### Http Security

A loginPage, logoutPage is implemented. There is a secured  REST Service. Each request needs authentication.

Example from a class [SecurityConfig.java](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/src/main/java/io/sapl/peembedded/config/SecurityConfig.java):


``` java
@Override
protected void configure(HttpSecurity http) throws Exception {
    LOGGER.debug("start configuring...");
    http
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
            .and()
            .httpBasic()
            .and()
            .csrf().disable();
    http.headers().frameOptions().disable();

}
```



### @Pre and @Post Annotations 

`@Pre` and `@Post` annotations are enabled with `@EnableGlobalMethodSecurity(prePostEnabled=true)`  at an instance of   `WebSecurityConfigurerAdapter` .

``` java
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
....
   }
```




### Permission Evaluator

We refer to the documentation of the  [PermissionEvaluator Interface](https://docs.spring.io/spring-security/site/docs/5.0.2.BUILD-SNAPSHOT/reference/htmlsingle/#el-permission-evaluator). 
`hasPermission()` expressions are delegated to an instance of `PermissionEvaluator` :

``` java
@PreAuthorize("hasPermission(#request, #request)")
```


The `PermissionEvaluator` interface is implemented in the [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLPermissionEvaluator.java), which again is enabled  as bean
in the class [PDPAutoConfiguration](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring-boot-starter/src/main/java/io/sapl/springboot/starter/PDPAutoConfiguration.java)
from submodule [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter).




## SAPLPermissionEvaluator

 
Here is an excerpt of the [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLPermissionEvaluator.java):

```java
public class SAPLPermissionEvaluator implements PermissionEvaluator {

    private SAPLAuthorizator saplAuthorizer;

    @Autowired
    public SAPLPermissionEvaluator(SAPLAuthorizator saplAuthorizer) {
        this.saplAuthorizer = saplAuthorizer;
    }

    @Override //(1.) 
    public boolean hasPermission(Authentication authentication, Object targetDomainObject,
                                  Object permission) {
        return authorize(authentication, permission, targetDomainObject); 
   }

    @Override //(1.)
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                  String targetType, Object permissionText) {
        return false;
    }

    public boolean authorize(Object subject, Object action, Object resource) { 
        LOGGER.trace(
                "Entering hasPermission (Object subject, Object action,
                 Object resource) \n subject: {} \n action {} \n resource: {}",
                subject.getClass(), action.getClass(), resource.getClass());
        if (Authentication.class.isInstance(subject)
              && HttpServletRequest.class.isInstance(action)) {
            Authentication auth = (Authentication) subject;
            Subject authSubject = new AuthenticationSubject(auth);
            HttpServletRequest request = (HttpServletRequest) action;
            Action httpAction = new HttpAction(RequestMethod.valueOf(request.getMethod()));
            Resource httpResource = new HttpResource(request);
            return saplAuthorizer.authorize(authSubject, httpAction, httpResource);//(2.)
        }
        Response response = saplAuthorizer.runPolicyCheck(subject, action, resource); 
        return response.getDecision() == Decision.PERMIT; //(3.)
    }

}
```


1. In a customized PermissionEvaluator always two `hasPermission` methods have to be implemented.

2. `SAPLPermissionEvaluator`  accepts  following _soft-wired_ expression: 
   `hasPermission(#request, #request)`.
   
3. You can also use  _hard-wired_   expressions  like `hasPermission('someResource', 'someAction')`.





An example for securing the `DELETE` method from
the [RestService](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/src/main/java/io/sapl/peembedded/controller/RestService.java) is listed below:

```java
	@DeleteMapping("{id}")
	@PreAuthorize("hasPermission(#request, #request)") // using SaplPolicies = DOCTOR
	public void delete(@PathVariable int id, HttpServletRequest request) {
		patientenRepo.deleteById(id);
	}
```

The corresponding SAPL policy can be found in <https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/src/main/resources/policies/httpPolicy.sapl>  and is implemented as follows:

```
policy "permit_doctor_delete_person"
permit
  action.method == "DELETE"
where
  "DOCTOR" in subject..authority;
  resource.uri =~ "/person/[0-9]+";
```



## Function Library

We use a custom function  providing further information to evaluate a policy. To be recognized as a function library, a class has to be annotated with `@FunctionLibrary`.
Here you can see the [PatientFunction](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-shared/src/main/java/io/sapl/demo/shared/functions/PatientFunction.java) from submodule <https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-shared>.


```java
@FunctionLibrary(name = "patientfunction", description = "")
public class PatientFunction {


    private Optional<RelationRepo> relationRepo = Optional.empty();

    private final ObjectMapper om = new ObjectMapper();

    private RelationRepo getRelationRepo(){
        LOGGER.debug("GetRelationRepo...");
        if(!relationRepo.isPresent()){
            LOGGER.debug("RelRepo not present...");
            ApplicationContext context = ApplicationContextProvider.getApplicationContext();
            LOGGER.debug("Context found: {}", context);
(1.)        relationRepo = Optional.of(ApplicationContextProvider.getApplicationContext().getBean(RelationRepo.class)); 
        }
        LOGGER.debug("Found required instance of RelationRepo: {}", relationRepo.isPresent());
        return relationRepo.get();
    }

    @Function(name = "related")   (2.)
    public JsonNode getRelations ( JsonNode value )  throws FunctionException {
        LOGGER.debug("Entering getRelations");
        List<String> returnList = new ArrayList<>();
        try{
            int id = Integer.parseInt(value.asText());
            LOGGER.debug("Entering getRelations. ID: {}", id);

            returnList.addAll(getRelationRepo().findByPatientid(id).stream()
                    .map(Relation::getUsername)
                    .collect(Collectors.toList()));

        }catch(NumberFormatException e){
            LOGGER.debug("getRelations couldn't parse the value to Int", e);
        }
        JsonNode result = om.convertValue(returnList, JsonNode.class);
        LOGGER.debug("Result: {}", result);
        return result;
    }
}

```

1. The CrudRepository `RelationRepo` has not  been provided as bean at the time if  we want to access it via the Function Library.
    Therefore we use _lazy initialization_ to load it.
    On the other  hand the [ApplicationContextProvider](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-shared/src/main/java/io/sapl/demo/shared/pip/ApplicationContextProvider.java)
    has to be loaded as  bean in submodule `sapl-demo-permeval`
    as you can see in [SecurityConfig](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/src/main/java/io/sapl/peembedded/config/SecurityConfig.java) :

        @Bean
        public ApplicationContextProvider applicationContextProvider(){
            return new ApplicationContextProvider();
        }

    Note: `ApplicationContext` doesn't support `getBean(RelationRepo.class)` if you work with `spring-boot-devtools`.
    
2. The method annotated with `@Function` gives back a list of users who are related to a patient. The corresponding policy looks like this:
         
        policy "permit_relative_see_room_number_with_function"
        permit
         action.method == "viewRoomNumberFunction"
        where
         subject.name in patientfunction.related(resource.id);





The Function Library also has to be imported into the policy set with:

```
import io.sapl.demo.shared.functions.PatientFunction as patientfunction

```



Furthermore, the name of the Function Library has to be notated in the _libraries_ entry of the `pdp.json` as follows:

```json
{
	"algorithm": "DENY_UNLESS_PERMIT",
	"variables": {},
	"attributeFinders": [],
	"libraries": ["patientfunction"]
}
```

## Best Practice

* Implement a Web Application with Spring Boot.
* Add  basic access to all URLs with [Http Security](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator#http-security) from Spring Security.
* Write [SAPL Policies](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator/src/main/resources/policies).
* Add [@Pre and @Post Annotations ](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator#pre-and-post-annotations) using `hasPermission()` expressions 
from [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#saplpermissionevaluator) .