package io.sapl.demo.shared.advicehandlers;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.advice.Advice;
import io.sapl.api.pdp.advice.AdviceHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleLoggingAdviceHandler implements AdviceHandler {
	
	@Override
	public void handleAdvice(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("message")) {
			LOGGER.info(advice.getJsonAdvice().findValue("message").asText());
		}

	}

	@Override
	public boolean canHandle(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("type")) {
			String type = adNode.findValue("type").asText();
			if ("simpleLogging".equals(type)) {
				return true;
			}
		}
		return false;
	}

}
