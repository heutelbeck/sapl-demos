package org.demo.helper;

import java.io.IOException;
import java.math.BigInteger;

import org.demo.MainView;
import org.demo.helper.contracts.GraftenOneCertificate;
import org.demo.helper.contracts.Ultimaker2ExtendedCertificate;
import org.demo.helper.contracts.ZmorphVXCertificate;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.StaticGasProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CertificateAddressProvider {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String CONFIG_PATH = "classpath:policies/pdp.json";

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final String ACCREDITATION_AUTHORITY = "0x3924F456CC0196ff89AAbbD6192289a9B37De73A";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	private static final StaticGasProvider gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);

	@Getter
	private String ultimakerAddress;

	@Getter
	private String graftenAddress;

	@Getter
	private String zmorphAddress;

	public CertificateAddressProvider(Web3j web3j) {
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);

		JsonNode variables = getVariables();
		if (variables != null) {
			JsonNode ethPipConfig = variables.get("ethPipConfig");
			if (ethPipConfig != null) {
				JsonNode uAddress = ethPipConfig.get(MainView.ULTIMAKER);
				if (uAddress != null)
					ultimakerAddress = uAddress.textValue();
				JsonNode gAddress = ethPipConfig.get(MainView.GRAFTEN);
				if (gAddress != null)
					graftenAddress = gAddress.textValue();
				JsonNode zAddress = ethPipConfig.get(MainView.ZMORPH);
				if (zAddress != null)
					zmorphAddress = zAddress.textValue();
			}
		}

		if (ultimakerAddress == null)
			ultimakerAddress = deployUltimakerContract(web3j, credentials);
		if (graftenAddress == null)
			graftenAddress = deployGraftenContract(web3j, credentials);
		if (zmorphAddress == null)
			zmorphAddress = deployZmorphContract(web3j, credentials);

	}

	private String deployUltimakerContract(Web3j web3j, Credentials credentials) {
		try {
			Ultimaker2ExtendedCertificate ultimakerCert = Ultimaker2ExtendedCertificate
					.deploy(web3j, credentials, gasProvider).send();
			ultimakerCert.addIssuer(ACCREDITATION_AUTHORITY).send();
			return ultimakerCert.getContractAddress();
		}
		catch (Exception e) {
			log.info("Could not deploy Ultimaker Certificate. Requests to this contract will not work.");
		}
		return "";
	}

	private String deployGraftenContract(Web3j web3j, Credentials credentials) {
		try {
			GraftenOneCertificate graftenCert = GraftenOneCertificate.deploy(web3j, credentials, gasProvider).send();
			graftenCert.addIssuer(ACCREDITATION_AUTHORITY).send();
			return graftenCert.getContractAddress();
		}
		catch (Exception e) {
			log.info("Could not deploy Graften Certificate. Requests to this contract will not work.");
		}
		return "";
	}

	private String deployZmorphContract(Web3j web3j, Credentials credentials) {
		try {
			ZmorphVXCertificate zmorphCert = ZmorphVXCertificate.deploy(web3j, credentials, gasProvider).send();
			zmorphCert.addIssuer(ACCREDITATION_AUTHORITY).send();
			return zmorphCert.getContractAddress();
		}
		catch (Exception e) {
			log.info("Could not deploy Zmorph Certificate. Requests to this contract will not work.");
		}
		return "";
	}

	private JsonNode getVariables() {
		try {
			JsonNode config = mapper.readValue(ResourceUtils.getFile(CONFIG_PATH), JsonNode.class);
			return config.get("variables");
		}
		catch (IOException | NullPointerException e) {
			log.info("No PDP configuration file found. Deploying contracts...");
		}
		return null;
	}

}
