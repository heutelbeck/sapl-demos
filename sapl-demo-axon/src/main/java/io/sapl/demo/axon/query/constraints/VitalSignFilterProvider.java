package io.sapl.demo.axon.query.constraints;

import java.util.Set;
import java.util.function.Predicate;

import org.axonframework.messaging.ResultMessage;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.constrainthandling.api.UpdateFilterConstraintHandlerProvider;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;

@Service
public class VitalSignFilterProvider implements UpdateFilterConstraintHandlerProvider {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		if (!constraint.isObject())
			return false;

		if (!(constraint.has("constraintType") || constraint.has("blockType")))
			return false;

		var constraintType = constraint.get("constraintType");
		if (!constraintType.isTextual() || !"filter vital sign type".equals(constraintType.textValue()))
			return false;

		return constraint.get("blockType").isTextual();
	}

	@Override
	public Set<ResponseType<?>> getSupportedResponseTypes() {
		return Set.of(ResponseTypes.instanceOf(VitalSignMeasurement.class));
	}

	@Override
	public Predicate<ResultMessage<?>> getHandler(JsonNode constraint) {
		var blockedType = constraint.get("blockType").textValue();

		return measurement -> !blockedType.equals(((VitalSignMeasurement) measurement.getPayload()).type().toString());
	}

}
