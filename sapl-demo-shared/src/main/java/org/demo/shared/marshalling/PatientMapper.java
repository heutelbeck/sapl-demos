package org.demo.shared.marshalling;

import org.demo.domain.Patient;
import org.demo.shared.resource.PatientResource;

import io.sapl.api.pdp.mapping.SaplClassMapper;
import io.sapl.api.pdp.mapping.SaplRequestElement;

public class PatientMapper implements SaplClassMapper {

	@Override
	public Object map(Object objectToMap, SaplRequestElement element) {
		Patient patient = (Patient) objectToMap;
		return new PatientResource(patient);
	}

	@Override
	public Class<?> getMappedClass() {
		return Patient.class;
	}

}
