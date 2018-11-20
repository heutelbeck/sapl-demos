package io.sapl.demo;

import io.sapl.api.pdp.mapping.SaplMapper;
import io.sapl.api.pdp.mapping.SimpleSaplMapper;
import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * This configuration is responsible for securing the RESTful API using OAuth2.
 *
 * The @EnableResourceServer on the class switches spring security from its
 * default authentication to be required to OAuth2. Currently all URLs are
 * covered by OAuth. See annotation documentation on how to customize this. A
 * resource server does not have to have a authorization server at the same time
 * and vice versa.
 */

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfiguration {

	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SimpleSaplMapper();
		// saplMapper.register(new AuthenticationMapper());
		saplMapper.register(new HttpServletRequestMapper());
		// saplMapper.register(new PatientMapper());
		return saplMapper;

	}

	// @Bean
	// public SimpleObligationHandlerService getObligationHandlers() {
	// SimpleObligationHandlerService sohs = new SimpleObligationHandlerService();
	// sohs.register(new EmailObligationHandler());
	// sohs.register(new CoffeeObligationHandler());
	// sohs.register(new SimpleLoggingObligationHandler());
	// return sohs;
	// }
	//
	// @Bean
	// public SimpleAdviceHandlerService setAdviceHandlers() {
	// SimpleAdviceHandlerService sahs = new SimpleAdviceHandlerService();
	// sahs.register(new EmailAdviceHandler());
	// sahs.register(new SimpleLoggingAdviceHandler());
	// return sahs;
	// }

}
