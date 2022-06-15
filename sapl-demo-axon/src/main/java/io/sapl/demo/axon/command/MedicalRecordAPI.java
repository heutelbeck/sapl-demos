package io.sapl.demo.axon.command;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MedicalRecordAPI {

	/* Commands */

	@Value
	public static class CreateMedicalRecordCommand {
		@TargetAggregateIdentifier
		String id;
		String name;
	}

	@Value
	public static class CreateMedicalRecordWithClinicalCommand {
		@TargetAggregateIdentifier
		String  id;
		String  name;
		boolean hasClinicalRecordAvailable;
	}

	@Value
	public static class AddClinicalRecordCommand {
		@TargetAggregateIdentifier
		String id;
	}

	@Value
	public static class UpdateMedicalRecordCommand {
		@TargetAggregateIdentifier
		String id;
		double pulse;
		double oxygenSaturation;
	}

	@Value
	public static class UpdateMedicalRecordCommandConstraintHandler {
		@TargetAggregateIdentifier
		String id;
		double pulse;
		double oxygenSaturation;
	}

	@Value
	public static class CreateBloodCountCommand {
		@TargetAggregateIdentifier
		String id;
		int    examinationId;
	}

	@Value
	public static class UpdateBloodCountCommand {
		@TargetAggregateIdentifier
		String id;
		int    examinationId;
		double hematocritValue;
	}

	/* Events */

	@Value
	public static class MedicalRecordCreatedEvent {
		String id;
		String name;
	}

	@Value
	public static class MedicalRecordCreatedWithClinicalEvent {
		String  id;
		String  name;
		boolean hasClinicalRecordAvailable;

	}

	@Value
	public static class MedicalRecordUpdatedEvent {
		String id;
		double pulse;
		double oxygenSaturation;
	}

	@Value
	public static class AuditingEvent {
		CommandMessage<?> message;
	}

	@Value
	public static class MedicalRecordLogEvent {
		String id;
		String name;
	}

	@Value
	public static class BloodCountCreatedEvent {
		int examinationId;
	}

	@Value
	public static class BloodCountUpdatedEvent {
		double hematocritValue;
	}

	@Value
	public static class BloodCountLogEvent {
		int    examinationId;
		double hematocritValue;
	}

	@Value
	public static class AddClinicalRecordEvent {
	}

}
