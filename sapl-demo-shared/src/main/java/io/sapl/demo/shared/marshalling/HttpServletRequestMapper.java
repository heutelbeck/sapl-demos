package io.sapl.demo.shared.marshalling;

import javax.servlet.http.HttpServletRequest;

import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestElement;
import io.sapl.spring.marshall.resource.HttpResource;

public class HttpServletRequestMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement element) {
		
		HttpServletRequest request = (HttpServletRequest) objectToMap;
		
		if(element == (SaplRequestElement.ACTION)) {
			return new HttpAction(request).getMethod();
		}
		
		if(element == (SaplRequestElement.RESOURCE)) {
			return new HttpResource(request).getUri();
		}
		
		return objectToMap;
	}


	@Override
	public Class<?> getMappedClass() {
		return HttpServletRequest.class;
	}

}
