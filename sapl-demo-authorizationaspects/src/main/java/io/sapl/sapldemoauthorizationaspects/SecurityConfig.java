package io.sapl.sapldemoauthorizationaspects;

import io.sapl.spring.SAPLAuthorizator;
import io.sapl.spring.annotation.PdpAuthorizeAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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
	PdpAuthorizeAspect pdpAuthorizeAspect(SAPLAuthorizator pep){
		return new PdpAuthorizeAspect(pep);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		//LOGGER.info("Initializing JWT with public key:\n{}", publicKey);
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		//converter.setSigningKey(privateKey);
		//converter.setVerifierKey(publicKey);
		return converter;
	}
}
