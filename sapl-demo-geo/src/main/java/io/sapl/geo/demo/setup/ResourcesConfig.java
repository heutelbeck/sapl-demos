package io.sapl.geo.demo.setup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PreDestroy;

@Configuration
public class ResourcesConfig {

	private PostgreSQLContainer<?> postgisContainer;
	private GenericContainer<?> traccarContainer;
	private GenericContainer<?> ownTracksContainer;
	
	@Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
	
	@Bean
	public GenericContainer<?> traccarContainer(){
		         
	    traccarContainer = new GenericContainer<>(
	            DockerImageName.parse("traccar/traccar:latest")).withExposedPorts(8082, 5055)
	            .withReuse(false);
	   traccarContainer.start();
	   return traccarContainer;
	}
   
	@Bean
	public GenericContainer<?> ownTracksContainer(){
		
		ownTracksContainer = new GenericContainer<>(
	            DockerImageName.parse("owntracks/recorder:latest")).withExposedPorts(8083)
	    		.withEnv("OTR_PORT", "0") //disable mqtt
	            .withReuse(false);
		ownTracksContainer.start();
	   return ownTracksContainer;
	}
	
    @Bean
    public PostgreSQLContainer<?> postgisContainer() {
        postgisContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres"))
                .withUsername("test")
                .withPassword("test")
                .withDatabaseName("test");
        postgisContainer.start();
        return postgisContainer;
    }

    @PreDestroy
    public void destroy() {
    	
    	if (postgisContainer != null) {
    		postgisContainer.close();
    	}
    	
        if (traccarContainer != null) {
            traccarContainer.close();
        }
        
        if (ownTracksContainer != null) {
            ownTracksContainer.close();
        }
    }
    
    @Bean
    public ConnectionFactory connectionFactory(PostgreSQLContainer<?> postgisContainer) {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(postgisContainer.getHost())
                        .port(postgisContainer.getMappedPort(5432))
                        .database(postgisContainer.getDatabaseName())
                        .username(postgisContainer.getUsername())
                        .password(postgisContainer.getPassword())
                        .build());
    }
    
    @Bean
    public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
    
    
    
}