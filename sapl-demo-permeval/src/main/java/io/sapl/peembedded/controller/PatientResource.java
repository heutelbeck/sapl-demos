package io.sapl.peembedded.controller;

import io.sapl.demo.domain.Patient;
import io.sapl.spring.marshall.Resource;
import lombok.Value;

@Value
public class PatientResource implements Resource {

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
