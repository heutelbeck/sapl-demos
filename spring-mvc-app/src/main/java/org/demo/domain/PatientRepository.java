package org.demo.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.sapl.spring.annotation.EnforcePolicies;

public interface PatientRepository {
	@EnforcePolicies(resultResource = true)
	Optional<Patient> findById(Long id);

	Optional<Patient> findByName(String name);

	List<Patient> findAll();

	Patient save(Patient patient);

	@EnforcePolicies
	void deleteById(Long id);

	@Modifying
	@Query("update Patient p set p.name = ?1 where p.id = ?2")
	void updateNameById(String name, Long id);

	@Modifying
	@Query("update Patient p set p.diagnosisText = ?1 where p.id = ?2")
	void updateDiagnosisTextById(String diagnosisText, Long id);

	@Modifying
	@Query("update Patient p set p.icd11Code = ?1 where p.id = ?2")
	void updateIcd11CodeById(String icd11Code, Long id);

	@Modifying
	@Query("update Patient p set p.phoneNumber = ?1 where p.id = ?2")
	void updatePhoneNumberById(String phoneNumber, Long id);

	@Modifying
	@Query("update Patient p set p.attendingDoctor = ?1 where p.id = ?2")
	void updateAttendingDoctorById(String attendingDoctor, Long id);

	@Modifying
	@Query("update Patient p set p.attendingNurse = ?1 where p.id = ?2")
	void updateAttendingNurseById(String attendingNurse, Long id);
}
