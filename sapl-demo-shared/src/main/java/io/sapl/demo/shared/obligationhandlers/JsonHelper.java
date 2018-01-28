package io.sapl.demo.shared.obligationhandlers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class JsonHelper {

	public static Optional<String> getValue(JsonNode node, String key) {
		if (node.has(key)) {
			return Optional.ofNullable(node.get(key).asText());
		}
		return Optional.empty();
	}

	public static String tryGetValue(JsonNode node, String key) {
		return getValue(node, key).orElse(null);
	}

}
