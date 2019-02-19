package org.demo;

import org.demo.domain.Patient;

import lombok.Value;

@Value
public class PatientResource {

	int id;
	String name;
	String attendingNurse;
	String attendingDoctor;

	public PatientResource(Patient patient) {
		id = patient.getId();
		name = patient.getName();
		attendingNurse = patient.getAttendingNurse();
		attendingDoctor = patient.getAttendingDoctor();
	}

}
