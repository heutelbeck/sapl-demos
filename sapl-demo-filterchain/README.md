# Tutorial  sapl-demo-filterchain

This demo shows how to filter incoming Requests with a Policy Enforcement Filter (PEF) that uses Sapl Policies. If you use the Policy Enforcement Filter, it is a Policy Enforcement Point (PEP) of your Application. The PFE sends first creates a request for the `SAPLAuthorizator`, which gets a `Response` from the Policy Decision Point (PDP) and then does the advice and obligation handling and the mapping.

## Tutorial for using the Policy Enforcement Filter

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


When using the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) , a bean of type `policyEnforcementFilter` will be automatically provided. To use it, you only have to add it to your `SecurityConfig` with the line `http.addFilterAfter(policyEnforcementFilter(), FilterSecurityInterceptor.class)`. Here you can see an example of a complete configuration:

```java
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.trace("start configuring...");
		http.addFilterAfter(policyEnforcementFilter(), FilterSecurityInterceptor.class)
		.authorizeRequests().anyRequest().authenticated()
		.and().formLogin().loginPage("/login").permitAll()
		.and().logout().logoutUrl("/logout")
		.logoutSuccessUrl("/login").permitAll().and().httpBasic().and().csrf().disable();
	}
```

Furthermore, you need to provide the PolicyEnforcementFilter to the application:

```java
	@Bean
	public PolicyEnforcementFilter policyEnforcementFilter() {
		return new PolicyEnforcementFilter(saplAuthorizer);
	}
```

Now you can filter incoming Requests using Sapl Policies. In the Policy Enforcement Filter the following pattern is used:

```java
boolean permission = sapl.authorize(authentication, request, request);
```

The `Authentication` represents the authenticated user, while the request is the `HttpServletRequest` that runs through the filterchain. It is recommended to use Sapl Mapping to map `Authentication` and `HttpServletRequest` to seomthing you can provide to your policies. You can find more information on Sapl Mapping in the [Introduction](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc). With the mappers used in this example a request could be transformed to something like this:

```json
  subject:{"name":"Julia","authorities":[{"authority":"DOCTOR"}],"details":null} 
  action:{"method":"DELETE"} 
  resource:{"uri":"/patient","uriLowerCased":"/patient"}
```

Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizator`, which can help you write your policies.

