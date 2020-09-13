package org.demo.domain;

import io.sapl.spring.method.pre.PreEnforce;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface MedicationRepository {

	@PreEnforce
	List<Medication> findAll();

	@PreEnforce
	Optional<Medication> findById(Long id);

	@PreEnforce
	Optional<List<Medication>> findByPatientId(Long patientId);

	@PreEnforce
	Medication save(Medication medication);

	@PreEnforce
	void deleteById(Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Medication m set m.status = ?1 where m.id = ?2")
	void updateStatusById(String status, Long id);

}
