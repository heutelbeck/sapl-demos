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

import io.sapl.axon.annotations.ConstraintHandler;
import io.sapl.demo.axon.command.MedicalRecordAPI.AddClinicalRecordCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.AddClinicalRecordEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.BloodCountCreatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateBloodCountCommand;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateMedicalRecord;
import io.sapl.demo.axon.command.MedicalRecordAPI.CreateMedicalRecordWithClinical;
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
	private String                 patientId;
	@AggregateMember
	private final List<BloodCount> bloodExaminations          = new ArrayList<>();
	private boolean                hasClinicalRecordAvailable = false;

	@PreEnforce
	@CommandHandler
	public MedicalRecord(CreateMedicalRecord command) {
		apply(new MedicalRecordCreatedEvent(command.getId(), command.getName()));
	}

	@PreEnforce(action = "hasClinicalRecordAvailable")
	@CommandHandler
	public MedicalRecord(CreateMedicalRecordWithClinical command) {
		apply(new MedicalRecordCreatedWithClinicalEvent(command.getId(), command.getName(),
				command.isHasClinicalRecordAvailable()));
	}

	@PreEnforce(resource = "patientName")
	@CommandHandler
	public void handle(UpdateMedicalRecordCommand command) {
		apply(new MedicalRecordUpdatedEvent(patientId, command.getPulse(), command.getOxygenSaturation()));
	}

	@PreEnforce
	@CommandHandler
	public void handle(CreateBloodCountCommand command) {
		apply(new MedicalRecordAPI.BloodCountCreatedEvent(command.getExaminationId()));
	}

	@CommandHandler
	public void handle(AddClinicalRecordCommand command) {
		log.info("handle(AddClinicalRecordCommand)");
		apply(new MedicalRecordAPI.AddClinicalRecordEvent());
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

	@EventSourcingHandler
	public void on(MedicalRecordCreatedEvent event) {
		patientId = event.getId();
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
