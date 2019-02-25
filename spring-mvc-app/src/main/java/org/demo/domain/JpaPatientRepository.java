package org.demo.domain;

import org.springframework.data.repository.CrudRepository;

public interface JpaPatientRepository extends CrudRepository<Patient, Long>, PatientRepository {
}
