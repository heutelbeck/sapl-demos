package io.sapl.geo.demo.setup;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final WebClient.Builder webClientBuilder;
    private static final String COOKIE = "Cookie";

    @Override
    public void run(String... args) throws Exception {
        log.info("traccar container started: {}:{}", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
        
        var email = "testuser@example.com";
        var password = "password";   
        
        registerTraccarUser(email, password)
            .flatMap(sessionCookie -> establishTraccarSession(email, password)
                .flatMap(traccarSessionCookie -> createTraccarDevice(traccarSessionCookie)
                    .flatMap(devId -> {
                        var traccarGeofences = new String[]{
                            createGeofenceJson("fence1", "description for fence1"),
                            createGeofenceJson("lmu", "description for lmu")
                        };
                        return Mono.when(
                            Arrays.stream(traccarGeofences)
                                .map(fence -> postTraccarGeofence(traccarSessionCookie, fence)
                                    .flatMap(geofence -> linkTraccarGeofenceToDevice(devId, geofence.get("id").asInt(), traccarSessionCookie)))
                                .toArray(Mono[]::new)
                        ).then();
                    })
                )
            )
            .doOnTerminate(() -> log.info("tracker setup completed"))
            .doOnError(error -> log.error("Error during tracker setup", error))
            .subscribe();
    }

    private Mono<String> registerTraccarUser(String email, String password) {
        String registerUserUrl = String.format("http://%s:%d/api/users", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
        WebClient webClient = webClientBuilder.build();

        String userJson = String.format("""
                {
                "name": "testuser",
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        return webClient.post()
                .uri(registerUserUrl)
                .header("Content-Type", "application/json")
                .bodyValue(userJson)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("User registered successfully: {}", response))
                .doOnError(error -> log.error("Error registering user", error)); 
    }

    private Mono<String> establishTraccarSession(String email, String password) {
        var sessionUrl = String.format("http://%s:%d/api/session", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

        var bodyProperties = createBodyProperties(email, password);

        var body = bodyProperties.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        var client = WebClient.builder().build();

        return client.post()
                .uri(sessionUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .flatMap(response -> {
                    if (response != null) {
                        var setCookieHeader = response.getHeaders().getFirst("Set-Cookie");
                        if (setCookieHeader != null) {
                            return Mono.just(Arrays.stream(setCookieHeader.split(";"))
                                    .filter(s -> s.startsWith("JSESSIONID"))
                                    .findFirst()
                                    .orElse(null));
                        }
                    }
                    return Mono.empty(); 
                });
    }

    private Map<String, String> createBodyProperties(String email, String password) {
        Map<String, String> bodyProperties = new HashMap<>();
        bodyProperties.put("email", email);
        bodyProperties.put("password", password);
        return bodyProperties;
    }
    
    private Mono<String> createTraccarDevice(String sessionCookie) {
        var createDeviceUrl = String.format("http://%s:%d/api/devices", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
        var webClient = webClientBuilder.build();

        String body = """
                {
                    "name": "Test Device",
                    "uniqueId": "1234567890"
                }
                """;

        return webClient.post()
                .uri(createDeviceUrl)
                .headers(headers -> {
                    headers.add(COOKIE, sessionCookie);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Device created successfully: {}", response))
                .doOnError(error -> log.error("Error creating device", error))
                .flatMap(result -> {
                    if (result != null) {
                        return Mono.just(result.get("id").asText());
                    }
                    return Mono.error(new Exception("Error while extracting deviceId"));
                });
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
                .doOnError(error -> log.error("Error creating geofence", error));
    }

    private Mono<Void> linkTraccarGeofenceToDevice(String deviceId, int geofenceId, String sessionCookie) {
        var linkGeofenceUrl = String.format("http://%s:%d/api/permissions", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));

        String linkJson = """
                {"deviceId":"%s","geofenceId": %d}
                """;

        String body = String.format(linkJson, deviceId, geofenceId);

        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri(linkGeofenceUrl)
                .headers(headers -> {
                    headers.add(COOKIE, sessionCookie);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Geofence linked successfully: {}", response))
                .doOnError(error -> log.error("Error linking geofence", error))
                .then(); 
    }

    private String createGeofenceJson(String name, String description) {
        return String.format("""
                {"name":"%s","description": "%s","area":"POLYGON ((48.25767 11.54370, 48.25767 11.54422, 48.25747 11.54422, 48.25747 11.54370, 48.25767 11.54370))"}
                """, name, description);
    }
}
