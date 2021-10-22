package io.sapl.demo.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.sapl.spring.config.EnableReactiveSaplMethodSecurity;

/**
 * Provide the @EnableReactiveSaplMethodSecurity annotation on any configuration
 * class to activate the reactive method security for methods returning a
 * Publisher<?>.
 */
@EnableWebFluxSecurity
@EnableReactiveSaplMethodSecurity
public class SecurityConfiguration {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		return http.authorizeExchange()
				   .anyExchange()
				   .permitAll()
				   .and().build();
		// @formatter:off

	}
}
