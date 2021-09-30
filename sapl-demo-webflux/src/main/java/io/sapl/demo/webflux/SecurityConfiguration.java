package io.sapl.demo.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.sapl.spring.config.EnableReactiveSaplMethodSecurity;

/**
 * Provide the @EnableReactiveSaplMethodSecurity annotation on any configuration
 * class to activate the reactive method security for methods returning a
 * Publisher<?>.
 */
@EnableReactiveSaplMethodSecurity
public class SecurityConfiguration {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.authorizeExchange().anyExchange().permitAll();
		return http.build();
	}
}