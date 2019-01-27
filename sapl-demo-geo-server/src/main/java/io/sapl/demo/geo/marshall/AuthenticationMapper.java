package io.sapl.demo.geo.marshall;

import io.sapl.api.pdp.mapping.SaplClassMapper;
import io.sapl.api.pdp.mapping.SaplRequestElement;
import io.sapl.spring.marshall.subject.AuthenticationSubject;
import org.springframework.security.core.Authentication;

public class AuthenticationMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement element) {
		Authentication authentication = (Authentication) objectToMap;
		return new AuthenticationSubject(authentication).getAsJson();
	}

	@Override
	public Class<?> getMappedClass() {
		return Authentication.class;
	}
}
