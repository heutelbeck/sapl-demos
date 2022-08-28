package io.sapl.demo.axon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http.cors().and().csrf().disable()
			.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
			.formLogin(); 
		return http.build();
		// @formatter:on
	}
}
