/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
