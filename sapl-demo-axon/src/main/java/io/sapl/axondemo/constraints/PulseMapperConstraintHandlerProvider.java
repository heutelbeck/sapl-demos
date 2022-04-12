package io.sapl.axondemo.constraints;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axondemo.domain.MedicalRecordAPI;
import io.sapl.spring.constraints.api.MappingConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PulseMapperConstraintHandlerProvider implements MappingConstraintHandlerProvider<MedicalRecordAPI.PulseRecord> {

    @Override
    public Function<MedicalRecordAPI.PulseRecord, MedicalRecordAPI.PulseRecord> getHandler(JsonNode constraint) {
        return record -> {
            log.info("mapping the pulse value");
            double pulse = record.getPulse();
            double pulseMapped;
            if (pulse < 65) pulseMapped =  0.0;
            else if (pulse < 120) pulseMapped =  1.0;
            else  pulseMapped = 2.0;
            return new MedicalRecordAPI.PulseRecord(pulseMapped);
        };
    }

    @Override
    public boolean isResponsible(JsonNode constraint) {
        return constraint != null && constraint.has("applyMapping")
                && constraint.get("applyMapping").asText().equals("map pulse to range");
    }

    @Override
    public Class<MedicalRecordAPI.PulseRecord> getSupportedType() {
        return MedicalRecordAPI.PulseRecord.class;
    }
}
