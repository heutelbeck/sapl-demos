package io.sapl.demo.jwt.authorizationserver.keyserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.JWK;

@Component
public class KeyRepository {

	private Map<String, JWK> keysById = new HashMap<>();

	public void add(JWK key) {
		keysById.put(key.getKeyID(), key);
	}

	public Optional<JWK> findById(String keyId) {
		return Optional.ofNullable(keysById.get(keyId));
	}
}
