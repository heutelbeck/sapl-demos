package org.demo.domain;

import io.sapl.spring.method.pre.PreEnforce;

import java.util.List;
import java.util.Optional;

public interface ImagingRepository {

	@PreEnforce
	List<Imaging> findAll();

	@PreEnforce
	Optional<Imaging> findById(Long id);

	@PreEnforce
	Optional<List<Imaging>> findByPatientId(Long patientId);

	@PreEnforce
	Optional<List<Imaging>> findByImageType(String imageType);

	@PreEnforce
	Imaging save(Imaging imaging);

	@PreEnforce
	void deleteById(Long id);

}
