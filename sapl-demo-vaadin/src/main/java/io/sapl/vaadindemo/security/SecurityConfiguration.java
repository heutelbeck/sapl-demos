package io.sapl.vaadindemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import io.sapl.spring.config.EnableSaplMethodSecurity;
import io.sapl.vaadin.base.VaadinAuthorizationSubscriptionBuilderService;
import io.sapl.vaadindemo.views.LoginView;

@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
public class SecurityConfiguration extends VaadinWebSecurity {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				requests -> requests.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

		// Icons from the line-awesome addon
		http.authorizeHttpRequests(
				requests -> requests.requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());
		super.configure(http);
		setLoginView(http, LoginView.class);
	}

	/**
	 * Demo UserDetailService which only provide two hard coded in memory users and
	 * their roles. NOTE: This should not be used in real world applications.
	 */
	@Bean
	UserDetailsService userDetailsService() {
		UserDetails admin = User.withUsername("admin")
				.password("$2a$12$wuM1Cmdn4e0eTZfWrqSk0.Q82N3S6ehvj7/jzdxUH5xuthcvvlKCW").roles("Admin").build();
		UserDetails user  = User.withUsername("user")
				.password("$2a$12$itBzi/0MWsalfjnrftIO9eQ6lifIn61K77A3/UbNMAC9IVEtVmnvW").roles("USER").build();
		return new InMemoryUserDetailsManager(admin, user);
	}

	@Bean
	protected VaadinAuthorizationSubscriptionBuilderService vaadinAuthorizationSubscriptionBuilderService(
			ObjectMapper mapper) {
		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
		return new VaadinAuthorizationSubscriptionBuilderService(expressionHandler, mapper);
	}
}
