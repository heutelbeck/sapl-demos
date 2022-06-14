package io.sapl.demo.axon.config;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.mongodb.client.MongoClient;
import com.thoughtworks.xstream.XStream;

@Configuration
@Profile("mongodb")
public class MongoTokenStoreConfiguration {
	@Bean
	public TokenStore mongoTokenStore(MongoClient client) {
		var tokenSerializer = XStreamSerializer.builder().xStream(new XStream()).build();
		return MongoTokenStore.builder().serializer(tokenSerializer)
				.mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build()).build();

	}

}
