package io.sapl.demo.shared.marshalling;

import io.sapl.api.pdp.mapping.SaplClassMapper;
import io.sapl.api.pdp.mapping.SaplRequestElement;
import io.sapl.demo.domain.Patient;
import io.sapl.demo.shared.resource.PatientResource;

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
