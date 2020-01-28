package org.demo.pip;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.interpreter.pip.EthereumPolicyInformationPoint;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = "printer", description = "Domain specific PIP for printer usage")
public class EthereumPrinterPip extends EthereumPolicyInformationPoint {

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final String DOC_CONTRACT = "0x9CDD57201DB1110A09d44F675cA00acaB62E5cE7";

	private static final String ADDRESS = "address";

	private static final String BOOL = "bool";

	private static final String INPUT_PARAMS = "inputParams";

	private static final String OUTPUT_PARAMS = "outputParams";

	@Attribute(name = "certified", docs = "Checks, if the given address has a valid printer certificate.")
	public Flux<JsonNode> certified(JsonNode address, Map<String, JsonNode> variables) {
		ObjectNode requestNode = JSON.objectNode();
		requestNode.put("contractAddress", DOC_CONTRACT);
		requestNode.put("functionName", "hasCertificate");
		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put("type", ADDRESS);
		input1.put("value", address.textValue().substring(2));
		inputParams.add(input1);
		requestNode.set(INPUT_PARAMS, inputParams);
		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		requestNode.set(OUTPUT_PARAMS, outputParams);
		return loadContractInformation(requestNode, variables).map(j -> j.get(0).get("value"));
	}

}
