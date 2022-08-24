package io.sapl.demo.axon.command;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.axon.annotation.ConstraintHandler;
import io.sapl.demo.axon.command.MedicalRecordAPI.AddClinicalRecordCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.AddClinicalRecordEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.BloodCountCreatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateBloodCountCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateMedicalRecordCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateMedicalRecordWithClinicalCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.MedicalRecordCreatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.MedicalRecordCreatedWithClinicalEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.MedicalRecordUpdatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.UpdateMedicalRecordCommand;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aggregate
@NoArgsConstructor
public class MedicalRecord {

	@AggregateIdentifier
	private String patientId;
	public String  patientName;
	public boolean hasClinicalRecordAvailable = false;

	@AggregateMember
	private final List<BloodCount> bloodExaminations = new ArrayList<>();

	@PreEnforce
	@CommandHandler
	public MedicalRecord(CreateMedicalRecordCommand command) {
		apply(new MedicalRecordCreatedEvent(command.getId(), command.getName()));
	}

	@CommandHandler
	@PreEnforce(action = "hasClinicalRecordAvailable")
	public MedicalRecord(CreateMedicalRecordWithClinicalCommand command) {
		apply(new MedicalRecordCreatedWithClinicalEvent(command.getId(), command.getName(),
				command.isHasClinicalRecordAvailable()));
	}

	@CommandHandler
	@PreEnforce(resource = "patientName")
	public void handle(UpdateMedicalRecordCommand command) {
		apply(new MedicalRecordUpdatedEvent(patientId, command.getPulse(), command.getOxygenSaturation()));
	}

	@PreEnforce
	@CommandHandler
	public void handle(CreateBloodCountCommand command) {
		apply(new BloodCountCreatedEvent(command.getExaminationId()));
	}

	@CommandHandler
	public void handle(AddClinicalRecordCommand command) {
		log.info("handle(AddClinicalRecordCommand)");
		apply(new AddClinicalRecordEvent());
	}

	@ConstraintHandler("#constraint.get('checkForEmployeesOnProbation').asText().equals('if no clinical record must be created')")
	public void checkIfClinicalRecordIsAvailable(UpdateMedicalRecordCommand command, JsonNode constraint,
			MetaData metaData, CommandBus commandBus, ObjectMapper mapper) {
		if (!hasClinicalRecordAvailable) {
			log.info("Doctor on probation + no clinical record available, create a clinical record");
			commandBus.dispatch(new GenericCommandMessage<>(new AddClinicalRecordCommand(patientId)));
		} else {
			log.info("Doctor is on probation, a clinical record is available");
		}
	}

	@ConstraintHandler("#constraint.get('type').asText().equals('logAccess')")
	public void logAccessForUser(UpdateMedicalRecordCommand command, JsonNode constraint, MetaData metaData,
			CommandBus commandBus, ObjectMapper mapper) {
		log.info(constraint.get("message").asText());
	}

	@EventSourcingHandler
	public void on(MedicalRecordCreatedEvent event) {
		patientId   = event.getId();
		patientName = event.getName();
	}

	@EventSourcingHandler
	public void on(AddClinicalRecordEvent event) {
		hasClinicalRecordAvailable = true;
	}

	@EventSourcingHandler
	public void on(MedicalRecordCreatedWithClinicalEvent event) {
		patientId                  = event.getId();
		hasClinicalRecordAvailable = event.isHasClinicalRecordAvailable();
	}

	@EventSourcingHandler
	public void handle(BloodCountCreatedEvent event) {
		bloodExaminations.add(new BloodCount(event.getExaminationId()));
	}
}
