package io.sapl.demo;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigAdapter extends WebSecurityConfigurerAdapter {

	private final AccessDecisionManager adm;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**/*.css").permitAll().anyRequest().authenticated()
				.accessDecisionManager(adm).and().formLogin().loginPage("/login").permitAll().and().logout()
				.logoutUrl("/logout").logoutSuccessUrl("/login").permitAll().and().httpBasic().and().csrf().disable();
	}

}
