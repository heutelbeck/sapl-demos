package io.sapl.demo.axon.query.vitals.api;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.sapl.demo.axon.command.MonitorType;

//@formatter:off
@Document
@JsonInclude(Include.NON_NULL)
public record VitalSignsDocument (
	@Id
	String      patientId,
	
	Map<MonitorType,VitalSignMeasurement> lastKnownMeasurements,
	Set<String> connectedSensors,
	
	Instant updatedAt
) {
	//@formatter:on
	public static Function<VitalSignsDocument, VitalSignsDocument> withSensor(String sensorId, Instant timestamp) {
		return vitals -> {
			var sensors = new HashSet<>(vitals.connectedSensors);
			sensors.add(sensorId);
			return new VitalSignsDocument(vitals.patientId, vitals.lastKnownMeasurements, sensors, timestamp);
		};
	}

	public static Function<VitalSignsDocument, VitalSignsDocument> withoutSensor(String sensorId, Instant timestamp) {
		return vitals -> {
			var sensors = new HashSet<>(vitals.connectedSensors);
			sensors.remove(sensorId);
			return new VitalSignsDocument(vitals.patientId, vitals.lastKnownMeasurements, sensors, timestamp);
		};
	}

	public static Function<VitalSignsDocument, VitalSignsDocument> withMeasurement(VitalSignMeasurement measurement,
			Instant timestamp) {
		return vitals -> {
			var measurements = new HashMap<>(vitals.lastKnownMeasurements);
			measurements.put(measurement.type(), measurement);
			return new VitalSignsDocument(vitals.patientId, measurements, vitals.connectedSensors, timestamp);
		};
	}

};
