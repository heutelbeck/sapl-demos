package io.sapl.demo.filterchain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import io.sapl.demo.domain.UserRepo;
import io.sapl.demo.filterchain.AuthManager;
import io.sapl.demo.filterchain.obligationhandlers.CoffeeObligationHandler;
import io.sapl.demo.filterchain.obligationhandlers.EmailObligationHandler;
import io.sapl.demo.filterchain.obligationhandlers.SimpleLoggingObligationHandler;
import io.sapl.demo.shared.advicehandlers.EmailAdviceHandler;
import io.sapl.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import io.sapl.demo.shared.marshalling.AuthenticationMapper;
import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.spring.PolicyEnforcementFilter;
import io.sapl.spring.marshall.advice.SimpleAdviceHandlerService;
import io.sapl.spring.marshall.mapper.SaplMapper;
import io.sapl.spring.marshall.mapper.SimpleSaplMapper;
import io.sapl.spring.marshall.obligation.SimpleObligationHandlerService;

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
	public SimpleObligationHandlerService getObligationHandlers() {
		SimpleObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;

	}

	@Bean
	public SimpleAdviceHandlerService setAdviceHandlers() {
		SimpleAdviceHandlerService sahs = new SimpleAdviceHandlerService();
		sahs.register(new EmailAdviceHandler());
		sahs.register(new SimpleLoggingAdviceHandler());
		return sahs;
	}

	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SimpleSaplMapper();
		saplMapper.register(new AuthenticationMapper());
		saplMapper.register(new HttpServletRequestMapper());
		saplMapper.register(new PatientMapper());
		return saplMapper;

	}

	@Bean
	AuthenticationManager authManager(UserRepo userRepo) {
		return new AuthManager(userRepo);
	}

}
