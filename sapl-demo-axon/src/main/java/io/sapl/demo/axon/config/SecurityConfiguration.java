package io.sapl.demo.axon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable();
    }
	
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		// @formatter:off
        UserDetails axon1 =
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("pwd")
                        .roles("ADMIN")
                        .build();
        UserDetails axon2 =
                User.withDefaultPasswordEncoder()
                        .username("user2")
                        .password("pwd")
                        .roles("USER")
                        .build();
        UserDetails axon3 =
                User.withDefaultPasswordEncoder()
                        .username("Jones")
                        .password("pwd")
                        .roles("PATIENT")
                        .build();
        UserDetails axon4 =
                User.withDefaultPasswordEncoder()
                        .username("doctorOnProbation")
                        .password("pwd")
                        .roles("USER")
                        .build();
        UserDetails axon5 =
                User.withDefaultPasswordEncoder()
                        .username("user5")
                        .password("pwd")
                        .roles("USER")
                        .build();
        UserDetails axon6 =
                User.withDefaultPasswordEncoder()
                        .username("nurse")
                        .password("pwd")
                        .roles("NURSE")
                        .build();
        UserDetails externalService =
                User.withDefaultPasswordEncoder()
                        .username("externalService")
                        .password("pwd")
                        .roles("EXTERNAL_SERVICE")
                        .build();
		// @formatter:on
		return new InMemoryUserDetailsManager(axon1, axon2, axon3, axon4, axon5, axon6, externalService);
	}

}
