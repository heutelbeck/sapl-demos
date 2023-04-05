package io.sapl.argumentmodification.demo;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.PreEnforce;

@Service
public class StringService {

	@PreEnforce
	public String lowercase(String aString) {
		return aString.toLowerCase();
	}
}
