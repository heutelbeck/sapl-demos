package org.demo.domain;

import io.sapl.spring.method.pre.PreEnforce;

import java.util.List;
import java.util.Optional;

public interface FamiliarRepository {

	@PreEnforce
	Optional<List<Familiar>> findByPatientId(Long patientId);

	@PreEnforce
	Familiar save(Familiar familiar);

	@PreEnforce
	void deleteById(Long id);

}
