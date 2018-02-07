package io.sapl.demo.filterchain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import io.sapl.demo.filterchain.AuthManager;
import io.sapl.demo.filterchain.obligationhandlers.CoffeeObligationHandler;
import io.sapl.demo.filterchain.obligationhandlers.EmailObligationHandler;
import io.sapl.demo.filterchain.obligationhandlers.SimpleLoggingObligationHandler;
import io.sapl.demo.repository.UserRepo;
import io.sapl.spring.PolicyEnforcementFilter;
import io.sapl.spring.StandardSAPLAuthorizator;
import io.sapl.spring.marshall.obligation.SimpleObligationHandlerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity // (debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	StandardSAPLAuthorizator saplAuthorizer;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.trace("start configuring...");
		http.addFilterAfter(policyEnforcementFilter(), FilterSecurityInterceptor.class).authorizeRequests().anyRequest()
				.authenticated().and().formLogin().loginPage("/login").permitAll().and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/login").permitAll().and().httpBasic().and().csrf().disable();
	}

	@Bean
	public PolicyEnforcementFilter policyEnforcementFilter() {
		return new PolicyEnforcementFilter(saplAuthorizer);
	}

	@Bean
	public SimpleObligationHandlerService getObligationHandlers() {
		SimpleObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;
	}

	@Bean
	AuthenticationManager authManager(UserRepo userRepo) {
		return new AuthManager(userRepo);
	}

}
