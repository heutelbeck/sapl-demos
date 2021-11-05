package io.sapl.test.unit;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = TestPIP.NAME, description = TestPIP.DESCRIPTION)
public class TestPIP {

	public static final String NAME = "test";

	public static final String DESCRIPTION = "Policy information Point for testing";

	@Attribute
	public Flux<Val> upper(@Text Val value, Map<String, JsonNode> variables) {
		return Flux.just(Val.of(value.get().asText().toUpperCase()));
	}

}