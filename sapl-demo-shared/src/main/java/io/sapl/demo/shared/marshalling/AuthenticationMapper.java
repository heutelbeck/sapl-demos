package io.sapl.demo.shared.marshalling;

import org.springframework.security.core.Authentication;

import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestElement;
import io.sapl.spring.marshall.subject.AuthenticationSubject;

public class AuthenticationMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement type) {
		Authentication authentication = (Authentication) objectToMap;
		return new AuthenticationSubject(authentication);
	}

	@Override
	public Class<?> getMappedClass() {
		return Authentication.class;
	}
	
	
	
	

}
