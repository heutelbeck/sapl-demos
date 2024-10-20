package io.sapl.geo.demo.setup;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.sapl.geo.demo.domain.Data;
import io.sapl.geo.demo.domain.DataRepository;
import io.sapl.geo.demo.domain.GeoTracker;
import io.sapl.geo.demo.domain.GeoUser;
import io.sapl.geo.demo.domain.GeoUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSetup implements CommandLineRunner {
	
	public static final String DEFAULT_RAW_PASSWORD = "password";
	
	private final ConnectionFactory connectionFactory;
    private final PostgreSQLContainer<?> postgisContainer;
	private final GeoUserRepository geoUserRepository;
	private final DataRepository dataRepository;
    private final PasswordEncoder passwordEncoder;
    
	@Override
	public void run(String... args) throws Exception {
	
		log.info("PostGIS container started: {}:{}", postgisContainer.getHost(), postgisContainer.getMappedPort(5432));     
		createGeoTable()
        .then(insertGeometries())
        .then(createDataTable())
        .thenMany(dataRepository.saveAll(data())) 
        .then(createUserTable())
        .thenMany(geoUserRepository.saveAll(users(passwordEncoder))) 
        .doOnComplete(() -> log.info("Database setup completed"))
        .doOnError(error -> log.error("Error during database setup", error))
        .subscribe();
		  
        log.info("database setup completed");
	}

    private Collection<GeoUser> users(PasswordEncoder encoder) {
        var users = new LinkedList<GeoUser>();      		
        users.add(new GeoUser("bob", encoder.encode(DEFAULT_RAW_PASSWORD), GeoTracker.TRACCAR,  "1", "1234567890")); //, 51.37442, 7.49254
        users.add(new GeoUser("alice", encoder.encode(DEFAULT_RAW_PASSWORD), GeoTracker.OWNTRACKS, "device1", "")); // , 49.051864, 2.657535 next 49.283110, 4.009421
        return users;
    }
	
    private Collection<Data> data() {
        var data = new LinkedList<Data>();      
        data.add(new Data(0, "unclassified Data" ));
        data.add(new Data(1, "confidential Data"));
        data.add(new Data(2, "secret Data"));
        return data;
    }
    
    private Mono<? extends Result> createDataTable() {
    	
    	var createTableQuery = "CREATE TABLE Data (Id SERIAL PRIMARY KEY, Classified SMALLINT, Content CHARACTER VARYING(25));";
    	
    	return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(createTableQuery).execute()),
                connection -> Mono.from(connection.close())
        )
        .doOnSuccess(unused -> log.info("Table created successfully with query: {}", createTableQuery))
        .doOnError(error -> log.error("Error creating table with query: {}", createTableQuery, error));
    }
    
    private Mono<? extends Result> createGeoTable() {
        
    	var createTableQuery = "CREATE TABLE geometries (id SERIAL PRIMARY KEY, geom GEOMETRY, name CHARACTER VARYING(25), country CHARACTER VARYING(25), text CHARACTER VARYING(25));";
    	
    	return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(createTableQuery).execute()),
                connection -> Mono.from(connection.close())
        )
        .doOnSuccess(unused -> log.info("Table created successfully with query: {}", createTableQuery))
        .doOnError(error -> log.error("Error creating table with query: {}", createTableQuery, error));
    	
    }
    
    private Mono<? extends Result> insertGeometries() {
    	var insertQuery = """
                INSERT INTO geometries VALUES
                (1, ST_GeomFromText('POINT(51.1657 10.4515)', 4326), 'point', 'Germany', 'text point'),
                (2, ST_GeomFromText('POLYGON((50.135 14.235, 50.135 14.715, 49.935 14.715, 49.935 14.235, 50.135 14.235))', 4326), 'Prag', 'Czech Republic', 'City of Prague'),
                (3, ST_GeomFromText('POLYGON((50.05 14.3, 50.05 14.4, 50.1 14.4, 50.1 14.3, 50.05 14.3))', 4326), 'Gelände1', 'Czech Republic', 'Aussenstelle Prag 1'),
                (4, ST_GeomFromText('POLYGON((51.337 7.440, 51.337 7.551, 51.412 7.551, 51.412 7.440, 51.337 7.440))', 4326), 'Hagen', 'Germany', 'City of Hagen'),
                (5, ST_GeomFromText('POLYGON ((51.37460700707621 7.493551849436841, 51.376858287987915 7.497413584089571, 51.37811056031603 7.497668713706247, 51.37933384529114 7.495071030337499, 51.37787169020365 7.49213703974624, 51.3782480910273 7.491696361317111, 51.37758214900313 7.490293148425877, 51.376547024232764 7.492426959765027, 51.37565665343163 7.493134364611478, 51.37460700707621 7.493551849436841))', 4326), 'Fernuni', 'Germany', 'Fernuniversität in Hagen');
             """;
    	
    	return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(insertQuery).execute()),
                connection -> Mono.from(connection.close())
        )
        .doOnSuccess(unused -> log.info("Inserted successfully with query: {}", insertQuery))
        .doOnError(error -> log.error("Error inserting data with query: {}", insertQuery, error));
    	
    }
    
    private Mono<? extends Result> createUserTable() {
        
    	var createTableQuery = "CREATE TABLE GeoUser (Id SERIAL PRIMARY KEY, UserName CHARACTER VARYING(25), Password CHARACTER VARYING(200), GeoTracker CHARACTER VARYING(25), TrackerDeviceId CHARACTER VARYING(25), UniqueDeviceId CHARACTER VARYING(25), NextLat DOUBLE PRECISION, NextLon DOUBLE PRECISION);";
    	
    	return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(createTableQuery).execute()),
                connection -> Mono.from(connection.close())
        )
        .doOnSuccess(unused -> log.info("Table created successfully with query: {}", createTableQuery))
        .doOnError(error -> log.error("Error creating table with query: {}", createTableQuery, error));

    }
    
}
