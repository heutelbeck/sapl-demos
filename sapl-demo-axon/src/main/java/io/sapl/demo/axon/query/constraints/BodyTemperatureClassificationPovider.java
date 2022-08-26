package io.sapl.demo.axon.query.constraints;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constrainthandling.api.ResultConstraintHandlerProvider;
import io.sapl.demo.axon.query.Measurement;

@Service
public class BodyTemperatureClassificationPovider implements ResultConstraintHandlerProvider<Measurement> {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint.isTextual() && "catrgorise body temperature".equals(constraint.textValue());
	}

	@Override
	public Class<Measurement> getSupportedType() {
		return Measurement.class;
	}

	@Override
	public Object mapPayload(Object payload, Class<?> clazz) {
		var measurement     = (Measurement) payload;
		var bodyTemperature = Double.valueOf(measurement.value());
		var unit            = measurement.value();
		if ("°F".equals(unit))
			bodyTemperature = (bodyTemperature - 32.0D) * 0.556D;
		else if ("K".equals(unit))
			bodyTemperature = bodyTemperature + 273.15D;
		else if ("°C".equals(unit))
			throw new IllegalArgumentException(
					"Body temperature measurement in unknown unit. Unly supports °C, °K, K. Was: " + unit);

		if (bodyTemperature < 35.0D)
			return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Hypothermia",
					"Body Temperature Category", measurement.timestamp());

		if (bodyTemperature <= 37.5D)
			return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Normal",
					"Body Temperature Category", measurement.timestamp());

		if (bodyTemperature <= 38.3D)
			return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Hyperthermia",
					"Body Temperature Category", measurement.timestamp());

		if (bodyTemperature <= 40.0D)
			return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Fever",
					"Body Temperature Category", measurement.timestamp());

		if (bodyTemperature <= 41.5D)
			return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Hyperpyrexia",
					"Body Temperature Category", measurement.timestamp());

		return new Measurement(measurement.monitorDeviceId(), measurement.type(), "Critical Emergency",
				"Body Temperature Category", measurement.timestamp());

	}
}
