package io.sapl.demo.axon.query.patients.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatientQueryAPI {

	// @formatter:off
	public record FetchAllPatients () {};
	public record FetchPatient 	   (String patientId) {};
	public record MonitorPatient   (String patientId) {};
	// @formatter:on

}
