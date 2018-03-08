package io.sapl.peembedded.config;

import javax.servlet.http.HttpServletRequest;

import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestType;
import io.sapl.spring.marshall.resource.HttpResource;

public class HttpServletRequestMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestType type) {
		
		HttpServletRequest request = (HttpServletRequest) objectToMap;
		
		if(type.equals(SaplRequestType.ACTION)) {
			return new HttpAction(request);
		}
		
		if(type.equals(SaplRequestType.RESOURCE)) {
			return new HttpResource(request);
		}
		
		return objectToMap;
	}


	@Override
	public String getMappedClass() {
		return HttpServletRequest.class.toString();
	}

}
