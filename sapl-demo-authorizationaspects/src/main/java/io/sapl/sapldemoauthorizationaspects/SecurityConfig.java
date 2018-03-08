package io.sapl.sapldemoauthorizationaspects;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import io.sapl.demo.repository.UserRepo;
//import io.sapl.demo.shared.marshalling.AuthenticationMapper;
//import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
//import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.spring.marshall.mapper.SaplMapper;
import io.sapl.spring.marshall.mapper.SimpleSaplMapper;

@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {




	@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
		return new ConfigAdapter();
	}

	@Bean
	AuthenticationManager authManager(UserRepo userRepo) {
		return new AuthManager(userRepo);
	}
	
//	@Bean
//	public SaplMapper getSaplMapper() {
//		SaplMapper saplMapper = new SimpleSaplMapper();
//		saplMapper.register(new AuthenticationMapper());
//		saplMapper.register(new HttpServletRequestMapper());
//		saplMapper.register(new PatientMapper());
//		return saplMapper;
//	}

	
}
