package io.sapl.geo.demo.setup;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import io.sapl.geo.demo.service.GeometryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.testcontainers.containers.GenericContainer;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackerSetup implements CommandLineRunner {

    private final GenericContainer<?> traccarContainer;
    private final GenericContainer<?> ownTracksContainer;
    private final WebClient.Builder webClientBuilder;
    private final GeometryService geometryService;
	
    private static final String COOKIE = "Cookie";

    @Override
    public void run(String... args) throws Exception {
        
        
        log.info("traccar container started: {}:{}", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
        var email = "testuser@example.com";
        var password = "password";   
        registerUser(email, password);
        var sessionCookie = establishSession(email, password);
        var devId = createDevice(sessionCookie);
        
        var body = """
        		{"name":"fence1","description": "description for fence1","area":"POLYGON ((48.25767 11.54370, 48.25767 11.54422, 48.25747 11.54422, 48.25747 11.54370, 48.25767 11.54370))"}
        		""";
        var body2 = """
        		{"name":"lmu","description": "description for lmu","area":"POLYGON ((48.150402911178844 11.566792870984045, 48.1483205765966 11.56544925428264, 48.147576865197465 11.56800995875841, 48.14969540929175 11.56935357546081, 48.150402911178844 11.566792870984045))"}
        		""";
        
        var traccarGeofences = new String[] {body, body2};
        
        for(var fence: traccarGeofences){
        	
        	var fenceId = postTraccarGeofence(sessionCookie, fence).block().get("id").asInt();
        	linkGeofenceToDevice(devId, fenceId, sessionCookie);
        }
        
        
        
        addPosition("1234567890", 51.34533, 7.40575);
        
        log.info("ownTracks container started: {}:{}", ownTracksContainer.getHost(), ownTracksContainer.getMappedPort(8083));
        
        addOwntracks();
        
        log.info("tracker setup completed");
    }

    

    	
	
    private void registerUser(String email, String password) {
        String registerUserUrl = String.format("http://%s:%d/api/users", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

        WebClient webClient = webClientBuilder.build();

        String userJson =String.format("""
                {
                "name": "testuser",
                "email": "%s",
                "password": "%s"
            }
            """, email, password);
        
        webClient.post()
                .uri(registerUserUrl)
                .header("Content-Type", "application/json")
                .bodyValue(userJson)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("User registered successfully: {}", response))
                .doOnError(error -> 
                log.error("Error registering user", error))
                .block();
    }
    
    private String establishSession(String email, String password) {
         	
    		var sessionUrl = String.format("http://%s:%d/api/session", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

        	var bodyProperties = new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

            };

            bodyProperties.put("email", email);
            bodyProperties.put("password", password);

            var body = bodyProperties.entrySet().stream().map(
                    e -> String.format("%s=%s", e.getKey(), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                    .collect(Collectors.joining("&"));

            var client = WebClient.builder().build();

            var response = client.post()
                    .uri(sessionUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response != null) {
                var setCookieHeader = response.getHeaders().getFirst("Set-Cookie");
                if (setCookieHeader != null) {
                    // Extrahiere nur die JSESSIONID
                    return Arrays.stream(setCookieHeader.split(";"))
                                              .filter(s -> s.startsWith("JSESSIONID"))
                                              .findFirst()
                                              .orElse(null);
                }
            }
            return null;
            
    }

    
    private String createDevice(String sessionCookie) throws Exception {
        
    	var createDeviceUrl = String.format("http://%s:%d/api/devices", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
        var webClient = webClientBuilder.build();

        String body = """
                {
                    "name": "Test Device",
                    "uniqueId": "1234567890"
                }
                """;

        var result = webClient.post()
                .uri(createDeviceUrl)
                .headers(headers -> {
                    headers.add(COOKIE, sessionCookie);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Device created successfully: {}", response.toString()))
                .doOnError(error -> 
                log.error("Error creating device", error)
                
                		)
                .block();
       
        if(result != null) {
        
        	return result.get("id").asText();
        }
        throw new Exception("Error while extracting deviceId");
        
    }

    
    private Mono<JsonNode> postTraccarGeofence(String sessionCookie, String body) {
    	
    	var createGeofenceUrl = String.format("http://%s:%d/api/geofences", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

        var webClient = webClientBuilder.build();
        
        return webClient.post()
                .uri(createGeofenceUrl)
                .headers(headers -> {
                    headers.add(COOKIE, sessionCookie);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Geofence created successfully: {}", response))
                .doOnError(error -> 
                log.error("Error creating geofence", error)
                );

    }
    
    private void linkGeofenceToDevice(String deviceId, int geofenceId, String sessionCookie) {
    	
    	var linkGeofenceUrl = String.format("http://%s:%d/api/permissions", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

    	String linkJson = """
        		{"deviceId":"%s","geofenceId": %d}
        		""";
        
    	String body = String.format(linkJson, deviceId, geofenceId);
    	
        WebClient webClient = webClientBuilder.build();
        
        webClient.post()
        .uri(linkGeofenceUrl)
        .headers(headers -> {
            headers.add(COOKIE, sessionCookie);
            headers.setContentType(MediaType.APPLICATION_JSON);
        })
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(response -> log.info("Geofence linked successfully: {}", response))
        .doOnError(error -> 
        log.error("Error linked geofence", error)
        
        		)
        .block();

}
    
    private void addPosition(String deviceId, Double lat, Double lon) {
        
	    geometryService.addTraccarPosition(deviceId, lat, lon)
	    			.block();

    }

    private void addOwntracks() {
    	
    	geometryService.addOwntracksPosition("alice", "device1", 48.856613, 2.352222)
	    	.then()
	    	.block();
    	
    }
}
    
    