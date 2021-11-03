# Demo: Using SAPL in the filter chain (non-reactive webserver)

This demo shows how to filter incoming Requests with a `Policy Enforcement Filter` (`PEF`) that uses Sapl Policies. If you use the `Policy Enforcement Filter`, it is a `Policy Enforcement Point` (`PEP`) in your Application. The `PFE` first creates a request for the `SAPLAuthorizer`, which gets a `Response` from the `Policy Decision Point` (`PDP`) and then does the advice and obligation handling and the mapping.

When using the [SAPL Spring Security Integration](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-security) , a bean of type `SaplFilterPolicyEnforcementPoint` will be automatically provided, if the following parameter is set in the `application.properties`:

```java
pdp.policyEnforcementFilter=true
```

Spring security will automatically pick up the bean and insert it in its filter chain. 

Attention: If you manually configure your `HttpSecurity` to use the `PEF` the following way, you will end up with the filter to be present twice in the filter chain.

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


Now you can filter incoming Requests using Sapl Policies. In the Policy Enforcement Filter, the following pattern is used:

```java
boolean permission = sapl.authorize(authentication, request, request);
```

The `Authentication` represents the authenticated user, while the request is the `HttpServletRequest` that runs through the filter chain. 
Developers can customize the mapping of the requests to match the domain-specific needs of an application. 

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
