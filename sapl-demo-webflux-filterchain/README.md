# Demo - Webflux filter chain enforcement

To add a reactive filter, use the custom AuthorizationManager supplied in the SAPL Spring Security packages by supplying the following configuration in you application:

```java

@EnableWebFluxSecurity
@EnableReactiveSaplMethodSecurity
public class SecurityConfiguration {

	@Bean
	public AuthorizationManagerPolicyEnforcementPoint<AuthorizationContext> saplAuthorizationManager(
			PolicyEnforcementPoint pep, AuthorizationSubscriptionBuilderService subBuilder) {
		return new AuthorizationManagerPolicyEnforcementPoint<AuthorizationContext>(subBuilder, pep);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
			AuthorizationManagerPolicyEnforcementPoint<AuthorizationContext> authzManager) {
		// @formatter:off
		return http.authorizeExchange()
				   .pathMatchers("/**").access(authzManager)
			       .and().formLogin()
			       .and().build();
		// @formatter:off
	}
}
```

