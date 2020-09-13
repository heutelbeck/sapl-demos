package org.demo.domain;

import io.sapl.spring.method.pre.PreEnforce;

import java.util.List;
import java.util.Optional;

public interface LaboratoryRepository {

	@PreEnforce
	List<Laboratory> findAll();

	@PreEnforce
	Optional<Laboratory> findById(Long id);

	@PreEnforce
	Optional<List<Laboratory>> findByPatientId(Long patientId);

	@PreEnforce
	Laboratory save(Laboratory laboratory);

	@PreEnforce
	void deleteById(Long id);

}
