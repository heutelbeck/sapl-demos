# Tutorial  sapl-demo-permissionevaluator

**Contents**


* [Spring Security Features](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#spring-security-features)
* [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#saplpermissionevaluator)
* [Function Library](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#function-library)
* [Best Practice](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#best-practice)



The sub-project sapl-demo-permissionevaluator makes extensive use of the [PermissionEvaluator Interface](https://docs.spring.io/spring-security/site/docs/5.0.2.BUILD-SNAPSHOT/reference/htmlsingle/#el-permission-evaluator) from [Spring Security](https://projects.spring.io/spring-security/).
The most important features are given below. Please note that we are using Lombok logging for all Demo Projects.




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
            .antMatchers("/css/**/*.css").permitAll()
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
from module [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter).




## SAPLPermissionEvaluator

 
Here is an excerpt of the [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLPermissionEvaluator.java):

```java
@Component
public class SAPLPermissionEvaluator implements PermissionEvaluator {

	private SAPLAuthorizer sapl;

	@Autowired
	public SAPLPermissionEvaluator(SAPLAuthorizer saplAuthorizer) {
		this.sapl = saplAuthorizer;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return sapl.authorize(authentication, permission, targetDomainObject);

	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permissionText) {
		return false;
	}

}
```


* In a customized PermissionEvaluator always two `hasPermission` methods have to be implemented.

* `SAPLPermissionEvaluator`  accepts  following _soft-wired_ expression: 
   `hasPermission(#request, #request)`. A `HttpServletRequest` object is mapped  as `action` and `resource` (cf. [The Sapl Mapper](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc#the-sapl-mapper)).
   
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
      action == "DELETE"
    where
      "DOCTOR" in subject..authority;
      resource =~ "/person/[0-9]+";
    ```
    This policy  gives permission to the authority `DOCTOR`.
    
* You can also write  _hard-wired_   expressions like `hasPermission('someResource', 'someAction')`. For example `hasPermission('HRN', 'read')` can be used with following policy:

        policy "permit_doctor_read_HRN"
        permit
            action == "read"
        where
          "DOCTOR" in subject..authority;
          resource == "HRN";
    
    This policy also gives permission to the authority `DOCTOR`. 


## Function Library

We use a custom function  providing further information to evaluate a policy. To be recognized as a function library, a class has to be annotated with `@FunctionLibrary`.
Here you can see the [PatientFunction](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-shared/src/main/java/io/sapl/demo/shared/functions/PatientFunction.java) from module <https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-shared>.


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
    has to be loaded as  bean in this sub-project 
    as you can see in [SecurityConfig](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/src/main/java/io/sapl/peembedded/config/SecurityConfig.java) :

        @Bean
        public ApplicationContextProvider applicationContextProvider(){
            return new ApplicationContextProvider();
        }

   Tip: Try to disable  `spring-boot-devtools` if `ApplicationContext` doesn't support `getBean(RelationRepo.class)`.
    
2. The method annotated with `@Function` gives back a list of users who are related to a patient. The corresponding policy looks like this:
         
        policy "permit_relative_see_room_number_with_function"
        permit
         action == "viewRoomNumberFunction"
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

* Add Maven dependency to [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc#sapl-spring-boot-starter).
* Implement a Web Application with Spring Boot.
* Add  basic access to all URLs with [Http Security](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator#http-security) from Spring Security.
* Write [SAPL Policies](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator/src/main/resources/policies) matching your purposes, which requires basic understanding of [SAPL](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-documentation/src/asciidoc/sapl-reference.adoc) .
* Add [@Pre and @Post Annotations ](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-permissionevaluator#pre-and-post-annotations) using `hasPermission()` expressions 
from [SAPLPermissionEvaluator](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-permissionevaluator/README.md#saplpermissionevaluator) .
