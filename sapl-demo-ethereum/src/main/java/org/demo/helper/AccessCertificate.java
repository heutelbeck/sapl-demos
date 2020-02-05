package org.demo.helper;

import java.io.IOException;
import java.math.BigInteger;

import org.demo.MainView;
import org.demo.helper.contracts.Device_Operator_Certificate;
import org.springframework.util.ResourceUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessCertificate {

	private static final String CONFIG_PATH = "classpath:policies/pdp.json";

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	public static void issueCertificate(String address, String printer) {
		Web3j web3j = Web3j.build(new HttpService());
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.issueCertificate(address).sendAsync();
	}

	public static void revokeCertificate(String address, String printer) {
		Web3j web3j = Web3j.build(new HttpService());
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.revokeCertificate(address).sendAsync();
	}

	public static String getContractAddress(String printer) {
		JsonNode variables = JSON.objectNode();
		try {
			JsonNode config = mapper.readValue(ResourceUtils.getFile(CONFIG_PATH), JsonNode.class);
			LOGGER.info("{}", config);
			variables = config.get("variables");
			LOGGER.info("{}", variables);
			if (MainView.ULTIMAKER.equals(printer)) {
				return variables.get(MainView.ULTIMAKER).textValue();
			}
			if (MainView.GRAFTEN.equals(printer))
				return variables.get(MainView.GRAFTEN).textValue();
			if (MainView.ZMORPH.equals(printer))
				return variables.get(MainView.ZMORPH).textValue();
		} catch (IOException e) {
			LOGGER.info("Conifguration file for contracts not found.");
		}

		return "";
	}
}