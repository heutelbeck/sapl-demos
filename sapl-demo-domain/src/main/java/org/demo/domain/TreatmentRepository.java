package org.demo.domain;

import io.sapl.spring.method.pre.PreEnforce;

import java.util.List;
import java.util.Optional;

public interface TreatmentRepository {

	@PreEnforce
	List<Treatment> findAll();

	@PreEnforce
	Optional<Treatment> findById(Long id);

	@PreEnforce
	Optional<List<Treatment>> findByPatientId(Long patientId);

	@PreEnforce
	Optional<List<Treatment>> findByUsername(Long username);

	@PreEnforce
	Treatment save(Treatment treatment);

	@PreEnforce
	void deleteById(Long id);

}
