# Tutorial  sapl-demo-filterchain

This demo shows how to filter incoming Requests with a `Policy Enforcement Filter` (`PEF`) that uses Sapl Policies. If you use the `Policy Enforcement Filter`, it is a `Policy Enforcement Point` (`PEP`) of your Application. The `PFE` first creates a request for the `SAPLAuthorizator`, which gets a `Response` from the `Policy Decision Point` (`PDP`) and then does the advice and obligation handling and the mapping.

## Tutorial for using the Policy Enforcement Filter

First add the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) to your application as described in the [tutorial](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc).

When using the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) , a bean of type `policyEnforcementFilter` will be automatically provided. In this example we provide the bean to our `SecurityConfig`. Please note that you have to use the `@Lazy` annotation to avoid circular dependency problems:

```java
	@Lazy
	@Autowired
	private PolicyEnforcementFilter policyEnforcementFilter;
```

Then you can configure your `HttpSecurity` to use the `PEF`. Here you can see an example of such a configuration:

```java
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.trace("start configuring...");
		http.addFilterAfter(policyEnforcementFilter, FilterSecurityInterceptor.class).authorizeRequests()
				.antMatchers("/css/**").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll().and()
				.httpBasic().and().csrf().disable();
	}
```

Furthermore, you need to add the following line to your `application.properties`:

```java
	pdp.policyEnforcementFilter=true
```

Now you can filter incoming Requests using Sapl Policies. In the Policy Enforcement Filter the following pattern is used:

```java
boolean permission = sapl.authorize(authentication, request, request);
```

The `Authentication` represents the authenticated user, while the request is the `HttpServletRequest` that runs through the filterchain. It is recommended to use Sapl Mapping to map `Authentication` and `HttpServletRequest` to something you can provide to your policies. You can find more information on Sapl Mapping in the [Introduction](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc). With the mappers used in this example a request could be transformed to something like this:

```json
  subject:{"name":"Julia","authorities":[{"authority":"DOCTOR"}],"details":null} 
  action:{"DELETE"} 
  resource:{"/patient"}
```

This request can be evaluated to `permit` by a policy like that:

```json
policy "permit_doctor_delete_patient"
permit
    action == "DELETE"
where
  "DOCTOR" in subject..authority;
  resource =~ "/patient/*";
```

Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizator`, which can help you write your policies.

