package io.sapl.demo.axon.command;

import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constraints.MessageConsumerConstraintHandlerProvider;
import io.sapl.demo.axon.command.MedicalRecordAPI.UpdateMedicalRecordCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("backend")
public class LogDataForPharmaceuticalStudyConstraintHandlerProvider implements
		MessageConsumerConstraintHandlerProvider<UpdateMedicalRecordCommand, CommandMessage<UpdateMedicalRecordCommand>> {

	@Override
	public Consumer<CommandMessage<UpdateMedicalRecordCommand>> getHandler(JsonNode constraint) {
		return this::logDataForStudy;
	}

	private void logDataForStudy(CommandMessage<?> commandMessage) {
		log.info("Logging required data for a pharmaceutical trial, with command = {}", commandMessage.getPayload());
	}

	@Override
	public Class<UpdateMedicalRecordCommand> getSupportedMessagePayloadType() {
		return UpdateMedicalRecordCommand.class;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<CommandMessage> getSupportedMessageType() {
		return CommandMessage.class;
	}

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint != null && constraint.has("logForAuditing")
				&& constraint.get("logForAuditing").asText().equals("log data for pharmaceutical study");
	}
}
