package org.demo.config;

import org.demo.obligationhandlers.CoffeeObligationHandler;
import org.demo.obligationhandlers.EmailObligationHandler;
import org.demo.obligationhandlers.SimpleLoggingObligationHandler;
import org.demo.shared.advicehandlers.EmailAdviceHandler;
import org.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import io.sapl.api.pdp.advice.AdviceHandlerService;
import io.sapl.api.pdp.obligation.ObligationHandlerService;
import io.sapl.pep.pdp.advice.SimpleAdviceHandlerService;
import io.sapl.pep.pdp.obligation.SimpleObligationHandlerService;
import io.sapl.spring.PolicyEnforcementFilter;

@Configuration
@EnableWebSecurity
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

	@Bean
	public ObligationHandlerService getObligationHandlers() {
		ObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;

	}

	@Bean
	public AdviceHandlerService setAdviceHandlers() {
		AdviceHandlerService sahs = new SimpleAdviceHandlerService();
		sahs.register(new EmailAdviceHandler());
		sahs.register(new SimpleLoggingAdviceHandler());
		return sahs;
	}

}
