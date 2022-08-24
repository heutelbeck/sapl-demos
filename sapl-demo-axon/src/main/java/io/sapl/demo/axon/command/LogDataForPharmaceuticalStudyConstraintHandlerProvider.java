package io.sapl.demo.axon.command;

import org.axonframework.commandhandling.CommandMessage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constrainthandling.api.CommandConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogDataForPharmaceuticalStudyConstraintHandlerProvider implements CommandConstraintHandlerProvider {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint != null && constraint.has("logForAuditing")
				&& constraint.get("logForAuditing").asText().equals("log data for pharmaceutical study");
	}

	@Override
	public void accept(CommandMessage<?> commandMessage) {
		log.info("Logging required data for a pharmaceutical trial, with command = {}", commandMessage.getPayload());
	}

}
