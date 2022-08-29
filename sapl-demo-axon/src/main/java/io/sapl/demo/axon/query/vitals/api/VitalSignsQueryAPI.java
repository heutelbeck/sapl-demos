package io.sapl.demo.axon.query.vitals.api;

import io.sapl.demo.axon.command.MonitorType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VitalSignsQueryAPI {

	// @formatter:off
	public record FetchVitalSignOfPatient 		(String patientId, MonitorType type) {};
	public record MonitorVitalSignOfPatient 	(String patientId, MonitorType type) {};
	// @formatter:on

}
