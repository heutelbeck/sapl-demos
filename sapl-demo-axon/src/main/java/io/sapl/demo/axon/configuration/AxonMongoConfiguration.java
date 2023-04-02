package io.sapl.demo.axon.configuration;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.client.MongoClient;

@Configuration
public class AxonMongoConfiguration {

	@Bean
	EventStorageEngine storageEngine(MongoClient client, Serializer serializer) {
		return MongoEventStorageEngine.builder().eventSerializer(serializer).snapshotSerializer(serializer)
				.mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();
	}

	@Bean
	TokenStore mongoTokenStore(MongoClient client, Serializer serializer) {
		return MongoTokenStore.builder().mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
				.serializer(serializer).build();
	}

	@Bean
	SagaStore<?> mongoSagaStore(MongoClient client, Serializer serializer) {
		return MongoSagaStore.builder().mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
				.serializer(serializer).build();
	}

}
