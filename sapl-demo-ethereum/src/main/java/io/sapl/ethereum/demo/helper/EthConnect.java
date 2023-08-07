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
package io.sapl.ethereum.demo.helper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import io.sapl.ethereum.demo.security.PrinterUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EthConnect {

	private static final String ACCREDITATION_AUTHORITY = "0x3924F456CC0196ff89AAbbD6192289a9B37De73A";

	private static final String ALICE_KEY = "0xE5a72C7Fa4991920619edCf25eD8828793045A53";

	private static final String ALICE_PRIVATE_KEY = "f59fb234666f3394909918eadcc8e1a3918597a2b32e212ad84035fbf0ab43b6";

	private static final String BOB_KEY = "0xC4991aAE3621aadE30b9f577c6DA66698bFB7cD8";

	private static final String BOB_PRIVATE_KEY = "a5e729c5ad3500fd6b8a5ecc7ab7a21190fe2f4595aa52e6c3b8615420e6ddfe";

	private final Web3j web3j;

	public EthConnect(Web3j web3j) {
		this.web3j = web3j;
	}

	public void makeDonation(PrinterUser user, String value) {
		String      address     = user.getEthereumAddress();
		Credentials credentials = getCredentials(address);
		BigDecimal  amount      = new BigDecimal(value);
		log.info("Amount: {}", amount);

		try {
			Transfer.sendFunds(web3j, credentials, ACCREDITATION_AUTHORITY, amount, Convert.Unit.ETHER).send();

		} catch (Exception e) {
			log.warn("Donation failed", e);
		}

	}

	public void makePayment(PrinterUser user, String value) {
		String      address     = user.getEthereumAddress();
		Credentials credentials = getCredentials(address);
		BigDecimal  amount      = new BigDecimal(value);
		log.info("Amount: {}", amount);

		try {
			TransactionReceipt receipt = Transfer
					.sendFunds(web3j, credentials, ACCREDITATION_AUTHORITY, amount, Convert.Unit.ETHER).send();
			user.setTransactionHash(receipt.getTransactionHash());
		} catch (Exception e) {
			log.warn("Payment failed", e);
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
