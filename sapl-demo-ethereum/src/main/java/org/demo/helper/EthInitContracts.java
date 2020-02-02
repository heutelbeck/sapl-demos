package org.demo.helper;

import static org.demo.helper.AccessCertificate.getContractAddress;

import java.math.BigInteger;

import org.demo.MainView;
import org.demo.helper.contracts.GraftenOneCertificate;
import org.demo.helper.contracts.Ultimaker2ExtendedCertificate;
import org.demo.helper.contracts.ZmorphVXCertificate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthInitContracts {

	private static final String ACCREDITATION_AUTHORITY_PRIVATE_KEY = "7bb90c8b20c4bfdc5833c5e94b36ec3fa050346f04441878a323eec3483960c4";

	private static final String ACCREDITATION_AUTHORITY = "0x3924F456CC0196ff89AAbbD6192289a9B37De73A";

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);

	public static void main(String[] args) {
		Web3j web3j = Web3j.build(new HttpService());

		Credentials credentials = Credentials.create(ACCREDITATION_AUTHORITY_PRIVATE_KEY);
		Ultimaker2ExtendedCertificate ultimaker = Ultimaker2ExtendedCertificate.load(
				getContractAddress(MainView.ULTIMAKER), web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		GraftenOneCertificate graften = GraftenOneCertificate.load(getContractAddress(MainView.GRAFTEN), web3j,
				credentials, new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
		ZmorphVXCertificate zmorph = ZmorphVXCertificate.load(getContractAddress(MainView.ZMORPH), web3j, credentials,
				new StaticGasProvider(GAS_PRICE, GAS_LIMIT));

		try {
			String status1 = ultimaker.addIssuer(ACCREDITATION_AUTHORITY).sendAsync().get().getStatus();
			String status2 = graften.addIssuer(ACCREDITATION_AUTHORITY).sendAsync().get().getStatus();
			String status3 = zmorph.addIssuer(ACCREDITATION_AUTHORITY).sendAsync().get().getStatus();
			LOGGER.info("{}, {}, {}", status1, status2, status3);

		}
		catch (Exception e) {
			LOGGER.info("Exception occurred", e);
		}

	}

}
