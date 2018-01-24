package io.saple.filterchain.obligationhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.marshall.obligation.Obligation;
import io.sapl.spring.marshall.obligation.ObligationFailedException;
import io.sapl.spring.marshall.obligation.ObligationHandler;

//import lombok.experimental.ExtensionMethod;
//@ExtensionMethod(JsonHelper.class)
public class SimpleLoggingObligationHandler implements ObligationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggingObligationHandler.class);

	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailedException {
		JsonNode obNode = obligation.getJsonObligation();
		// String value = obNode.tryGetValue("message").orElseThrow(new
		// ObligationFailedException());
		if (obNode.has("message")) {
			LOGGER.info(obligation.getJsonObligation().findValue("message").asText());
		} else
			throw new ObligationFailedException();

	}

	@Override
	public boolean canHandle(Obligation obligation) {
		JsonNode obNode = obligation.getJsonObligation();
		if (obNode.has("type")) {
			String type = obNode.findValue("type").asText();
			if ("simpleLogging".equals(type)) {
				return true;
			}
		}
		return false;
	}

}
