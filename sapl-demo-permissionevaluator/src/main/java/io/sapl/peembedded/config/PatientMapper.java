package io.sapl.peembedded.config;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.resource.PatientResource;
import io.sapl.spring.marshall.mapper.SaplClassMapper;
import io.sapl.spring.marshall.mapper.SaplRequestElement;

public class PatientMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement type) {
		
		Patient patient = (Patient) objectToMap;
		return new PatientResource(patient);


	}

	@Override
	public Class<?> getMappedClass() {
		return Patient.class;
	}

}
