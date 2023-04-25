package io.sapl.etherium.demo;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.sapl.ethereum.demo.EthereumDemoApplication;

@DirtiesContext
@Testcontainers
@SpringJUnitConfig
@SpringBootTest(classes = EthereumDemoApplication.class)
public class EthereumDemoApplicationIT {

	private static final Duration TIMEOUT_FOR_GANACHE_CLI_SPINUP = Duration.ofSeconds(10);
	private static final int GANACHE_SERVER_PORT = 8545;
	private static final String MNEMONIC = "defense decade prosper portion dove educate sing auction camera minute sing loyal";
	private static final String[] STARTUP_COMMAND = new String[] {
			"ganache-cli",
			"--mnemonic", String.format("\"%s\"", MNEMONIC),
	};
	private static final String STARTUP_LOG_MESSAGE = ".*Listening on 0.0.0.0:" + GANACHE_SERVER_PORT + ".*\\n";

	@Container
	static GenericContainer<?> ganacheCli = new GenericContainer<>(DockerImageName.parse("trufflesuite/ganache-cli"))
			.withCommand(STARTUP_COMMAND)
			.withExposedPorts(GANACHE_SERVER_PORT).waitingFor(Wait.forLogMessage(STARTUP_LOG_MESSAGE, 1))
			.withStartupTimeout(TIMEOUT_FOR_GANACHE_CLI_SPINUP);

	@Test
	void testContainerStartup() {
		
	}

}
