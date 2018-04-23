package io.sapl.demo;

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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.sapl.demo.domain.User;
import io.sapl.demo.domain.UserRepo;

/**
 * This configuration sets up a simple authorization server providing OAuth2 and
 * JWT functionality. If you have a stand-alone RESTful service and a dedicated
 * OAuth authorization server, these services do not have to be instantiated in
 * order to have an API secured with OAuth2 and JWT.
 */
@Configuration
@EnableAuthorizationServer
@SuppressWarnings("deprecation")
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private static final String ROLE_CLIENT = "ROLE_CLIENT";

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.privateKey}")
	private String privateKey;

	@Value("${jwt.publicKey}")
	private String publicKey;

	// user repository of the demo domain model
	@Autowired
	private UserRepo userRepo;

	// Use the user repository for authentication
	@Bean
	AuthenticationManager authenticationManager() {
		return authentication -> {
			String username = authentication.getPrincipal().toString();

			User user = userRepo.findById(username).orElseThrow(() -> {
				return new BadCredentialsException("user and/or password do not match");
			});
			if (user.isDisabled()) {
				throw new DisabledException("user disabled");
			}
			String password = authentication.getCredentials().toString();
			// Plain-text passwords are used. Do not do this in production.
			// TODO: use bcrypt in demo as well. show best practices!
			if (!password.equals(user.getPassword())) {
				throw new BadCredentialsException("user and/or password do not match");
			}
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
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		converter.setVerifierKey(publicKey);
		return converter;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
				.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
				.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager()).accessTokenConverter(accessTokenConverter());
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
