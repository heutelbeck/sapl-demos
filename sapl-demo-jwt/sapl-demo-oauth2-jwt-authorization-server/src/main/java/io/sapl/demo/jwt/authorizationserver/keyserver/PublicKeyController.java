package io.sapl.demo.jwt.authorizationserver.keyserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublicKeyController {

	SigningKeyRepository keyRepo;

	@PostMapping(path = "/public-key/{keyId}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getPublicKey(@PathVariable String keyId) {
		log.info("Incomming public-key-request: " + keyId);
		var key = keyRepo.findById(keyId);
		if (key.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find key with ID " + keyId);
		return key.get().getEncodedPublicKey();
	}
}
