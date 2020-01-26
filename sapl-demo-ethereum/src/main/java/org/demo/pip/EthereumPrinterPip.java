package org.demo.pip;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.interpreter.pip.EthereumPolicyInformationPoint;

@PolicyInformationPoint(name = "printer", description = "Domain specific PIP for printer usage")
public class EthereumPrinterPip extends EthereumPolicyInformationPoint {

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	public JsonNode certified(JsonNode saplObject, Map<String, JsonNode> variables) {
		return JSON.booleanNode(false);

	}

}
