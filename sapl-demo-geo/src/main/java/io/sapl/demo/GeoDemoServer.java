package io.sapl.demo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.api.pip.AttributeException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoDemoServer {

	// Server Configuration
	private static final int PORT = 5699;
	private static final String POLICY_PATH = "./././policies";
	private static final int MAX_REQUESTS = 10;
	private static final int MIN_PASSENGER = 166;

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static EmbeddedPolicyDecisionPoint pdp;

	public static void main(String[] args) {
		try (SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault()
				.createServerSocket(PORT)) {
			serverSocket.setNeedClientAuth(true);

			initPDP();

			log.info("Starting server");
			log.info("Client-Auth: {}", serverSocket.getNeedClientAuth());

			for (int i = 0; i < MAX_REQUESTS; i++) {
				log.info("Listening on port {}...", PORT);
				try (SSLSocket socket = (SSLSocket) serverSocket.accept()) {
					handleConnection(socket);
				}
			}
		} catch (IOException e) {
			log.error("Exception during Server initialization: {}", e.toString());
			log.error(e.getMessage());
		}
	}

	private static synchronized void initPDP() {
		try {
			pdp = new EmbeddedPolicyDecisionPoint(POLICY_PATH);
		} catch (FunctionException | AttributeException | PolicyEvaluationException | IOException e) {
			log.error("Exception in PDP: {}", e.toString());
			log.error(e.getMessage());
		}
	}

	private static void handleConnection(SSLSocket socket) throws IOException {
		SSLSession session = socket.getSession();

		log.info("Connected with : {}", session.getPeerHost());
		log.info("Cypher {}", session.getCipherSuite());
		log.info("Protocol: {}", session.getProtocol());

		Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name());
		OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8.name());

		String receivedRequest = in.nextLine();
		log.info("Request: {}", receivedRequest);
		Request request = MAPPER.readValue(receivedRequest, Request.class);
		Response response = pdp.decide(request);

		// Execute function in accordance with decision
		if (response.getDecision() == Decision.PERMIT) {
			log.info("Permit - sending answer");
			PilDataConstructor pil = new PilDataConstructor(request.getResource(), MIN_PASSENGER);
			out.write(pil.getData());
			out.close();
		} else {
			String decision = response.getDecision().toString();
			log.info("{} - sending answer", decision);
			out.write(decision);
			out.close();
		}
		in.close();
	}
}
