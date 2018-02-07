package io.sapl.demo.shared.advicehandlers;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.marshall.advice.Advice;
import io.sapl.spring.marshall.advice.AdviceHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleLoggingAdviceHandler implements AdviceHandler {
	
	@Override
	public void handleAdvice(Advice advice) {
		JsonNode obNode = advice.getJsonAdvice();
		if (obNode.has("message")) {
			LOGGER.info(advice.getJsonAdvice().findValue("message").asText());
		}

	}

	@Override
	public boolean canHandle(Advice advice) {
		JsonNode obNode = advice.getJsonAdvice();
		if (obNode.has("type")) {
			String type = obNode.findValue("type").asText();
			if ("simpleLogging".equals(type)) {
				return true;
			}
		}
		return false;
	}

}
