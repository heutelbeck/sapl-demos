package io.sapl.demo.axon.config;

/**
 * client profile The required and optional components of the SAPL Axon
 * Client-Side Utilities to use with the application. As the demo is setup as a
 * single project the components which can be used in client facing (frontend)
 * parts of the application are annotated with the 'client' profile.
 */

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.queryhandling.QueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.axon.client.gateway.SaplQueryGateway;
import io.sapl.axon.client.metadata.DefaultSaplCommandInterceptor;
import io.sapl.axon.client.metadata.DefaultSaplQueryInterceptor;

@Configuration
@Profile("client")
public class ClientSideSaplAxonConfiguration {

	/**
	 * Subjects requesting to perform an action (here a command) are required by the
	 * SAPL engine in order to evaluate policies. The DefaultSaplCommandInterceptor
	 * attaches the relevant user information from the
	 * {@link SecurityContextHolder},
	 */
	@Autowired
	public void registerCommandDispatchInterceptors(CommandBus commandBus, ObjectMapper mapper) {
		commandBus.registerDispatchInterceptor(new DefaultSaplCommandInterceptor(mapper));
	}

	/**
	 * The SaplQueryGateway adds additional functionality to handle queries where
	 * the corresponding QueryHandler is annotated with @EnforceRecoverableIfDenied
	 */
	@Bean
    public SaplQueryGateway registerQueryGateWay(QueryBus queryBus, ObjectMapper mapper) {
        var sd = SaplQueryGateway.builder().queryBus(queryBus).build();
        sd.registerDispatchInterceptor(new DefaultSaplQueryInterceptor(mapper)); // - enable subject in monolith-query-handling
        return sd;
    }

	/**
	 * Subjects requesting to perform an action (here a query) are required by the
	 * SAPL engine in order to evaluate policies. The DefaultSaplQueryInterceptor
	 * attaches the relevant user information from the
	 * {@link SecurityContextHolder},
	 */
	@Autowired
	public void registerQueryDispatchInterceptor(QueryBus queryBus, ObjectMapper mapper) {
		queryBus.registerDispatchInterceptor(new DefaultSaplQueryInterceptor(mapper));
	}

}
