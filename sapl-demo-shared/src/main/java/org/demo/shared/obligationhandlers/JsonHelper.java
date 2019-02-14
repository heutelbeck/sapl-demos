package org.demo.shared.obligationhandlers;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.experimental.UtilityClass;

@UtilityClass
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
