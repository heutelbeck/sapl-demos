package org.demo;

import org.demo.domain.UserRepository;
import org.demo.shared.marshalling.AuthenticationMapper;
import org.demo.shared.marshalling.HttpServletRequestMapper;
import org.demo.shared.marshalling.PatientMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import io.sapl.api.pdp.mapping.SaplMapper;
import io.sapl.pep.pdp.mapping.SimpleSaplMapper;

@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
		return new ConfigAdapter();
	}

	@Bean
	public AuthenticationManager authManager(UserRepository userRepo) {
		return new AuthnManager(userRepo);
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
