package io.sapl.demo.axon.config;

import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.mongodb.client.MongoClient;
import com.thoughtworks.xstream.XStream;

@Configuration
@Profile("mongodb")
public class MongoEventStoreConfiguration {
	/**
	 * mongodb profile
	 * If MongoDb is chosen as an event store the beans annotated with the 'mongodb' profile provide
	 * the required configuration. The Axon Framework Extension for MongoDb is used here.
	 * For more information see https://docs.axoniq.io/reference-guide/extensions/mongo
	 **/

	@Bean
	public EmbeddedEventStore eventStore(EventStorageEngine storageEngine,
										 org.axonframework.config.Configuration configuration) {
		return EmbeddedEventStore.builder().storageEngine(storageEngine)
				.messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore")).build();
	}

	@Bean
	public EventStorageEngine storageEngine(MongoClient client, XStream xStream) {
		var securedSerializer = XStreamSerializer.builder().xStream(xStream).build();
		return MongoEventStorageEngine.builder().eventSerializer(securedSerializer)
				.snapshotSerializer(securedSerializer)
				.mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();
	}

}
