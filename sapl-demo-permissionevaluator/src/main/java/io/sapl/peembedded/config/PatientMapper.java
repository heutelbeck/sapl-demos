package io.sapl.peembedded.config;

import io.sapl.demo.domain.Patient;
import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestType;

public class PatientMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestType type) {
		

		return null;
	}

	@Override
	public String getMappedClass() {
		return Patient.class.toString();
	}

}
