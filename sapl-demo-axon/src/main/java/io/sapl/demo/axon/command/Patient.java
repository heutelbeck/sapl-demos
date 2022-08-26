package io.sapl.demo.axon.command;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.HashSet;
import java.util.Set;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import io.sapl.demo.axon.command.PatientCommandAPI.ConnectMonitorToPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.DischargePatient;
import io.sapl.demo.axon.command.PatientCommandAPI.DisconnectMonitorFromPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.HospitalisePatient;
import io.sapl.demo.axon.command.PatientCommandAPI.MakeDiagnosisForPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.MonitorConnectedToPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.MonitorDisconnectedFromPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientDiagnosed;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientDischarged;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientHospitalised;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.command.PatientCommandAPI.RegisterPatient;
import lombok.NoArgsConstructor;

@Aggregate
@NoArgsConstructor
public class Patient {

	@AggregateIdentifier
	public String      id;
	public Set<String> connectedMonitors = new HashSet<>();
	public boolean     isHospitalized;
	public Ward        ward;

	@CommandHandler
	public Patient(RegisterPatient cmd) {
		apply(new PatientRegistered(cmd.id(), cmd.name(), Ward.NONE));
	}

	@CommandHandler
	void handle(HospitalisePatient cmd) {
		apply(new PatientHospitalised(cmd.id(), cmd.ward()));
	}

	@CommandHandler
	void handle(DischargePatient cmd) {
		apply(new PatientDischarged(cmd.id(), Ward.NONE));
	}

	@CommandHandler
	void handle(MakeDiagnosisForPatient cmd) {
		apply(new PatientDiagnosed(cmd.id(), cmd.doctor(), cmd.icd11Code(), cmd.diagnosisText()));
	}

	@CommandHandler
	void handle(ConnectMonitorToPatient cmd) {
		if (connectedMonitors.contains(cmd.monitorDeviceId()))
			throw new IllegalStateException(
					String.format("Monitor %s already connected to patient %s.", id, cmd.monitorDeviceId()));
		apply(new MonitorConnectedToPatient(cmd.id(), cmd.monitorDeviceId(), cmd.monitorType()));
	}

	@CommandHandler
	void handle(DisconnectMonitorFromPatient cmd) {
		if (!connectedMonitors.contains(cmd.monitorDeviceId()))
			throw new IllegalStateException(
					String.format("Monitor %s is not connected to patient %s.", id, cmd.monitorDeviceId()));
		apply(new MonitorDisconnectedFromPatient(cmd.id(), cmd.monitorDeviceId()));
	}

	@EventSourcingHandler
	void on(PatientRegistered evt) {
		this.id   = evt.id();
		this.ward = evt.ward();
	}

	@EventSourcingHandler
	void on(PatientHospitalised evt) {
		this.isHospitalized = true;
		this.ward           = evt.ward();
	}

	@EventSourcingHandler
	void on(PatientDischarged evt) {
		this.isHospitalized = false;
		this.ward           = evt.ward();
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
