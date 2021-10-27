package io.sapl.demo.jwt.authorizationserver.keyserver;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import com.heutelbeck.uuid.Base64Id;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.StandardException;

@NoArgsConstructor
@AllArgsConstructor
public class SigningKey {

	private static Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
	private static Base64.Decoder decoder = Base64.getUrlDecoder();
	private static final String cipherTransformation = "AES/CBC/PKCS5PADDING";
	private static final String aesEncryptionAlgorithem = "AES";

	private static final Map<String, String> algorithmNameMapping = Map.of("RS256", "RSA", "HS256", "HS256");

	@Getter
	private String id;
	@Getter
	private SigningKeyType type = SigningKeyType.NONE;
	@Getter
	private String algorithm;
	private String secretKey;
	private String publicKey;

	public SigningKey(KeyPair keys, SignatureAlgorithm algo, String encryptionKey) throws SigningKeyException {
		if (!keys.getPrivate().getAlgorithm().equals(keys.getPublic().getAlgorithm()))
			throw new SigningKeyException("Public and Private keys do not match!");
		this.id = Base64Id.randomID();
		this.algorithm = algo.getName();
		this.type = SigningKeyType.ASYMKEYPAIR;
		this.publicKey = encoder.encodeToString(keys.getPublic().getEncoded()).replaceAll("=", "");
		this.secretKey = encrypt(keys.getPrivate().getEncoded(), encryptionKey);
	}

	public SigningKey(PrivateKey privateKey, SignatureAlgorithm algo, String encryptionKey) throws SigningKeyException {
		this.id = Base64Id.randomID();
		this.algorithm = algo.getName();
		this.type = SigningKeyType.ASYMPRIVATE;
		this.publicKey = null;
		this.secretKey = encrypt(privateKey.getEncoded(), encryptionKey);
	}

	public SigningKey(PublicKey publicKey, SignatureAlgorithm algo) {
		this.id = Base64Id.randomID();
		this.algorithm = algo.getName();
		this.type = SigningKeyType.ASYMPUBLIC;
		this.publicKey = encoder.encodeToString(publicKey.getEncoded()).replaceAll("=", "");
		this.secretKey = null;
	}

	public SigningKey(SecretKey secretKey, MacAlgorithm algo, String encryptionKey) throws SigningKeyException {
		this.id = Base64Id.randomID();
		this.algorithm = algo.getName();
		this.type = SigningKeyType.SYMMETRIC;
		this.publicKey = null;
		this.secretKey = encrypt(secretKey.getEncoded(), encryptionKey);
	}

	public SigningKey(OctetSequenceKey secretKey, MacAlgorithm algo, String encryptionKey) throws SigningKeyException {
		this.id = Base64Id.randomID();
		this.algorithm = algo.getName();
		this.type = SigningKeyType.SYMMETRIC;
		this.publicKey = null;
		this.secretKey = encrypt(secretKey.toByteArray(), encryptionKey);
	}

	public String getEncodedPublicKey() {
		if (this.publicKey == null || this.publicKey.isBlank())
			return "";
		else
			return this.publicKey;
	}

	public SecretKey getSymmetricKey(String decryptionKey) throws SigningKeyException {
		if (this.type != SigningKeyType.SYMMETRIC)
			throw new SigningKeyException("Key not of type SYMMETRIC!");
		return new SecretKeySpec(decrypt(this.secretKey, decryptionKey), this.algorithm);
	}

	public PublicKey getPublicKey() throws SigningKeyException {
		if (this.type != SigningKeyType.ASYMKEYPAIR && this.type != SigningKeyType.ASYMPUBLIC)
			throw new SigningKeyException("Key not of type ASYMKEYPAIR or ASYMPUBLIC!");
		try {
			return KeyFactory.getInstance(algorithmNameMapping.get(this.algorithm))
					.generatePublic(new X509EncodedKeySpec(decoder.decode(this.publicKey), this.algorithm));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new SigningKeyException(e.getLocalizedMessage());
		}
	}

	public PrivateKey getPrivateKey(String decryptionKey) throws SigningKeyException {
		if (this.type != SigningKeyType.ASYMKEYPAIR && this.type != SigningKeyType.ASYMPRIVATE)
			throw new SigningKeyException("Key not of type ASYMKEYPAIR or ASYMPRIVATE!");
		try {
			return KeyFactory.getInstance(algorithmNameMapping.get(this.algorithm))
					.generatePrivate(new PKCS8EncodedKeySpec(decrypt(this.secretKey, decryptionKey), this.algorithm));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new SigningKeyException(e.getLocalizedMessage());
		}
	}

	private String encrypt(byte[] plain, String encryptionKey) throws SigningKeyException {
		try {
			Cipher cipher = Cipher.getInstance(cipherTransformation);
			byte[] key = decoder.decode(encryptionKey);
			SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
			IvParameterSpec ivparameterspec = new IvParameterSpec(key);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
			byte[] cipherText = cipher.doFinal(plain);
			return encoder.encodeToString(cipherText).replaceAll("=", "");
		} catch (Exception e) {
			throw new SigningKeyException(e.getLocalizedMessage());
		}
	}

	private byte[] decrypt(String encryptedText, String decryptionKey) throws SigningKeyException {
		try {
			Cipher cipher = Cipher.getInstance(cipherTransformation);
			byte[] key = decoder.decode(decryptionKey);
			SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
			IvParameterSpec ivparameterspec = new IvParameterSpec(key);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
			byte[] cipherText = decoder.decode(encryptedText.getBytes(StandardCharsets.UTF_8));
			return cipher.doFinal(cipherText);
		} catch (Exception e) {
			throw new SigningKeyException(e.getLocalizedMessage());
		}
	}

	public enum SigningKeyType {
		NONE, SYMMETRIC, ASYMPUBLIC, ASYMPRIVATE, ASYMKEYPAIR
	}

	@StandardException
	public class SigningKeyException extends Exception {
	}
}