package io.sapl.demo.axon.query;

import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.PulseRecord;
import io.sapl.spring.constraints.api.MappingConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("backend")
public class PulseMapperConstraintHandlerProvider implements MappingConstraintHandlerProvider<PulseRecord> {

	@Override
	public Function<PulseRecord, PulseRecord> getHandler(JsonNode constraint) {
		return record -> {
			double pulse = record.getPulse();
			double pulseMapped;
			if (pulse < 65)
				pulseMapped = 0.0;
			else if (pulse < 120)
				pulseMapped = 1.0;
			else
				pulseMapped = 2.0;
			log.info("mapping the pulse value: " + pulse + " -> " + pulseMapped);
			return new PulseRecord(pulseMapped);
		};
	}

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint != null && constraint.has("applyMapping")
				&& constraint.get("applyMapping").asText().equals("map pulse to range");
	}

	@Override
	public Class<PulseRecord> getSupportedType() {
		return PulseRecord.class;
	}
}
