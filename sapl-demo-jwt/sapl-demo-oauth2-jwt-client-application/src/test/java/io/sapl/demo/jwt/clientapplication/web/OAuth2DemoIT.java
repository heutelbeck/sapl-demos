package io.sapl.demo.jwt.clientapplication.web;

import static org.xmlunit.assertj3.XmlAssert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.xmlunit.builder.DiffBuilder;
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
@SpringJUnitConfig
@WebAppConfiguration
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(classes = OAuth2ClientApplication.class)
public class OAuth2DemoIT {

	private static final Duration TIMEOUT_SPINUP = Duration.ofSeconds(20);
	private static final String REGISTRY = "ghcr.io/heutelbeck/";
	private static final String TAG = ":2.1.0-SNAPSHOT";

	private static final int AUTH_SERVER_PORT = 9000;
	private static final int RESOURCE_SERVER_PORT = 8090;
	private static final Network IT_NETWORK = Network.newNetwork();
	private static final String AUTH_SERVER = "auth-server";

	private static final String AUTH_CODE_GRANT_TYPE_URL = "http://localhost:8080/authorize?grant_type=authorization_code";
	private static final String AUTH_CODE_GRANT_TYPE_REDIRECT_PATTERN = "^" + Pattern.quote(
			"http://auth-server:9000/oauth2/authorize?response_type=code&client_id=miskatonic-client&scope=books.read%20faculty.read%20bestiary.read&state=")
			+ ".*" + Pattern.quote("%3D&redirect_uri=http://127.0.0.1:8080/authorized") + "$";

	private static final String AUTH_CREDENTIALS_GRANT_TYPE_URL = "http://localhost:8080/authorize?grant_type=client_credentials";
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

	static DocumentBuilderFactory xmlBuilderFactory;

	@Container
	static GenericContainer<?> authServer = new GenericContainer<>(
			DockerImageName.parse(REGISTRY + "sapl-demo-oauth2-jwt-authorization-server" + TAG))
			.withImagePullPolicy(PullPolicy.defaultPolicy()).withNetwork(IT_NETWORK).withNetworkAliases(AUTH_SERVER)
			.waitingFor(Wait.forListeningPort())
			.waitingFor(Wait.forLogMessage(containsPattern("Started OAuth2AuthorizationServerApplication"), 1))
			.withStartupTimeout(TIMEOUT_SPINUP)
			.withCreateContainerCmdModifier(configureContainerStartup(AUTH_SERVER_PORT));

	@Container
	static GenericContainer<?> resourceServer = new GenericContainer<>(
			DockerImageName.parse(REGISTRY + "sapl-demo-oauth2-jwt-resource-server" + TAG))
			.withImagePullPolicy(PullPolicy.defaultPolicy()).withNetwork(IT_NETWORK).waitingFor(Wait.forListeningPort())
			.waitingFor(Wait.forLogMessage(containsPattern("Started ResourceServerApplication"), 1))
			.withStartupTimeout(TIMEOUT_SPINUP)
			.withCreateContainerCmdModifier(configureContainerStartup(RESOURCE_SERVER_PORT));

	@BeforeAll
	static void beforeAll() throws ParserConfigurationException {
		xmlBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
		xmlBuilderFactory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
	}

	private static String containsPattern(String pattern) {
		return "^.*" + pattern.replaceAll("[\\^\\$]", "") + ".*$";
	}

	private static Consumer<CreateContainerCmd> configureContainerStartup(int fixedPort) {
		return cmd -> cmd.getHostConfig()
				.withPortBindings(new PortBinding(Ports.Binding.bindPort(fixedPort), new ExposedPort(fixedPort)));
	}

	@SneakyThrows
	private static void printResponse(MockHttpServletResponse response) {
		System.out.println(response.getStatus());
		for (var header : response.getHeaderNames())
			System.out.println(header + "\t:\t" + response.getHeaderValues(header));
		System.out.println(response.getContentAsString());
	}

	@Autowired
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@BeforeEach
	void beforeEach() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@Test
	@WithMockUser(username = "user1", password = "password")
	void test_codeGrantType_clientRedirect() throws Exception {
		var request = request(HttpMethod.GET, AUTH_CODE_GRANT_TYPE_URL);
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(content().string(""))
				.andExpect(header().exists("Location"))
				.andExpect(
						header().string("Location",
								new PredicateMatcher<String>(
										str -> Pattern.matches(AUTH_CODE_GRANT_TYPE_REDIRECT_PATTERN, str))))
				.andReturn();
	}

	@Test
	@WithMockUser(username = "user1", password = "password")
	void test_credectialsGrantType_accessToRessources() throws Exception {
		var request = request(HttpMethod.GET, AUTH_CREDENTIALS_GRANT_TYPE_URL);
		var response = mockMvc.perform(request).andExpect(status().is2xxSuccessful())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn().getResponse();
		printResponse(response);
		DiffBuilder.compare(Input.fromFile(AUTH_CREDENTIALS_GRANT_TYPE_RESULT_PATH));
		assertThat(Input.fromString(response.getContentAsString()))
				.and(Input.fromFile(AUTH_CREDENTIALS_GRANT_TYPE_RESULT_PATH))
				.withDocumentBuilderFactory(xmlBuilderFactory).areIdentical();

	}

}
