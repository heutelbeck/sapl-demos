package org.demo.security;

import org.demo.domain.PrinterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String LOGIN_PROCESSING_URL = "/login";

	private static final String LOGIN_FAILURE_URL = "/login";

	private static final String LOGIN_URL = "/login";

	private static final String LOGOUT_SUCCESS_URL = "/login";

	@Autowired
	private PrinterUserService printerUserService;

	/**
	 * Require login to access internal pages and configure login form.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().requestCache().requestCache(new CustomRequestCache()).and().authorizeRequests()
				.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
				.failureUrl(LOGIN_FAILURE_URL).and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(printerUserService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/VAADIN/**", "/favicon.ico", "/robots.txt", "/manifest.webmanifest", "/sw.js",
				"/offline-page.html", "/icons/**", "/images/**", "/frontend/**", "/webjars/**", "/h2-console/**",
				"/frontend-es5/**", "/frontend-es6/**");
	}

}
