# Demo: Using SAPL in the filter chain (non-reactive webserver)

This demo shows how to filter incoming Requests with an `AuthorizationManagerr` that uses a SAPL PDP.

The `SaplAuthorizationManager` is automatically deployed for servlet-based applications using the `sapl-spring-security` module as a dependency. It can be added to the application as follows:

```java
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, SaplAuthorizationManager saplAuthzManager) throws Exception {
		return http.authorizeHttpRequests(requests -> 
						requests.anyRequest()
				                .access(saplAuthzManager)
				    )
				   .formLogin(withDefaults())
				   .httpBasic(withDefaults())
				   .build();
	}
```

Now all requests identified in the filter chain configuration to be subject to the `SaplAuthorizationManager` will issue an authorization request to the PDP before proceeding.
