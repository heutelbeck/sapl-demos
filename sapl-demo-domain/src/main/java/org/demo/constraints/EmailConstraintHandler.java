package org.demo.constraints;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pep.ConstraintHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailConstraintHandler implements ConstraintHandler {

	@Override
	public boolean handle(JsonNode constraint) {
		if (canHandle(constraint) && constraint.has("recipient")
				&& constraint.has("subject") && constraint.has("message")) {
			sendEmail(constraint.findValue("recipient").asText(),
					constraint.findValue("subject").asText(),
					constraint.findValue("message").asText());
			return true;
		}
		return false;
	}

	@Override
	public boolean canHandle(JsonNode constraint) {
		return constraint != null && constraint.has("type")
				&& "sendEmail".equals(constraint.findValue("type").asText());
	}

	private static void sendEmail(String recipient, String subject, String message) {
		LOGGER.info(
				"An E-Mail has been sent to {} with the subject '{}' and the message '{}'.",
				recipient, subject, message);
	}

}