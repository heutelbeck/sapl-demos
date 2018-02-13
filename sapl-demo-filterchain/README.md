# Submodule  sapl-demo-filterchain

This demo shows how to filter incoming Requests with a Policy Enforcement Filter that uses Sapl Policies. 

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

## Spring Features

General spring features in this submodule are:

* [Spring Boot](https://projects.spring.io/spring-boot/)
* Standard SQL database: [H2](http://www.h2database.com) (In-Memory), programmable via JPA
* [Hibernate](http://hibernate.org/)
* web interfaces (Rest, UI) with Spring MVC
* model classes (Patient, User, Relation), CrudRepositories in JPA
* [Spring Security](https://projects.spring.io/spring-security/)
* Thymeleaf


## The Policy Enforcement Filter

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
boolean permission = sapl.authorize(new AuthenticationSubject(authentication), new HttpAction(req),
					new HttpResource(req));
```

Therefore a request could be transformed to something like this:

```json
  subject:{"name":"Julia","authorities":[{"authority":"DOCTOR"}],"details":null} 
  action:{"method":"DELETE"} 
  resource:{"uri":"/patient","uriLowerCased":"/patient"}
```

Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `StandardSAPLAuthorizator`, which can help you write your policies.

