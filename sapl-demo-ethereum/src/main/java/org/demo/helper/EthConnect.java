package org.demo.helper;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthConnect {

	private static final String ACCREDITATION_AUTHORITY = "0x3924F456CC0196ff89AAbbD6192289a9B37De73A";

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final String ALICE_KEY = "0xE5a72C7Fa4991920619edCf25eD8828793045A53";

	private static final String ALICE_PRIVATE_KEY = "f59fb234666f3394909918eadcc8e1a3918597a2b32e212ad84035fbf0ab43b6";

	private static final String BOB_KEY = "0xC4991aAE3621aadE30b9f577c6DA66698bFB7cD8";

	private static final String BOB_PRIVATE_KEY = "a5e729c5ad3500fd6b8a5ecc7ab7a21190fe2f4595aa52e6c3b8615420e6ddfe";

	private static final String GRADUATE = "0xE5a72C7Fa4991920619edCf25eD8828793045A53";

	private static final String DOC_CONTRACT = "0x9CDD57201DB1110A09d44F675cA00acaB62E5cE7";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	public static void makeDonation(String address, String value) {
		Web3j web3j = Web3j.build(new HttpService());
		Credentials credentials = getCredentials(address);
		BigDecimal amount = new BigDecimal(value);
		System.out.println(amount);

		try {
			Transfer.sendFunds(web3j, credentials, ACCREDITATION_AUTHORITY, amount, Convert.Unit.ETHER).send();
		}
		catch (Exception e) {
			LOGGER.info("Donation failed {}", e);
		}

	}

	private static Credentials getCredentials(String address) {
		if (ALICE_KEY.equalsIgnoreCase(address))
			return Credentials.create(ALICE_PRIVATE_KEY);
		if (BOB_KEY.equalsIgnoreCase(address))
			return Credentials.create(BOB_PRIVATE_KEY);
		return null;

	}

}
