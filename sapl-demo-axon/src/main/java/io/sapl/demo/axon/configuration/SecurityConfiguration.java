package io.sapl.demo.axon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
		// @formatter:off
		http.cors().and().csrf().disable()
			.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
			.formLogin(); 
		return http.build();
		// @formatter:on
	}
}
