package org.demo.helper;

import java.math.BigInteger;

import org.demo.helper.contracts.Device_Operator_Certificate;
import org.demo.pip.EthereumPrinterPip;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

public class AccessCertificate {

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	public static void issueCertificate(String address, String printer) {
		Web3j web3j = Web3j.build(new HttpService());
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = EthereumPrinterPip.getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.issueCertificate(address).sendAsync();
	}

	public static void revokeCertificate(String address, String printer) {
		Web3j web3j = Web3j.build(new HttpService());
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = EthereumPrinterPip.getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.revokeCertificate(address).sendAsync();
	}
}