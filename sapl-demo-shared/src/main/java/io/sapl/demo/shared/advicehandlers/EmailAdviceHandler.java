package io.sapl.demo.shared.advicehandlers;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.advice.Advice;
import io.sapl.api.pdp.advice.AdviceHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailAdviceHandler implements AdviceHandler {
	
	@Override
	public void handleAdvice(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("recipient") && adNode.has("subject") && adNode.has("message")) {
			sendEmail(adNode.findValue("recipient").asText(), adNode.findValue("subject").asText(),
					adNode.findValue("message").asText());
		}

	}

	@Override
	public boolean canHandle(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("type")) {
			String type = adNode.findValue("type").asText();
			if ("sendEmail".equals(type)) {
				return true;
			}
		}
		return false;
	}

	private static void sendEmail(String recipient, String subject, String message) {
		LOGGER.info("An E-Mail has been sent to {} with the subject '{}' and the message '{}'.", recipient, subject,
				message);
	}

}
