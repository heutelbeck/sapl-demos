package org.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Starts a server providing REST endpoints for the policy information points needed by
 * the reactive demo application when it is configured to connect to a remote PDP. The PDP
 * server then has to read policies which use the HTTP-PIP to connect to remote PIPs.
 * These remote PIPs are provided by this applications server. The server can be
 * configured using the application.properties file under src/main/resources.
 */
@EntityScan
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PipServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PipServerApplication.class, args);
	}

}
