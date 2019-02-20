package org.demo.shared.constraints;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pep.ConstraintHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoggingConstraintHandler implements ConstraintHandler {

	@Override
	public boolean handle(JsonNode constraint) {
		if (canHandle(constraint) && constraint.has("message")) {
			LOGGER.info(constraint.findValue("message").asText());
			return true;
		}
		return false;
	}

	@Override
	public boolean canHandle(JsonNode constraint) {
		return constraint.has("type") && "sendEmail".equals(constraint.findValue("simpleLogging").asText());
	}

}
