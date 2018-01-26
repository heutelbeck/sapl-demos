package io.sapl.demo.shared.marshalling;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.sapl.demo.domain.Patient;
import io.sapl.spring.marshall.mapper.AuthenticationSerializer;
import io.sapl.spring.marshall.mapper.HttpServletRequestSerializer;
import io.sapl.spring.marshall.mapper.SaplMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class MapperUsageExample {

	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SaplMapper();

		SimpleModule authModule = new SimpleModule("AuthenticationSerializer", new Version(1, 0, 0, null, null, null));
		authModule.addSerializer(Authentication.class, new AuthenticationSerializer());
		saplMapper.registerModule(authModule);

		SimpleModule patientModule = new SimpleModule("PatientSerializer", new Version(1, 0, 0, null, null, null));
		patientModule.addSerializer(Patient.class, new PatientSerializer());
		saplMapper.registerModule(patientModule);

		SimpleModule httpServletRequestModule = new SimpleModule("HttpServletRequestSerializer",
				new Version(1, 0, 0, null, null, null));
		patientModule.addSerializer(HttpServletRequest.class, new HttpServletRequestSerializer());
		saplMapper.registerModule(httpServletRequestModule);

		return saplMapper;
	}
}
