package io.sapl.demo.axon.query.constraints;

import java.util.function.Predicate;

import org.axonframework.messaging.ResultMessage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constrainthandling.api.UpdateFilterConstraintHandlerProvider;
import io.sapl.demo.axon.query.Measurement;

@Service
public class VitalSignFilterProvider implements UpdateFilterConstraintHandlerProvider<Measurement> {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		if(!constraint.isObject())
			return false;
		
		if (!(constraint.has("constraintType") || constraint.has("blockType")))
				return false;
		
		var constraintType =constraint.get("constraintType");
		if(!constraintType.isTextual() || !"filter vital sign type".equals(constraintType.textValue()))
			return false;
		
		return constraint.get("blockType").isTextual();
	}

	@Override
	public Class<Measurement> getSupportedType() {
		return Measurement.class;
	}

	@Override
	public Predicate<ResultMessage<Measurement>> getHandler(JsonNode constraint) {
		var blockedType = constraint.get("blockType").textValue();
		return measurement -> !blockedType.equals(measurement.getPayload().type());
	}

}
