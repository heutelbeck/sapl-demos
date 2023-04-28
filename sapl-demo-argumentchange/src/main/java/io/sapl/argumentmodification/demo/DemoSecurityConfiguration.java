package io.sapl.argumentmodification.demo;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DemoSecurityConfiguration {

	static final String         DEMO_USER      = "demoUser";
	static final String         DEMO_PASSWORD  = "demoPassword";
	private static final String DEMO_AUTHORITY = "demoAuthority";

	@Bean
	@Primary
	UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		var testUser = new User(DEMO_USER, passwordEncoder.encode(DEMO_PASSWORD),
				List.of(new SimpleGrantedAuthority(DEMO_AUTHORITY)));
		log.info("");
		log.info("Generating demo user: {}", testUser);
		log.info("use username : '{}' and password '{}' for login", DEMO_USER, DEMO_PASSWORD);
		log.info("");
		return new InMemoryUserDetailsManager(testUser);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable(); // for demo purposes only!
							   // do not disable csrf in your application unless strictly necessary.
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equals(rawPassword);
			}

			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}
		};
	}
}
