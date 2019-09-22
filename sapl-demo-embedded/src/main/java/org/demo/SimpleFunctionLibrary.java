package org.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionException;
import io.sapl.api.functions.FunctionLibrary;
import io.sapl.api.validation.Number;
import io.sapl.api.validation.Text;

@FunctionLibrary(name = "simple", description = "some simple functions")
public class SimpleFunctionLibrary {

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	@Function
	public JsonNode length(JsonNode parameter) throws FunctionException {
		JsonNode result = null;
		if (parameter.isArray()) {
			result = JSON.numberNode(parameter.size());
		}
		else if (parameter.isTextual()) {
			result = JSON.numberNode(parameter.asText().length());
		}
		else {
			throw new FunctionException(
					"length() parameter must be a string or an array, found "
							+ parameter.getNodeType() + ".");
		}
		return result;
	}

	@Function
	public JsonNode append(@Text @Number JsonNode... parameters) throws FunctionException {
		StringBuilder builder = new StringBuilder();
		for (JsonNode parameter : parameters) {
			if (parameter.isTextual()) {
				builder.append(parameter.asText());
			}
			else if (parameter.isNumber()) {
				builder.append(parameter.asInt());
			}
		}
		return JSON.textNode(builder.toString());
	}

}
