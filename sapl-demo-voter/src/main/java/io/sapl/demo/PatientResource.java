package io.sapl.demo;

import io.sapl.api.pdp.marshall.Resource;
import io.sapl.demo.domain.Patient;
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
