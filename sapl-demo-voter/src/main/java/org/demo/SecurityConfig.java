package org.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import io.sapl.spring.SaplAccessDecisionVoter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public AccessDecisionManager getAccessDecisionManager(SaplAccessDecisionVoter saplAccessDecisionVoter) {

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
				// The WebExpressionVoter enables us to use SpEL (Spring Expression Language) to
				// authorize the requests using the @PreAuthorize annotation.
				new WebExpressionVoter(),
				// The RoleVoter votes if any of the configuration attributes starts with the
				// String “ROLE_”.
				new RoleVoter(),
				// The AuthenticatedVoter will cast a vote based on the Authentication object’s
				// level of authentication – specifically looking for either a fully
				// authenticated principal
				new AuthenticatedVoter(),

				// Finally add the autoconfigured SaplBasedVoter
				saplAccessDecisionVoter);

		// Now select a AccessDecisionManager implementation based on the individual
		// votes of all voters are supposed to be combined into a final decision.
		//
		// There are three different choices:
		// - AffirmativeBased – grants access if any of the AccessDecisionVoters return
		// an affirmative vote
		// - ConsensusBased – grants access if there are more affirmative votes than
		// negative (ignoring users who abstain)
		// - UnanimousBased – grants access if every voter either abstains or returns an
		// affirmative vote

		return new UnanimousBased(decisionVoters);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		@SuppressWarnings("deprecation")
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
				.build();
		UserDetails userX = User.withDefaultPasswordEncoder().username("userX").password("password").roles("USER")
				.build();
		UserDetails userY = User.withDefaultPasswordEncoder().username("userY").password("password").roles("USER")
				.build();
		UserDetails userZ = User.withDefaultPasswordEncoder().username("userZ").password("password").roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user, userX, userY, userZ);
	}

	@Configuration
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		AccessDecisionManager accessDecisionManager;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()//.//antMatchers("/", "/home", "public").permitAll().anyRequest().authenticated()
					.accessDecisionManager(accessDecisionManager).and().formLogin().loginPage("/login").permitAll()
					.and().logout().permitAll();
		}
	}

}
