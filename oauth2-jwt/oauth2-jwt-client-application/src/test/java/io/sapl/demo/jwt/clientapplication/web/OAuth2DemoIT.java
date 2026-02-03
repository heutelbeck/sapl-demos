package io.sapl.demo.jwt.clientapplication.web;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Base64;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OAuth2/JWT demo verifying SAPL access control.
 * <p>
 * Tests obtain real JWT tokens from the authorization server and call
 * the resource server directly to verify SAPL policies are enforced.
 */
@DirtiesContext
@Testcontainers
class OAuth2DemoIT {

    private static final Duration TIMEOUT_SPINUP = Duration.ofMinutes(2);
    private static final String   REGISTRY       = System.getProperty("docker.registry", "local/");
    private static final String   TAG            = ":4.0.0-SNAPSHOT";
    private static final boolean  USE_LOCAL      = REGISTRY.startsWith("local");

    private static final int     AUTH_SERVER_PORT     = 9000;
    private static final int     RESOURCE_SERVER_PORT = 8090;
    private static final Network IT_NETWORK           = Network.newNetwork();
    private static final String  AUTH_SERVER          = "auth-server";

    private static final String CLIENT_ID     = "miskatonic-client";
    private static final String CLIENT_SECRET = "secret";

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final JsonMapper   jsonMapper   = new JsonMapper();
    private static String             accessToken;

    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> authServer = new GenericContainer<>(
            DockerImageName.parse(REGISTRY + "oauth2-jwt-authorization-server" + TAG))
            .withImagePullPolicy(USE_LOCAL ? ignored -> false : PullPolicy.defaultPolicy())
            .withNetwork(IT_NETWORK).withNetworkAliases(AUTH_SERVER)
            .withExposedPorts(AUTH_SERVER_PORT)
            .waitingFor(Wait.forHttp("/.well-known/openid-configuration")
                    .forPort(AUTH_SERVER_PORT)
                    .forStatusCode(200)
                    .withStartupTimeout(TIMEOUT_SPINUP))
            .withCreateContainerCmdModifier(configureContainerStartup(AUTH_SERVER_PORT));

    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> resourceServer = new GenericContainer<>(
            DockerImageName.parse(REGISTRY + "oauth2-jwt-resource-server" + TAG))
            .withImagePullPolicy(USE_LOCAL ? ignored -> false : PullPolicy.defaultPolicy())
            .withNetwork(IT_NETWORK)
            .withExposedPorts(RESOURCE_SERVER_PORT)
            .waitingFor(Wait.forHttp("/books")
                    .forPort(RESOURCE_SERVER_PORT)
                    .forStatusCode(401)
                    .withStartupTimeout(TIMEOUT_SPINUP))
            .withCreateContainerCmdModifier(configureContainerStartup(RESOURCE_SERVER_PORT));

    private static Consumer<CreateContainerCmd> configureContainerStartup(int fixedPort) {
        return cmd -> cmd.getHostConfig()
                .withPortBindings(new PortBinding(Ports.Binding.bindPort(fixedPort), new ExposedPort(fixedPort)));
    }

    @BeforeAll
    static void obtainAccessToken() throws Exception {
        var tokenUrl = "http://localhost:" + AUTH_SERVER_PORT + "/oauth2/token";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        headers.setBasicAuth(Base64.getEncoder().encodeToString(credentials.getBytes()));

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "books.read faculty.read bestiary.read");

        var request  = new HttpEntity<>(body, headers);
        var response = restTemplate.postForEntity(tokenUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var tokenResponse = jsonMapper.readTree(response.getBody());
        accessToken = tokenResponse.get("access_token").textValue();
        assertThat(accessToken).isNotBlank();
    }

    private ResponseEntity<String[]> callResourceServer(String endpoint) {
        var url     = "http://localhost:" + RESOURCE_SERVER_PORT + endpoint;
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        var request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, String[].class);
    }

    @Test
    void whenAccessingBooksWithValidToken_thenSaplPolicyPermitsAccess() {
        var response = callResourceServer("/books");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .contains("Necronomicon", "Nameless Cults", "Book of Eibon");
    }

    @Test
    void whenAccessingFacultyWithValidToken_thenSaplPolicyPermitsAccess() {
        var response = callResourceServer("/faculty");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .contains("Dr. Henry Armitage", "Professor William Dyer");
    }

    @Test
    void whenAccessingBestiaryWithValidToken_thenSaplPolicyPermitsAccess() {
        var response = callResourceServer("/bestiary");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .contains("Shoggoths", "Deep Ones", "Mi-Go");
    }

}
