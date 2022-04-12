package io.sapl.axondemo.constraints;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constraints.MessageConsumerConstraintHandlerProvider;
import io.sapl.axondemo.domain.MedicalRecordAPI;

@Slf4j
@Service
public class LogDataForPharmaceuticalStudyConstraintHandlerProvider implements
		MessageConsumerConstraintHandlerProvider<MedicalRecordAPI.UpdateMedicalRecordCommand,
				CommandMessage<MedicalRecordAPI.UpdateMedicalRecordCommand>> {

	@Override
	public Consumer<CommandMessage<MedicalRecordAPI.UpdateMedicalRecordCommand>> getHandler(JsonNode constraint) {
		return this::logDataForStudy;
	}

	private void logDataForStudy(CommandMessage<?> commandMessage) {
		log.info("Logging required data for a pharmaceutical trial, with command = {}", commandMessage.getPayload());
	}

	@Override
	public Class<MedicalRecordAPI.UpdateMedicalRecordCommand> getSupportedMessagePayloadType() {
		return MedicalRecordAPI.UpdateMedicalRecordCommand.class;
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
