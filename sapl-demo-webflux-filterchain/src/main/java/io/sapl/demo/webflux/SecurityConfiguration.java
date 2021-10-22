package io.sapl.demo.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import io.sapl.spring.config.EnableReactiveSaplMethodSecurity;
import io.sapl.spring.filter.AuthorizationManagerPolicyEnforcementPoint;
import io.sapl.spring.pep.PolicyEnforcementPoint;
import io.sapl.spring.subscriptions.AuthorizationSubscriptionBuilderService;

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
