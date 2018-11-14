package io.sapl.demo.shared.resource;

import io.sapl.demo.domain.Patient;
import io.sapl.spring.marshall.Resource;
import lombok.Value;

@Value
public class PatientResource implements Resource {

	int id;
	String name;
	String healthRecordNumber;
	String attendingNurse;
	String attendingDoctor;

	public PatientResource(Patient patient) {
		id = patient.getId();
		name = patient.getName();
		healthRecordNumber = patient.getHealthRecordNumber();
		attendingNurse = patient.getAttendingNurse();
		attendingDoctor = patient.getAttendingDoctor();

	}

}
