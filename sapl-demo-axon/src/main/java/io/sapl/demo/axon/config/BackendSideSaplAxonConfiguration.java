package io.sapl.demo.axon.config;

import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.axonserver.connector.AxonServerConnectionManager;
import org.axonframework.axonserver.connector.TargetContextResolver;
import org.axonframework.axonserver.connector.query.AxonServerQueryBus;
import org.axonframework.axonserver.connector.query.QueryPriorityCalculator;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryInvocationErrorHandler;
import org.axonframework.queryhandling.QueryMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.sapl.axon.async.DefaultSAPLQueryHandlerEnhancer;
import io.sapl.axon.blocking.DefaultSAPLCommandHandlerEnhancer;
import io.sapl.axon.commandhandling.CommandPolicyEnforcementPoint;
import io.sapl.axon.commandhandling.SaplCommandBus;
import io.sapl.axon.queryhandling.QueryPolicyEnforcementPoint;
import io.sapl.axon.queryhandling.SaplQueryBus;
import io.sapl.axon.queryhandling.SaplQueryUpdateEmitter;

@Configuration
public class BackendSideSaplAxonConfiguration {

	/**
	 * backend profiles The required components of the SAPL Backend Integration to
	 * use with the application. This demo is set up as a single project. Therefore,
	 * components required for the backend, e.g. where the command/query handling
	 * happens, are annotated with a backend profile.
	 * <p>
	 * For the command side 2 different configurations are available and only one of
	 * them can be active at a time: - @Profile("backend_SaplCommandBus"): Leverage
	 * the SAPLCommandBus - @Profile("backend_blocking"): use the
	 * SAPLCommandHandlerEnhancer with any CommandBus Implementation of the Axon
	 * Framework
	 * <p>
	 * For the query side three components are required: -
	 * DefaultSAPLQueryHandlerEnhancer - SaplQueryUpdateEmitter - SaplQueryBus
	 */

	@Bean
	@Profile("backend_SaplCommandBus")
	@Qualifier("localSegment")
	public CommandBus saplCommandBus(CommandPolicyEnforcementPoint pep) {
		CommandBus commandBus = SaplCommandBus.builder().policyEnforcementPoint(pep).build();
		return commandBus;
	}

	@Bean
	@Profile("backend_SaplQueryBus")
	public AxonServerQueryBus registerSaplQueryBusOnAxonServer(AxonServerConnectionManager axonServerConnectionManager,
			AxonServerConfiguration axonServerConfiguration, AxonConfiguration axonConfiguration,
			TransactionManager txManager, @Qualifier("messageSerializer") Serializer messageSerializer,
			Serializer genericSerializer, QueryPriorityCalculator priorityCalculator,
			QueryInvocationErrorHandler queryInvocationErrorHandler,
			TargetContextResolver<? super QueryMessage<?, ?>> targetContextResolver,
			SaplQueryUpdateEmitter queryUpdateEmitter) {
		SaplQueryBus queryBus = SaplQueryBus.builder()
				.messageMonitor(axonConfiguration.messageMonitor(QueryBus.class, "queryBus"))
				.transactionManager(txManager).queryUpdateEmitter(queryUpdateEmitter)
				.errorHandler(queryInvocationErrorHandler).build();
		queryBus.registerHandlerInterceptor(
				new CorrelationDataInterceptor<>(axonConfiguration.correlationDataProviders()));
		return AxonServerQueryBus.builder().axonServerConnectionManager(axonServerConnectionManager)
				.configuration(axonServerConfiguration).localSegment(queryBus)
				.updateEmitter(queryBus.queryUpdateEmitter()).messageSerializer(messageSerializer)
				.genericSerializer(genericSerializer).priorityCalculator(priorityCalculator)
				.targetContextResolver(targetContextResolver).build();
	}

	@Bean
	@Profile("backend_blocking")
	public HandlerEnhancerDefinition registerCommandHandlerEnhancer(CommandPolicyEnforcementPoint pep) {
		return new DefaultSAPLCommandHandlerEnhancer(pep);
	}

	@Bean
	@Profile("backend")
	public HandlerEnhancerDefinition registerQueryHandlerEnhancer(QueryPolicyEnforcementPoint pep) {
		return new DefaultSAPLQueryHandlerEnhancer(pep);
	}

	@Bean
	@Profile("backend")
	public SaplQueryUpdateEmitter registerQueryUpdateEmitter(QueryPolicyEnforcementPoint policyEnforcementPoint) {
		return SaplQueryUpdateEmitter.builder().policyEnforcementPoint(policyEnforcementPoint).build();
	}

	@Bean
	@Profile("SaplQueryBus")
	public QueryBus registerBackendQueryBus(SaplQueryUpdateEmitter queryUpdateEmitter) {
		return SaplQueryBus.builder().queryUpdateEmitter(queryUpdateEmitter).build();
	}

}
