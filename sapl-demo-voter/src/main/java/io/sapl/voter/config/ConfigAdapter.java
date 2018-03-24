package io.sapl.voter.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConfigAdapter extends WebSecurityConfigurerAdapter {

	private final AccessDecisionManager adm;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.trace("start configuring...");
		http.authorizeRequests().antMatchers("/css/**/*.css").permitAll().anyRequest().authenticated()
				.accessDecisionManager(adm).and().formLogin().loginPage("/login").permitAll().and().logout()
				.logoutUrl("/logout").logoutSuccessUrl("/login").permitAll().and().httpBasic().and().csrf().disable();
	}

}
