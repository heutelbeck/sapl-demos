package org.demo.decision;

import org.demo.domain.PrinterUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vaadin.flow.spring.annotation.SpringComponent;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import reactor.core.publisher.Flux;

@Service
@SpringComponent
public class PrinterDecisionHandler {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	@Autowired
	PolicyDecisionPoint pdp;

	public Flux<Decision> crowdAccessDecision(PrinterUser user) {
		AuthorizationSubscription sub = new AuthorizationSubscription(JSON.textNode(user.getEthereumAddress()),
				JSON.textNode("access"), JSON.textNode("crowdTemplate"), null);
		final Flux<AuthorizationDecision> crowdAccess = pdp.decide(sub);
		final Flux<Decision> decisionFlux = crowdAccess.map(d -> d.getDecision());
		return decisionFlux;
	}

	public Flux<Decision> paidAccessDecision(PrinterUser user) {
		AuthorizationSubscription sub = new AuthorizationSubscription(mapper.convertValue(user, JsonNode.class),
				JSON.textNode("access"), JSON.textNode("paidTemplate"), null);
		final Flux<AuthorizationDecision> paidAccess = pdp.decide(sub);
		final Flux<Decision> decisionFlux = paidAccess.map(d -> d.getDecision());
		return decisionFlux;
	}

	public Flux<Decision> printerAccessDecision(PrinterUser user, String printer) {
		AuthorizationSubscription sub = new AuthorizationSubscription(mapper.convertValue(user, JsonNode.class),
				JSON.textNode("start"), JSON.textNode(printer), null);
		final Flux<AuthorizationDecision> printerAccess = pdp.decide(sub);
		final Flux<Decision> decisionFlux = printerAccess.map(d -> d.getDecision());
		return decisionFlux;
	}

}
