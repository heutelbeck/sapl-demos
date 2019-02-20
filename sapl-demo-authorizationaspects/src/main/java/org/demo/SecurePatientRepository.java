package org.demo;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.springframework.stereotype.Repository;

import io.sapl.spring.annotation.EnforcePolicies;

@Repository
public interface SecurePatientRepository extends PatientRepository {
	@EnforcePolicies(resultResource=true)
	public Patient findById(int id);
}
