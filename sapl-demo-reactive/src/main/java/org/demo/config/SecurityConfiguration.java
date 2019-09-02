package org.demo.config;

import org.demo.domain.DemoData;
import org.demo.security.VaadinSessionSecurityContextHolderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	static {
		SecurityContextHolder.setStrategyName(
				VaadinSessionSecurityContextHolderStrategy.class.getName());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
	}

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMem = auth
				.inMemoryAuthentication();
		DemoData.loadUsers(inMem, passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
