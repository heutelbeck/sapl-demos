package io.sapl.demo.filterchain.obligationhandlers;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.obligation.Obligation;
import io.sapl.api.pdp.obligation.ObligationFailed;
import io.sapl.api.pdp.obligation.ObligationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import lombok.experimental.ExtensionMethod;
//@ExtensionMethod(JsonHelper.class)
public class SimpleLoggingObligationHandler implements ObligationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggingObligationHandler.class);

	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailed {
		JsonNode obNode = obligation.getJsonObligation();
		// String value = obNode.tryGetValue("message").orElseThrow(new
		// ObligationFailedException());
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
