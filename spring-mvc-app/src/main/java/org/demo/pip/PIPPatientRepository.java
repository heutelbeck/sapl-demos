package org.demo.pip;

import org.demo.domain.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PIPPatientRepository extends CrudRepository<Patient, Long> {
}