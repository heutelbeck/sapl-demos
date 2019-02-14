package org.demo.shared.obligationhandlers;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.obligation.Obligation;
import io.sapl.api.pdp.obligation.ObligationFailed;
import io.sapl.api.pdp.obligation.ObligationHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleLoggingObligationHandler implements ObligationHandler {

	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailed {
		JsonNode obNode = obligation.getJsonObligation();
		if (obNode.has("message")) {
			LOGGER.info(obligation.getJsonObligation().findValue("message").asText());
		} else
			throw new ObligationFailed();
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
