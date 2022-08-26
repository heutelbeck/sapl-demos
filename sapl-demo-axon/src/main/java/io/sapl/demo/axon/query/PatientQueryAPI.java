package io.sapl.demo.axon.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatientQueryAPI {

	// @formatter:off
	public record FetchAllPatients 					() {};
	public record FetchPatient 						(String patientId) {};
	public record FetchVitalSignsOfPatient 			(String patientId) {};
	public record MonitorAllVitalSignsOfPatient 	(String patientId) {};
	public record FetchSingleVitalSignOfPatient 	(String patientId, String type) {};
	// @formatter:on
	
}
