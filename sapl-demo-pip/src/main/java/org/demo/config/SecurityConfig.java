package org.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import io.sapl.spring.PolicyEnforcementFilter;

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Lazy initialization is needed to prevent circular dependency problem caused
	 * by the {@link #configure(HttpSecurity)}-method that needs this field
	 * reference early in startup
	 */
	@Lazy
	@Autowired
	private PolicyEnforcementFilter policyEnforcementFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(policyEnforcementFilter, FilterSecurityInterceptor.class).authorizeRequests()
				.antMatchers("/css/**").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
				.permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll().and()
				.httpBasic().and().csrf().disable();

	}
	
}
