package io.sapl.demo.geo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import io.sapl.demo.geo.AuthManager;
import io.sapl.demo.geo.domain.CrewRepo;
import io.sapl.spring.PolicyEnforcementFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Lazy
	@Autowired
	private PolicyEnforcementFilter policyEnforcementFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(policyEnforcementFilter, FilterSecurityInterceptor.class).authorizeRequests().anyRequest()
				.authenticated().and().httpBasic();
		http.csrf().disable();
	}

	@Bean
	AuthenticationManager authManager(CrewRepo crewRepo) {
		return new AuthManager(crewRepo);
	}

}
