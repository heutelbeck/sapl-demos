package io.sapl.demo.axon.command.patient;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.HashSet;
import java.util.Set;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.annotation.ConstraintHandler;
import io.sapl.axon.annotation.PreHandleEnforce;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.ConnectMonitorToPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.DischargePatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.DisconnectMonitorFromPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.HospitalisePatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MakeDiagnosisForPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorConnectedToPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorDisconnectedFromPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientDiagnosed;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientDischarged;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientHospitalised;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.RegisterPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.SuspiciousManipulation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aggregate(snapshotTriggerDefinition = "patientSnapshotTrigger")
@NoArgsConstructor
public class Patient {

	@AggregateIdentifier
	public String      id;
	public Set<String> connectedMonitors = new HashSet<>();
	public Ward        ward;

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'RegisterPatient'}", resource = "{ 'type':'Patient', 'id':#command.id(), 'ward':'NONE' }")
	public Patient(RegisterPatient cmd) {
		apply(new PatientRegistered(cmd.id(), cmd.name(), Ward.NONE));
	}

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'HospitalisePatient', 'ward':#command.ward()}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
	void handle(HospitalisePatient cmd) {
		apply(new PatientHospitalised(cmd.id(), cmd.ward()));
	}

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'DischargePatient'}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
	void handle(DischargePatient cmd) {
		apply(new PatientDischarged(cmd.id(), Ward.NONE));
	}

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'MakeDiagnosisForPatient'}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
	void handle(MakeDiagnosisForPatient cmd) {
		apply(new PatientDiagnosed(cmd.id(), cmd.doctor(), cmd.icd11Code(), cmd.diagnosisText()));
	}

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'ConnectMonitorToPatient'}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
	void handle(ConnectMonitorToPatient cmd) {
		if (connectedMonitors.contains(cmd.monitorDeviceId()))
			throw new IllegalStateException(
					String.format("Monitor %s already connected to patient %s.", id, cmd.monitorDeviceId()));
		apply(new MonitorConnectedToPatient(cmd.id(), cmd.monitorDeviceId(), cmd.monitorType()));
	}

	@CommandHandler
	@PreHandleEnforce(action = "{'command':'DisconnectMonitorFromPatient'}", resource = "{ 'type':'Patient', 'id':id, 'ward':ward }")
	void handle(DisconnectMonitorFromPatient cmd) {
		if (!connectedMonitors.contains(cmd.monitorDeviceId()))
			throw new IllegalStateException(
					String.format("Monitor %s is not connected to patient %s.", id, cmd.monitorDeviceId()));
		apply(new MonitorDisconnectedFromPatient(cmd.id(), cmd.monitorDeviceId()));
	}

	@ConstraintHandler("#constraint.get('type').textValue() == 'documentSuspisiousManipulation'")
	public void handleSuspiciousManipulation(JsonNode constraint) {
		log.debug("Handling constraint: {}", constraint);
		apply(new SuspiciousManipulation(id, constraint.get("username").textValue()));
	}

	@EventSourcingHandler
	void on(PatientRegistered evt) {
		this.id   = evt.id();
		this.ward = evt.ward();
	}

	@EventSourcingHandler
	void on(PatientHospitalised evt) {
		this.ward = evt.ward();
	}

	@EventSourcingHandler
	void on(PatientDischarged evt) {
		this.ward = evt.ward();
	}

	@EventSourcingHandler
	void on(MonitorConnectedToPatient evt) {
		this.connectedMonitors.add(evt.monitorDeviceId());
	}

	@EventSourcingHandler
	void on(MonitorDisconnectedFromPatient evt) {
		this.connectedMonitors.remove(evt.monitorDeviceId());
	}

}
