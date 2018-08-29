package io.sapl.demo;

import io.sapl.demo.domain.UserRepo;
import io.sapl.demo.shared.advicehandlers.EmailAdviceHandler;
import io.sapl.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import io.sapl.demo.shared.marshalling.AuthenticationMapper;
import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.demo.shared.obligationhandlers.CoffeeObligationHandler;
import io.sapl.demo.shared.obligationhandlers.EmailObligationHandler;
import io.sapl.demo.shared.obligationhandlers.SimpleLoggingObligationHandler;
import io.sapl.demo.shared.pip.ApplicationContextProvider;
import io.sapl.spring.marshall.advice.SimpleAdviceHandlerService;
import io.sapl.spring.marshall.mapper.SaplMapper;
import io.sapl.spring.marshall.mapper.SimpleSaplMapper;
import io.sapl.spring.marshall.obligation.SimpleObligationHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**/*.css").permitAll().anyRequest().authenticated().and().formLogin()
				.loginPage("/login").permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
				.permitAll().and().httpBasic().and().csrf().disable();
		http.headers().frameOptions().disable();

	}

	@Bean
	public AuthenticationManager authManager(UserRepo userRepo) {
		return new AuthManager(userRepo);
	}

	@Bean
	public ApplicationContextProvider applicationContextProvider() {
		return new ApplicationContextProvider();
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

}
