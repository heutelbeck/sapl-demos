package io.sapl.demo.shared.marshalling;

import org.springframework.security.core.Authentication;

import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.subject.AuthenticationSubject;

public class AuthenticationMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap) {
		Authentication authentication = (Authentication) objectToMap;
		return new AuthenticationSubject(authentication);
	}

	@Override
	public String getMappedClass() {
		return Authentication.class.toString();
	}
	
	

}
