package io.sapl.jwt.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.sapl.demo.domain.User;
import io.sapl.demo.repository.UserRepo;
import io.sapl.demo.shared.marshalling.AuthenticationMapper;
import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.spring.marshall.mapper.SaplMapper;
import io.sapl.spring.marshall.mapper.SimpleSaplMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAuthorizationServer
@EnableResourceServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	// @Value("${encryptet.testpwd}")
	// private String defaultPassword;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.privateKey}")
	private String privateKey;

	@Value("${jwt.publicKey}")
	private String publicKey;

	@Autowired
	private UserRepo userRepo;


	private static final String ROLE_CLIENT = "ROLE_CLIENT";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Bean
	AuthenticationManager authManager() {
		return authentication -> {
			LOGGER.trace("enter authentication manager");
			String username = authentication.getPrincipal().toString();

			User user = userRepo.findById(username).orElseThrow(() -> {
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
			List<GrantedAuthority> userAuthorities = new ArrayList<>();
			user.getFunctions().forEach(function -> userAuthorities.add(new SimpleGrantedAuthority(function)));
			return new UsernamePasswordAuthenticationToken(username, password, userAuthorities);

		};
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		LOGGER.info("Initializing JWT with public key:\n{}", publicKey);
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		converter.setVerifierKey(publicKey);
		return converter;
	}
	
	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SimpleSaplMapper();
		saplMapper.register(new AuthenticationMapper());
		saplMapper.register(new HttpServletRequestMapper());
		saplMapper.register(new PatientMapper());
		return saplMapper;
		
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
				.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).accessTokenConverter(accessTokenConverter());
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		Map<String, Object> claims = new HashMap<>();
		claims.put("homeGatewayAccess", Arrays.asList("0123456", "6543210", "deadbeef"));
		clients.inMemory().withClient("my-client-with-registered-redirect").authorizedGrantTypes("authorization_code")
				.authorities(ROLE_CLIENT).scopes("read", "trust").redirectUris("http://anywhere?key=value").and()
				.withClient("testingClient").authorizedGrantTypes("client_credentials", "password")
				.authorities(ROLE_CLIENT, "ROLE_TRUSTED_CLIENT").additionalInformation(claims).scopes("read", "write")
				.secret(secret);
	}

}
