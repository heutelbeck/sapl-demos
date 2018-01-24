package io.saple.filterchain.obligationhandlers;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.marshall.obligation.Obligation;
import io.sapl.spring.marshall.obligation.ObligationFailedException;
import io.sapl.spring.marshall.obligation.ObligationHandler;

public class CoffeeObligationHandler implements ObligationHandler {

	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailedException {
		JsonNode obNode = obligation.getJsonObligation();
		if (!obNode.has("coffeeAddiction")) {
			throw new ObligationFailedException();
		}

	}

	@Override
	public boolean canHandle(Obligation obligation) {
		JsonNode obNode = obligation.getJsonObligation();
		if (obNode.has("type")) {
			String type = obNode.findValue("type").asText();
			if ("coffee".equals(type)) {
				return true;
			}
		}
		return false;
	}

}
