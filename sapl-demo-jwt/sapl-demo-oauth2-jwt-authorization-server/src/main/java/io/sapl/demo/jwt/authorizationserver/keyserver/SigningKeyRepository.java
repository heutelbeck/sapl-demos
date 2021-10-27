package io.sapl.demo.jwt.authorizationserver.keyserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.sapl.demo.jwt.authorizationserver.keyserver.SigningKey.SigningKeyType;

@Component
public class SigningKeyRepository {

	private Map<String, SigningKey> keysById = new HashMap<>();
	private Map<String, Collection<SigningKey>> keysByAlgorithm = new HashMap<>();

	public void add(SigningKey key) {
		var keys = keysByAlgorithm.get(key.getAlgorithm());
		if (keys == null)
			keys = new ArrayList<>();
		keys.add(key);
		keysByAlgorithm.put(key.getAlgorithm(), keys);
		keysById.put(key.getId(), key);
	}

	public Collection<SigningKey> findAllByAlgorithm(String algorithm) {
		var keys = keysByAlgorithm.get(algorithm);
		return keys == null ? List.of() : List.copyOf(keys);
	}

	public Optional<SigningKey> findByAlgorithmAndType(String algorithm, SigningKeyType type) {
		var keys = findAllByAlgorithm(algorithm);
		var typedKeys = keys.stream().filter(key -> key.getType() == type).collect(Collectors.toUnmodifiableList());
		return typedKeys.isEmpty() ? Optional.empty() : Optional.of(typedKeys.get(0));
	}

	public Optional<SigningKey> findById(String keyId) {
		return Optional.ofNullable(keysById.get(keyId));
	}
}
