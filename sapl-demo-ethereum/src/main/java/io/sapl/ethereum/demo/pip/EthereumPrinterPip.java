/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.ethereum.demo.pip;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.JsonObject;
import io.sapl.ethereum.demo.helper.CertificateAddressProvider;
import io.sapl.ethereum.demo.views.mainview.MainView;
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
	public Flux<Val> certified(@JsonObject Val saplObject, Map<String, JsonNode> variables) {
		String address         = saplObject.get().get("address").textValue();
		String printer         = saplObject.get().get("printer").textValue();
		String contractAddress = getContractAddress(printer, variables);

		ObjectNode requestNode = JSON.objectNode();
		requestNode.put("contractAddress", contractAddress);
		requestNode.put("functionName", "hasCertificate");
		ArrayNode  inputParams = JSON.arrayNode();
		ObjectNode input1      = JSON.objectNode();
		input1.put("type", ADDRESS);
		input1.put("value", address.substring(2));
		inputParams.add(input1);
		requestNode.set(INPUT_PARAMS, inputParams);
		ArrayNode outputParams = JSON.arrayNode();
		outputParams.add(BOOL);
		requestNode.set(OUTPUT_PARAMS, outputParams);
		return loadContractInformation(Val.of(requestNode), variables).map(j -> j.get().get(0).get("value"))
				.map(Val::of);
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
