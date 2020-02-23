package org.demo.pip;

import java.util.Map;

import org.demo.MainView;
import org.demo.helper.CertificateAddressProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.interpreter.pip.EthereumPolicyInformationPoint;
import reactor.core.publisher.Flux;

@Component
@PolicyInformationPoint(name = "printer", description = "Domain specific PIP for printer usage")
public class EthereumPrinterPip extends EthereumPolicyInformationPoint {

	private static final String ETH_PIP_CONFIG = "ethPipConfig";

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final String ADDRESS = "address";

	private static final String BOOL = "bool";

	private static final String INPUT_PARAMS = "inputParams";

	private static final String OUTPUT_PARAMS = "outputParams";

	@Autowired
	private CertificateAddressProvider addressProvider;

	public EthereumPrinterPip(Web3j web3j) {
		super(web3j);
	}

	@Attribute(name = "certified", docs = "Checks, if the given address has a valid printer certificate.")
	public Flux<JsonNode> certified(JsonNode saplObject, Map<String, JsonNode> variables) {
		String address = saplObject.get("address").textValue();
		String printer = saplObject.get("printer").textValue();
		String contractAddress = getContractAddress(printer, variables);

		ObjectNode requestNode = JSON.objectNode();
		requestNode.put("contractAddress", contractAddress);
		requestNode.put("functionName", "hasCertificate");
		ArrayNode inputParams = JSON.arrayNode();
		ObjectNode input1 = JSON.objectNode();
		input1.put("type", ADDRESS);
		input1.put("value", address.substring(2));
		inputParams.add(input1);
		requestNode.set(INPUT_PARAMS, inputParams);
		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		requestNode.set(OUTPUT_PARAMS, outputParams);
		return loadContractInformation(requestNode, variables).map(j -> j.get(0).get("value"));
	}

	private String getContractAddress(String printer, Map<String, JsonNode> variables) {
		if (MainView.ULTIMAKER.equals(printer)) {
			JsonNode ethPipConfig = variables.get(ETH_PIP_CONFIG);
			if (ethPipConfig != null) {
				JsonNode address = ethPipConfig.get(MainView.ULTIMAKER);
				if (address != null)
					return address.textValue();
			}
			return addressProvider.getUltimakerAddress();
		}
		if (MainView.GRAFTEN.equals(printer)) {
			JsonNode ethPipConfig = variables.get(ETH_PIP_CONFIG);
			if (ethPipConfig != null) {
				JsonNode address = ethPipConfig.get(MainView.GRAFTEN);
				if (address != null)
					return address.textValue();
			}
			return addressProvider.getGraftenAddress();
		}
		if (MainView.ZMORPH.equals(printer)) {
			JsonNode ethPipConfig = variables.get(ETH_PIP_CONFIG);
			if (ethPipConfig != null) {
				JsonNode address = ethPipConfig.get(MainView.ZMORPH);
				if (address != null)
					return address.textValue();
			}
			return addressProvider.getZmorphAddress();
		}
		return "";
	}

}
