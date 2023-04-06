# Demo - Webflux filter chain enforcement

To add a reactive filter, use the custom AuthorizationManager supplied in the SAPL Spring Security packages by supplying the following configuration in the application:

```java
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final ReactiveSaplAuthorizationManager saplAuthzManager;

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		return http.authorizeExchange(exchange -> 
		            	exchange.anyExchange()
				                .access(saplAuthzManager)
				    )
				   .formLogin(withDefaults())
				   .httpBasic(withDefaults())
				   .build();
		// @formatter:off
	}
	
}
```

# How to use the demo:

The URL http://localhost:8080/public is accessible without login.

The URL http://localhost:8080/secret is only accessible with login.

The username: user

The password: user

Logout URL: http://localhost:8080/logout
