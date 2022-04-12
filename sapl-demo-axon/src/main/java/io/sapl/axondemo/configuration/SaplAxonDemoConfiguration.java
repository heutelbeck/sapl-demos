package io.sapl.axondemo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.thoughtworks.xstream.XStream;
import io.sapl.axon.async.DefaultSAPLQueryHandlerEnhancer;
import io.sapl.axon.blocking.DefaultSAPLCommandHandlerEnhancer;
import io.sapl.axon.client.gateway.SaplQueryGateway;
import io.sapl.axon.client.metadata.DefaultSaplCommandInterceptor;
import io.sapl.axon.client.metadata.DefaultSaplQueryInterceptor;
import io.sapl.axon.commandhandling.CommandPolicyEnforcementPoint;
import io.sapl.axon.commandhandling.SaplCommandBus;
import io.sapl.axon.queryhandling.QueryPolicyEnforcementPoint;
import io.sapl.axon.queryhandling.SaplQueryBus;
import io.sapl.axon.queryhandling.SaplQueryUpdateEmitter;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.axonserver.connector.AxonServerConnectionManager;
import org.axonframework.axonserver.connector.TargetContextResolver;
import org.axonframework.axonserver.connector.query.AxonServerQueryBus;
import org.axonframework.axonserver.connector.query.QueryPriorityCalculator;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryInvocationErrorHandler;
import org.axonframework.queryhandling.QueryMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.TimeUnit;

@Configuration
public class SaplAxonDemoConfiguration {

	/**
	 * mongodb profile
	 * If MongoDb is chosen as an event store the beans annotated with the 'mongodb' profile provide
	 * the required configuration. The Axon Framework Extension for MongoDb is used here.
	 * For more information see https://docs.axoniq.io/reference-guide/extensions/mongo
	 **/

	@Bean
	@Profile("mongodb")
	public EmbeddedEventStore eventStore(EventStorageEngine storageEngine,
										 org.axonframework.config.Configuration configuration) {
		return EmbeddedEventStore.builder().storageEngine(storageEngine)
				.messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore")).build();
	}

	@Bean
	@Profile("mongodb")
	public EventStorageEngine storageEngine(MongoClient client, XStream xStream) {
		var securedSerializer = XStreamSerializer.builder().xStream(xStream).build();
		return MongoEventStorageEngine.builder().eventSerializer(securedSerializer)
				.snapshotSerializer(securedSerializer)
				.mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();
	}

	@Bean
	@Profile("mongodb")
	public TokenStore mongoTokenStore(MongoClient client) {
		var tokenSerializer = XStreamSerializer.builder().xStream(new XStream()).build();
		return MongoTokenStore.builder().serializer(tokenSerializer)
				.mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();

	}


	// ensure that the Xstream instance is allows to serialize and deserialize the required classes
	@Autowired
	public void xStream(XStream xStream) {
		xStream.allowTypesByWildcard(new String[]{"io.sapl.**", "com.fasterxml.jackson.databind.node.**"});
	}

	/**
	 * event processing configuration
	 * Configure the tracking event processor of the Axon Framework.
	 * https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors/streaming
	 */

	@Autowired
	public void configureProcessorDefault(EventProcessingConfigurer processingConfigurer) {
		processingConfigurer.usingTrackingEventProcessors();
	}

	@Autowired
	public void configureInitialAndTokenClaimValues(EventProcessingConfigurer processingConfigurer) {
		TrackingEventProcessorConfiguration tepConfig = TrackingEventProcessorConfiguration
				.forSingleThreadedProcessing().andTokenClaimInterval(1000, TimeUnit.MILLISECONDS)
				.andInitialTrackingToken(StreamableMessageSource::createTailToken)
				.andEventAvailabilityTimeout(2000, TimeUnit.MILLISECONDS);

		processingConfigurer.registerTrackingEventProcessorConfiguration(config -> tepConfig)
				.registerTrackingEventProcessorConfiguration("medicalRecordProjection", config -> tepConfig);
	}

