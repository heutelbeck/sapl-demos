package org.demo.helper;

import java.math.BigInteger;

import org.demo.MainView;
import org.demo.helper.contracts.Device_Operator_Certificate;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.StaticGasProvider;

@Component
public class AccessCertificate {

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	private CertificateAddressProvider addressProvider;

	private Web3j web3j;

	public AccessCertificate(CertificateAddressProvider addressProvider, Web3j web3j) {
		this.addressProvider = addressProvider;
		this.web3j = web3j;
	}

	public void issueCertificate(String address, String printer) {
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.issueCertificate(address).sendAsync();
	}

	public void revokeCertificate(String address, String printer) {
		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		String contractAddress = getContractAddress(printer);
		Device_Operator_Certificate contract = Device_Operator_Certificate.load(contractAddress, web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		contract.revokeCertificate(address).sendAsync();
	}

	public String getContractAddress(String printer) {
		if (MainView.ULTIMAKER.equals(printer))
			return addressProvider.getUltimakerAddress();
		if (MainView.GRAFTEN.equals(printer))
			return addressProvider.getGraftenAddress();
		if (MainView.ZMORPH.equals(printer))
			return addressProvider.getZmorphAddress();
		return "";
	}
}