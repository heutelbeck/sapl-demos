package io.sapl.sapldemoannotation;

import io.sapl.spring.StandardSAPLAuthorizator;
import io.sapl.spring.annotation.PdpAuthorizeAspect;
import io.sapl.spring.annotation.PdpAuthorizeHttpAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import io.sapl.demo.repository.UserRepo;

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

	@Bean
	PdpAuthorizeAspect pdpAuthorizeAspect(StandardSAPLAuthorizator pep){
		return new PdpAuthorizeAspect(pep);
	}

	@Bean
	PdpAuthorizeHttpAspect pdpAuthorizeHttpAspect(StandardSAPLAuthorizator pep){
		return new PdpAuthorizeHttpAspect(pep);
	}


}
