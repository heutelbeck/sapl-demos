package io.sapl.peembedded.config;

import java.util.ArrayList;
import java.util.List;

import io.sapl.demo.domain.User;
import io.sapl.demo.repository.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.debug("start configuring...");
		http
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login").permitAll()
				.and()
				.logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
				.and()
				.httpBasic()
				.and()
				.csrf().disable();
		http.headers().frameOptions().disable();

	}
	
	@Bean 
	public AuthenticationManager authManager(UserRepo userRepo){
		return authentication -> {
			LOGGER.trace("enter authentication manager");
			String username = authentication.getPrincipal().toString();

		    User user = userRepo.findById(username)
		    		.orElseThrow(() -> {
						LOGGER.debug("user {} not found in repo", username);
		    			return new BadCredentialsException("no valid user name provided");
		    		});
		    if (user.isDisabled()) {
		    	LOGGER.debug("user is disabled");
		        throw new DisabledException("user disabled");
		    }
		    String password = authentication.getCredentials().toString();
		    if (!password.equals(user.getPassword())) {
		    	LOGGER.debug("password does not match");
		        throw new BadCredentialsException("user and/or password do not match");
		    }
		    LOGGER.trace("user successfully authenticated, will create UsernamePasswordAuthenticationToken...");
			List<GrantedAuthority> userAuthorities = new ArrayList<>() ;
			user.getFunctions().forEach(function ->  userAuthorities.add(new SimpleGrantedAuthority(function)));
		    return new UsernamePasswordAuthenticationToken(username, password, userAuthorities);

		};
	}

}