	/**
	 * backend profiles
	 * The required components of the SAPL Backend Integration to use with the application. This demo is set up
	 * as a single project. Therefore, components required for the backend,
	 * e.g. where the command/query handling happens, are annotated with a backend profile.
	 * <p>
	 * For the command side 2 different configurations are available and only one of them can be active at a
	 * time:
	 * - @Profile("backend_SaplCommandBus"): Leverage the SAPLCommandBus
	 * - @Profile("backend_blocking"): use the SAPLCommandHandlerEnhancer with any CommandBus Implementation
	 * of the Axon Framework
	 * <p>
	 * For the query side three components are required:
	 * - DefaultSAPLQueryHandlerEnhancer
	 * - SaplQueryUpdateEmitter
	 * - SaplQueryBus
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
															   AxonServerConfiguration axonServerConfiguration,
															   AxonConfiguration axonConfiguration,
															   TransactionManager txManager,
															   @Qualifier("messageSerializer") Serializer messageSerializer,
															   Serializer genericSerializer,
															   QueryPriorityCalculator priorityCalculator,
															   QueryInvocationErrorHandler queryInvocationErrorHandler,
															   TargetContextResolver<? super QueryMessage<?, ?>> targetContextResolver,
															   SaplQueryUpdateEmitter queryUpdateEmitter) {
		SaplQueryBus queryBus = SaplQueryBus.builder()
				.messageMonitor(axonConfiguration.messageMonitor(QueryBus.class, "queryBus"))
				.transactionManager(txManager)
				.queryUpdateEmitter(queryUpdateEmitter)
				.errorHandler(queryInvocationErrorHandler)
				.build();
		queryBus.registerHandlerInterceptor(
				new CorrelationDataInterceptor<>(axonConfiguration.correlationDataProviders())
		);
		return AxonServerQueryBus.builder()
				.axonServerConnectionManager(axonServerConnectionManager)
				.configuration(axonServerConfiguration)
				.localSegment(queryBus)
				.updateEmitter(queryBus.queryUpdateEmitter())
				.messageSerializer(messageSerializer)
				.genericSerializer(genericSerializer)
				.priorityCalculator(priorityCalculator)
				.targetContextResolver(targetContextResolver)
				.build();
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

		SaplQueryBus saplQueryBus = SaplQueryBus.builder().queryUpdateEmitter(queryUpdateEmitter).build();

		return saplQueryBus;
	}


	/** client profile
	 * The required and optional components of the SAPL Axon Client-Side Utilities to use with the application.
	 * As the demo is setup as a single project the components which can be used in client facing (frontend)
	 * parts of the application are annotated with the 'client' profile.
	 */

	/**
	 * Subjects requesting to perform an action (here a command) are required by the SAPL engine in order
	 * to evaluate policies. The DefaultSaplCommandInterceptor attaches the relevant user information from the
	 * {@link SecurityContextHolder},
	 */
	@Autowired
	@Profile("client")
	public void registerCommandDispatchInterceptors(CommandBus commandBus, ObjectMapper mapper) {
		commandBus.registerDispatchInterceptor(new DefaultSaplCommandInterceptor(mapper));
	}

	/**
	 * The SaplQueryGateway adds additional functionality to handle queries where the corresponding
	 * QueryHandler is annotated with @EnforceRecoverableIfDenied
	 */
	@Bean
	@Profile("client")
	public SaplQueryGateway registerQueryGateWay(QueryBus queryBus) {
		SaplQueryGateway queryGateway = SaplQueryGateway.builder().queryBus(queryBus).build();
		return queryGateway;
	}

	/**
	 * Subjects requesting to perform an action (here a query) are required by the SAPL engine in order
	 * to evaluate policies. The DefaultSaplQueryInterceptor attaches the relevant user information from the
	 * {@link SecurityContextHolder},
	 */
	@Autowired
	@Profile("client")
	public void registerQueryDispatchInterceptor(QueryBus queryBus, ObjectMapper mapper) {
		queryBus.registerDispatchInterceptor(new DefaultSaplQueryInterceptor(mapper));
	}

	// ObjectMapper instance for the DispatchInterceptors
	@Bean
	@Profile("client")
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}

}
