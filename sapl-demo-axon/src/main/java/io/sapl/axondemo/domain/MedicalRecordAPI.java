package io.sapl.axondemo.domain;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MedicalRecordAPI {

	/* Commands */

	@Value
	public static class CreateMedicalRecord {
		@TargetAggregateIdentifier
		String id;
		String name;
	}
	
	@Value
	public static class CreateMedicalRecordWithClinical {
		@TargetAggregateIdentifier
		String id;
		String name;
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
		int examinationId;
	}

	@Value
	public static class UpdateBloodCount {
		@TargetAggregateIdentifier
		String id;
		int examinationId;
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
		String id;
		String name;
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
		int examinationId;
		double hematocritValue;
	}

	@Value
	public static class AddClinicalRecordEvent {
	}

	/* Queries */
	
	@Value
	public static class FetchMedicalRecordSummariesQuery {
		int offset;
		int limit;
	}

	@Value
	public static class FetchMedicalRecordSummaryQuery {
		String id;
	}

	@Value
	public static class FetchPulseQuery {
		String id;
	}

	@Value
	public static class FetchSinglePulseQuery {
		String id;
	}

	@Value
	public static class FetchOxygenSaturationQuery {
		String id;
	}


	public static class CountMedicalRecordSummariesQuery {
	}

	@Value
	public static class CountMedicalRecordSummariesResponse {
		int count;
		long lastEvent;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MedicalRecordSummary {
		String id;
		String patientName;
		double pulse;
		double oxygenSaturation;

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReducedRecord {
		String patientName;
		double value;
	}

	public static class CountChangedUpdate {

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PulseRecord {
		double pulse;
	}


}
