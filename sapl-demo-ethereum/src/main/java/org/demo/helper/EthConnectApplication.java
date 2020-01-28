package org.demo.helper;

import java.io.IOException;
import java.util.List;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthConnectApplication {

	private static final String ACCREDITATION_AUTHORITY = "0x3924F456CC0196ff89AAbbD6192289a9B37De73A";

	private static final String DOC_CONTRACT = "0x9CDD57201DB1110A09d44F675cA00acaB62E5cE7";

	public static void main(String[] args) {
		Web3j web3j = Web3j.build(new HttpService());
		List<String> accounts = null;
		try {
			accounts = web3j.ethAccounts().send().getAccounts();
		}
		catch (IOException e) {
			LOGGER.info("No accounts found.");
		}
		LOGGER.info("{}", accounts);

		// Authorize User 2
		Transaction transaction = new Transaction(accounts.get(0), null, null, null, accounts.get(1), null, null);
		web3j.ethSendTransaction(transaction);

	}

}
