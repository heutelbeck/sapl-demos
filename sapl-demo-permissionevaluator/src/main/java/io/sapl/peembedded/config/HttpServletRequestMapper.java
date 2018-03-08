package io.sapl.peembedded.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestElement;
import io.sapl.spring.marshall.resource.HttpResource;

public class HttpServletRequestMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement type) {
		
		HttpServletRequest request = (HttpServletRequest) objectToMap;
		
		if(type.equals(SaplRequestElement.ACTION)) {
			return new HttpAction(request);
		}
		
		if(type.equals(SaplRequestElement.RESOURCE)) {
			return new HttpResource(request);
		}
		
		return objectToMap;
	}


	@Override
	public Class<?> getMappedClass() {
		return HttpServletRequest.class;
	}

}
