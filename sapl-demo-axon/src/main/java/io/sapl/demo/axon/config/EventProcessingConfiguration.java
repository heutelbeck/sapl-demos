package io.sapl.demo.axon.config;

import java.util.concurrent.TimeUnit;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventProcessingConfiguration {
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
}
