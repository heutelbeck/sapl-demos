package io.sapl.demo.axon.query.patients;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.sapl.demo.axon.query.patients.api.PatientDocument;

@Repository
public interface PatientsRepository extends CrudRepository<PatientDocument, String>{

}
