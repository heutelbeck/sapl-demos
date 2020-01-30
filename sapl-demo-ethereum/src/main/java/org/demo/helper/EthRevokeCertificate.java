package org.demo.helper;

import java.math.BigInteger;

import org.demo.helper.contracts.Device_Operator_Certificate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthRevokeCertificate {

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final String GRADUATE = "0xE5a72C7Fa4991920619edCf25eD8828793045A53";

	private static final String DOC_CONTRACT = "0x9CDD57201DB1110A09d44F675cA00acaB62E5cE7";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	public static void main(String[] args) {
		Web3j web3j = Web3j.build(new HttpService());

		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(DOC_CONTRACT, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));

		try {
			contract.revokeCertificate(GRADUATE).send().getStatus();

		}
		catch (Exception e) {
			LOGGER.info("Exception occurred", e);
		}

	}

}
