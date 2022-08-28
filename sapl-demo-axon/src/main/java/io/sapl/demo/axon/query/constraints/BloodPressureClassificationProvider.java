package io.sapl.demo.axon.query.constraints;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constrainthandling.api.ResultConstraintHandlerProvider;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;

@Service
public class BloodPressureClassificationProvider implements ResultConstraintHandlerProvider<VitalSignMeasurement> {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint.isTextual() && "catrgorise blood pressure".equals(constraint.textValue());
	}

	@Override
	public Class<VitalSignMeasurement> getSupportedType() {
		return VitalSignMeasurement.class;
	}

	@Override
	public Object mapPayload(JsonNode constraint, Object payload, Class<?> clazz) {
		var measurement = (VitalSignMeasurement) payload;
		var split       = measurement.value().split("/");
		var systolic    = Double.valueOf(split[0]);
		var diastolic   = Double.valueOf(split[1]);

		if (systolic < 100 || diastolic < 60)
			return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Hypotension",
					"Blood Pressure Category", measurement.timestamp());

		if (systolic < 120 || diastolic < 80)
			return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Normal",
					"Blood Pressure Category", measurement.timestamp());

		if (systolic < 140 || diastolic < 90)
			return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Prehypertension",
					"Blood Pressure Category", measurement.timestamp());
		
		if (systolic < 160 || diastolic < 100)
			return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Stage 1 Hypertension",
					"Blood Pressure Category", measurement.timestamp());

		if (systolic < 180 || diastolic < 110)
			return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Stage 2 Hypertension",
					"Blood Pressure Category", measurement.timestamp());
		
		return new VitalSignMeasurement(measurement.monitorDeviceId(), measurement.type(), "Hypertension Crisis EMERGENCY",
					"Blood Pressure Category", measurement.timestamp());
	}

}
