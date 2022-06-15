package io.sapl.demo.axon.command;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.EntityId;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.annotations.ConstraintHandler;
import io.sapl.demo.axon.command.MedicalRecordAPI.BloodCountLogEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.BloodCountUpdatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.UpdateBloodCountCommand;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class BloodCount {
	@EntityId
	private int examinationId;

	public BloodCount(int examinationId) {
		this.examinationId = examinationId;
	}

	@PreEnforce
	@CommandHandler
	public void handle(UpdateBloodCountCommand command) {
		apply(new BloodCountUpdatedEvent(command.getHematocritValue()));
	}

	@ConstraintHandler("#constraint.get('log event').asText().equals('blood count event')")
	public void logClinicalRecordOnlyIfRequired(UpdateBloodCountCommand command, JsonNode constraint, MetaData metaData)
			throws Exception {
		var logEvent = new BloodCountLogEvent(examinationId, command.getHematocritValue());
		log.info("Sending BloodCountLogEvent from @ConstraintHandler:\t" + logEvent);
		apply(logEvent);
	}

}
