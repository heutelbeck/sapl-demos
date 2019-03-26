package org.demo.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.sapl.spring.method.pre.PreEnforce;

@PreEnforce
@Repository
public interface JpaPatientRepository extends CrudRepository<Patient, Long>, PatientRepository {
}
