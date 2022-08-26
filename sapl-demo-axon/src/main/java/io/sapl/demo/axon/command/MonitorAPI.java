package io.sapl.demo.axon.command;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MonitorAPI {
	// @formatter:off
	public record MeasurementTaken (String monitorId, String monitorType, String value, String unit) {};
	// @formatter:on

}
