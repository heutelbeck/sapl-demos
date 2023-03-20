package io.sapl.demo.axon.command.patient;

import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import io.sapl.demo.axon.command.MonitorType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PatientCommandAPI {

	// Commands

	// @formatter:off
	public record RegisterPatient    			(@TargetAggregateIdentifier String id, String name) {}
	public record HospitalisePatient			(@TargetAggregateIdentifier	String id, Ward ward) {}
	public record DischargePatient				(@TargetAggregateIdentifier String id) {}
	public record MakeDiagnosisForPatient 		(@TargetAggregateIdentifier String id, String doctor, 
																			String icd11Code, String diagnosisText) {}
	public record ConnectMonitorToPatient 		(@TargetAggregateIdentifier String id, String monitorDeviceId, MonitorType monitorType) {}
	public record DisconnectMonitorFromPatient	(@TargetAggregateIdentifier String id, String monitorDeviceId) {}
	// @formatter:on

	// Events

	// @formatter:off
	public record PatientRegistered    			(String id, String name, Ward ward) {}
	public record PatientHospitalised			(String id, Ward ward) {}
	public record PatientDischarged				(String id, Ward ward) {}
	public record PatientDiagnosed		 		(String id, String doctor, String icd11Code, String diagnosisText) {}
	public record MonitorConnectedToPatient		(String id, String monitorDeviceId, MonitorType monitorType) {}
	public record MonitorDisconnectedFromPatient(String id, String monitorDeviceId) {}
	public record SuspiciousManipulation        (String id, String user) {}
	// Snapshot
	public record PatientSnapshot               (String id, Set<String> connectedMonitors, Ward ward) {}
	// @formatter:on
}
