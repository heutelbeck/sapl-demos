package io.sapl.geo.demo.service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.sapl.api.interpreter.Val;
import io.sapl.geo.databases.DataBaseTypes;
import io.sapl.geo.databases.DatabaseStreamQuery;
import io.sapl.geo.functionlibraries.GeoConverter;
import io.sapl.geo.functionlibraries.GeoParser;
import io.sapl.geo.owntracks.OwnTracks;
import io.sapl.geo.traccar.TraccarGeofences;
import io.sapl.geo.traccar.TraccarPositions;
import io.sapl.pip.http.ReactiveWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeometryService {

	private final PostgreSQLContainer<?> postgisContainer;
	private final GenericContainer<?> traccarContainer;
	private final GenericContainer<?> ownTracksContainer;
	
	public Flux<JsonNode> getFencesAndLocationsFromPostgis() throws JsonProcessingException {
		
		var authenticationTemplate = """
                {
                "user":"%s",
                "password":"%s",
            	"server":"%s",
            	"port": %s,
            	"dataBase":"%s"
            	}
            """;
		var authTemplate = String.format(authenticationTemplate, postgisContainer.getUsername(),
                postgisContainer.getPassword(), postgisContainer.getHost(), postgisContainer.getMappedPort(5432),
                postgisContainer.getDatabaseName());
		
		var template = ("""
            {
                "table":"%s",
                "geoColumn":"%s",
            	"singleResult": false,
            	"columns": ["name", "country", "text"]
            }
            """);
		
		var queryString = String.format(template, "geometries", "geom");
		
		var mapper = new ObjectMapper();
		var postgisConnection = new DatabaseStreamQuery(Val.ofJson(authTemplate).get(), mapper, DataBaseTypes.POSTGIS);
				
				
		var result = postgisConnection.sendQuery(Val.ofJson(queryString).get());
		
		return result
				.map(Val::get);
		
		

	}
	
	public Flux<JsonNode> getPositionFromTraccar(String deviceId) throws JsonProcessingException{
		var authenticationTemplate = """
                {
                "user":"testuser@example.com",
                "password":"password",
            	"server":"%s",
            	"protocol":"http"
            	}
            """;
		
		var address = traccarContainer.getHost() + ":" + traccarContainer.getMappedPort(8082);
		
		var authTemplate = String.format(authenticationTemplate, address);
		
		var template = ("""
            {
                "deviceId":"%s",
                "latitudeFirst":false
            }
            """);
		
		var requestTemplate = String.format(template, deviceId);
		
		var result = new TraccarPositions(Val.ofJson(authTemplate).get(), new ObjectMapper()).getPositions(Val.ofJson(requestTemplate).get());

		
		return result
				.map(Val::get);
		
	}
	
	public Flux<JsonNode> getGeofencesFromTraccar(String deviceId) throws JsonProcessingException{
		var authenticationTemplate = """
                {
                "user":"testuser@example.com",
                "password":"password",
            	"server":"%s",
            	"protocol":"http"
            	}
            """;
		
		var address = traccarContainer.getHost() + ":" + traccarContainer.getMappedPort(8082);
		
		var authTemplate = String.format(authenticationTemplate, address);
		
		var template = ("""
            {
                "deviceId":"%s",
                "latitudeFirst":false
            }
            """);
		
		var requestTemplate = String.format(template, deviceId);
		
		var result = new TraccarGeofences(Val.ofJson(authTemplate).get(), new ObjectMapper()).getGeofences(Val.ofJson(requestTemplate).get());

		
		return result
				.map(Val::get);
		
	}
	
	public Flux<JsonNode> getGeometriesFromHttp() throws JsonProcessingException{
		
		var reactiveWebClient = new ReactiveWebClient(new ObjectMapper());
		var template = """
                {
                    "baseUrl" : "http://localhost:8000/kml/features",
                    "accept" : "%s",
                    "pollingIntervalMs" : 2000
                }
                """;
		var request = Val.ofJson(String.format(template, MediaType.TEXT_PLAIN_VALUE));
		
		var response = reactiveWebClient.httpRequest(HttpMethod.GET, request);
		
		var geoParser = new GeoParser(new ObjectMapper());
		var geoConverter = new GeoConverter();
		
		
		return response
		.map(v-> {
			try {
				return geoParser.parseKML(v);
			} catch (Exception e) {
				return Val.error(e.getMessage());
			}
			
		})
		.map(x->{
			var geos = x.get();
			for(JsonNode geo:geos) {
				var kmlGeometry = geo.get("Geometry");
				try {				
					var json = geoConverter.kmlToGeoJson( Val.of(kmlGeometry)).get();
					((ObjectNode)geo).replace("Geometry", json);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
			}
			return geos;
		})
		;
	}
	
	public Flux<JsonNode> getPositionFromOwnTracks(String user, String device) throws JsonProcessingException{
		
		String authTemplate = """
                {
            	"server":"%s",
            	"protocol":"http"
            	}
            """;
		
		String template = """
                {
                "user":"%s",
            	"deviceId":"%s",
            	"latitudeFirst":false
            	}
            """;
		var address  = ownTracksContainer.getHost() + ":" + ownTracksContainer.getMappedPort(8083);
		var authenticationTemplate = String.format(authTemplate, address);
		var requestTemplate = String.format(template, user, device);
		
		var val          = Val.ofJson(requestTemplate);
		var authVal		 = Val.ofJson(authenticationTemplate);
        return new OwnTracks(authVal.get(), new ObjectMapper()).connect(val.get()).map(Val::get);
                
	}
	
	public Mono<String> addTraccarPosition(String deviceId, Double lat, Double lon) {
	        
		var webClient =   WebClient.builder().build();

		var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		var timeStamp =  LocalDateTime.now().format(formatter);
		
		var url = """
		 		http://%s:%d/?id=%s&lat=%s&lon=%s&timestamp=%s&hdop=0&altitude=990&speed=0
                """;
		var addPositionUrl = String.format(url, traccarContainer.getHost(), traccarContainer.getMappedPort(5055), deviceId, lat.toString(), lon.toString(), timeStamp);

		return exchange( 
					webClient.get()
					.uri(addPositionUrl)
              );
		}	

	public Mono<String> addOwntracksPosition(String user, String device, Double lat, Double lon ) {
		var webClient =   WebClient.builder().build();
		
		var urlString = String.format("http://%s:%s/pub", ownTracksContainer.getHost(), ownTracksContainer.getMappedPort(8083)); // URL des OwnTracks Servers		
		var json = """
					{
					"_type": "location", 
					"tid": "TD", 
					"lat": %s, 
					"lon": %s, 
					"tst": %s, 
					"batt": 99, 
					"acc": 14, 
					"alt": 100, 
					"created_at":"2023-07-09T13:34:19.000+00:00",
					"inregions":[]}
				""";
		
		var payload = String.format(json, lat, lon, Instant.now().getEpochSecond());
      	
		
      
		return exchange (
					webClient.post()
					.uri(urlString)
					.header("X-Limit-U",  user)
					.header("X-Limit-D", device)
					.bodyValue(payload)
				);
	}
	
	private Mono<String> exchange(RequestHeadersSpec<?> client){
		
		return client.exchangeToMono(response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(String.class)
                        .doOnSuccess(body ->
                                log.info("Position added successfully: {}", body))
                        .doOnError(error ->
                                log.error("Error reading response body", error));
            } else {
                return response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("Error adding position: status {}, body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("Error adding position: " + response.statusCode()));
                        });
            }
        })
        .doOnError(error -> log.error("Request failed", error))
        
        ;
	}
	
	
}

