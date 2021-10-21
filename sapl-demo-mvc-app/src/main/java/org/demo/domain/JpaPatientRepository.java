package org.demo.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPatientRepository extends CrudRepository<Patient, Long>, PatientRepository {

}
