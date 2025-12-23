package io.sapl.demo.jwt.clientapplication.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.builder.Input;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import io.sapl.demo.jwt.clientapplication.OAuth2ClientApplication;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@DirtiesContext
@Testcontainers
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(classes = OAuth2ClientApplication.class)
class OAuth2DemoIT {

    private static final Duration TIMEOUT_SPINUP = Duration.ofSeconds(20);
    // Configurable via -Ddocker.registry=ghcr.io/heutelbeck/ for CI, defaults to local/ for dev
    private static final String   REGISTRY       = System.getProperty("docker.registry", "local/");
    private static final String   TAG            = ":4.0.0-SNAPSHOT";
    // Don't pull for local images, use default policy for remote registries
    private static final boolean  USE_LOCAL      = REGISTRY.startsWith("local");

    private static final int     AUTH_SERVER_PORT     = 9000;
    private static final int     RESOURCE_SERVER_PORT = 8090;
    private static final Network IT_NETWORK           = Network.newNetwork();
    private static final String  AUTH_SERVER          = "auth-server";

    private static final String INDEX_URL         = "http://localhost:8080/index";
    private static final String INDEX_RESULT_PATH = "target/test-classes/xml/index_result.html";

    private static final String AUTH_CODE_GRANT_TYPE_URL              = "http://localhost:8080/authorize?grant_type=authorization_code";
    private static final String AUTH_CODE_GRANT_TYPE_REDIRECT_PATTERN = "^" + Pattern.quote(
            "http://auth-server:9000/oauth2/authorize?response_type=code&client_id=miskatonic-client&scope=books.read%20faculty.read%20bestiary.read&state=")
            + ".*" + Pattern.quote("%3D&redirect_uri=http://127.0.0.1:8080/authorized") + "$";

    private static final String AUTH_CREDENTIALS_GRANT_TYPE_URL         = "http://localhost:8080/authorize?grant_type=client_credentials";
    private static final String AUTH_CREDENTIALS_GRANT_TYPE_RESULT_PATH = "target/test-classes/xml/credentials_grant_type_result.html";

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class PredicateMatcher<T> extends BaseMatcher<T> {

        Predicate<T> test;

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object actual) {
            try {
                return test.test((T) actual);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("passes Predicate ").appendValue(test);
        }

    }

    @Container
    @SuppressWarnings("resource") // Fine for tests which are short-lived
    static GenericContainer<?> authServer = new GenericContainer<>(
            DockerImageName.parse(REGISTRY + "sapl-demo-oauth2-jwt-authorization-server" + TAG))
            .withImagePullPolicy(USE_LOCAL ? __ -> false : PullPolicy.defaultPolicy())
            .withNetwork(IT_NETWORK).withNetworkAliases(AUTH_SERVER)
            .waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forLogMessage(containsPattern("Started OAuth2AuthorizationServerApplication"), 1))
            .withStartupTimeout(TIMEOUT_SPINUP)
            .withCreateContainerCmdModifier(configureContainerStartup(AUTH_SERVER_PORT));

    @Container
    @SuppressWarnings("resource") // Fine for tests which are short-lived
    static GenericContainer<?> resourceServer = new GenericContainer<>(
            DockerImageName.parse(REGISTRY + "sapl-demo-oauth2-jwt-resource-server" + TAG))
            .withImagePullPolicy(USE_LOCAL ? __ -> false : PullPolicy.defaultPolicy())
            .withNetwork(IT_NETWORK).waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forLogMessage(containsPattern("Started ResourceServerApplication"), 1))
            .withStartupTimeout(TIMEOUT_SPINUP)
            .withCreateContainerCmdModifier(configureContainerStartup(RESOURCE_SERVER_PORT));

    private static String containsPattern(String pattern) {
        return "^.*" + pattern.replaceAll("[\\^\\$]", "") + ".*$";
    }

    private static Consumer<CreateContainerCmd> configureContainerStartup(int fixedPort) {
        return cmd -> cmd.getHostConfig()
                .withPortBindings(new PortBinding(Ports.Binding.bindPort(fixedPort), new ExposedPort(fixedPort)));
    }

    @SneakyThrows
    static void printResponse(MockHttpServletResponse response) {
        System.out.println(response.getStatus());
        for (var header : response.getHeaderNames())
            System.out.println(header + "\t:\t" + response.getHeaderValues(header));
        System.out.println(response.getContentAsString());
    }

    static <T> void printResponse(ResponseEntity<T> response) {
        System.out.println(response.getStatusCode());
        for (var header : response.getHeaders().keySet())
            System.out.println(header + "\t:\t" + response.getHeaders().get(header));
        System.out.println(response.getBody());
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebClient webClient;

    @Test
    @WithMockUser(username = "user1", password = "password")
    void test_index() throws Exception {
        final var request  = request(HttpMethod.GET, INDEX_URL);
        final var response = mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn().getResponse();
        XmlAssert.assertThat(Input.fromString(response.getContentAsString()))
                .and(Input.fromFile(INDEX_RESULT_PATH)).areIdentical();
    }

    @Test
    @WithMockUser(username = "user1", password = "password")
    void test_codeGrantType_clientRedirect() throws Exception {
        final var request      = request(HttpMethod.GET, AUTH_CODE_GRANT_TYPE_URL);
        final var response     = mockMvc.perform(request).andExpect(status().is3xxRedirection())
                .andExpect(content().string("")).andExpect(header().exists("Location"))
                .andExpect(header().string("Location",
                        new PredicateMatcher<String>(
                                str -> Pattern.matches(AUTH_CODE_GRANT_TYPE_REDIRECT_PATTERN, str))))
                .andReturn().getResponse();
        final var redirectURI  = response.getHeader("Location");
        final var authResponse = webClient.get().uri(redirectURI).retrieve().toEntity(String.class).block();
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(authResponse.getHeaders()).containsKey("Location");
    }

    @Test
    @WithMockUser(username = "user1", password = "password")
    void test_credectialsGrantType_accessToRessources() throws Exception {
        final var request  = request(HttpMethod.GET, AUTH_CREDENTIALS_GRANT_TYPE_URL);
        final var response = mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn().getResponse();
        XmlAssert.assertThat(Input.fromString(response.getContentAsString()))
                .and(Input.fromFile(AUTH_CREDENTIALS_GRANT_TYPE_RESULT_PATH)).areIdentical();
    }

}
