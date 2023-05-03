package io.sapl.demo.jwt.clientapplication.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import java.time.Duration;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
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

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import io.sapl.demo.jwt.clientapplication.OAuth2ClientApplication;
import lombok.AccessLevel;
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

	private static String containsPattern(String pattern) {
		return "^.*" + pattern.replaceAll("[\\^\\$]", "") + ".*$";
	}

	private static Consumer<CreateContainerCmd> configureContainerStartup(int fixedPort) {
		return cmd -> cmd.getHostConfig()
				.withPortBindings(new PortBinding(Ports.Binding.bindPort(fixedPort), new ExposedPort(fixedPort)));
	}

	@Autowired
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@BeforeEach
	void beforeEach() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@Disabled
	void testContainerStartup() throws Exception {
		var response = mockMvc.perform(request(HttpMethod.GET, "http://localhost:8080")).andReturn().getResponse();
		System.out.println(response.getStatus());
		for (var header : response.getHeaderNames())
			System.out.println(header + "\t:\t" + response.getHeaderValues(header));
		System.out.println(response.getContentAsString());
	}

}
