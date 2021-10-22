package io.sapl.demo.filterchain;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
		    .and().formLogin()
		          .loginPage("/login")
		          .permitAll()
		    .and().logout()
		          .logoutUrl("/logout")
		          .logoutSuccessUrl("/login")
		          .permitAll()
		    .and().httpBasic()
		    .and().csrf().disable();
		// @formatter:on
	}

}
