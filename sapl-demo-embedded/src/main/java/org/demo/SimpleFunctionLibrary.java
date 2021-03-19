package org.demo;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionLibrary;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.interpreter.Val;
import io.sapl.api.validation.Number;
import io.sapl.api.validation.Text;

@FunctionLibrary(name = "simple", description = "some simple functions")
public class SimpleFunctionLibrary {

	@Function
	public Val length(Val parameter) {
		if (parameter.isArray()) {
			return Val.of(parameter.get().size());
		} else if (parameter.isTextual()) {
			return Val.of(parameter.get().asText().length());
		} else {
			throw new PolicyEvaluationException("length() parameter must be a string or an array, found "
					+ (parameter.isUndefined() ? "undefined" : parameter.get().getNodeType()) + ".");
		}
	}

	@Function
	public Val append(@Text @Number Val... parameters) {
		var builder = new StringBuilder();
		for (var parameter : parameters) {
			if (parameter.isTextual()) {
				builder.append(parameter.get().asText());
			} else if (parameter.isNumber()) {
				builder.append(parameter.get().asInt());
			}
		}
		return Val.of(builder.toString());
	}

}
