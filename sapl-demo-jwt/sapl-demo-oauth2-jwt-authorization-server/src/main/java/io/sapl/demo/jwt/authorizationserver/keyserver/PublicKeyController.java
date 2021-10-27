package io.sapl.demo.jwt.authorizationserver.keyserver;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicKeyController {
	private static Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

	// TODO: replace with ? private final JWKSource<SecurityContext> jwkSource;
	private final KeyRepository keyRepo;

	@GetMapping(path = "/public-key/{keyId}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getPublicKey(@PathVariable String keyId) throws JOSEException {
		return publicKey(keyId);
	}

	private String publicKey(String keyId) throws JOSEException {
		var key = keyRepo.findById(keyId);
		if (key.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find key with ID " + keyId);
		return ENCODER.encodeToString(key.get().toRSAKey().toRSAPublicKey().getEncoded());
	}

}
