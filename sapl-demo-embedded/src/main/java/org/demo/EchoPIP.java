package org.demo;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "echo", description = "PIP echoing the input value")
public class EchoPIP {

	@Attribute(name = "echo")
	public Flux<JsonNode> echo(@Text JsonNode value, Map<String, JsonNode> variables) {
		return Flux.just(value);
	}

}
