package io.sapl.demo.geo.marshall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestElement;

public class HttpServletRequestMapper implements SaplClassMapper {

	private static final String HTTP_METHOD = "httpMethod";
	private static final String HTTP_URI = "httpUri";
	private static final String AC_REG = "acReg";
	private static final String AC_REG_STD = "DATBF";
	private static final int ONE_AND_ONLY = 0;

	@Override
	public Object map(Object objectToMap, SaplRequestElement element) {

		HttpServletRequest request = (HttpServletRequest) objectToMap;
		ObjectMapper jsonMapper = new ObjectMapper();

		if (element == (SaplRequestElement.ACTION)) {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put(HTTP_METHOD, request.getMethod());
			paramMap.put(HTTP_URI, request.getRequestURI());
			return jsonMapper.convertValue(paramMap, JsonNode.class);
		}

		if (element == (SaplRequestElement.RESOURCE)) {
			Map<String, Object> paramMap = new HashMap<>();
			request.getParameterMap().entrySet().stream()
					.forEach(entry -> paramMap.put(entry.getKey(), stringToType(entry.getValue()[ONE_AND_ONLY])));

			// aircraft registration would normally be assigned according to flight number
			paramMap.put(AC_REG, AC_REG_STD);

			return jsonMapper.convertValue(paramMap, JsonNode.class);
		}

		return objectToMap;
	}

	@Override
	public Class<?> getMappedClass() {
		return HttpServletRequest.class;
	}

	private static Object stringToType(String string) {
		if (StringUtils.isNumeric(string)) {
			return Integer.valueOf(string);
		} else {
			return string;
		}
	}
}
