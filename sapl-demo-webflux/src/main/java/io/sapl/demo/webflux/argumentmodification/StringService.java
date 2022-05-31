package io.sapl.demo.webflux.argumentmodification;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.PreEnforce;
import reactor.core.publisher.Mono;

@Service
public class StringService {

	@PreEnforce
	public Mono<String> lowercase(String aString) {
		return Mono.just(aString.toLowerCase());
	}
}
