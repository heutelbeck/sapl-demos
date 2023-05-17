package io.sapl.demo.axon.query.patients;

import org.springframework.data.repository.CrudRepository;

import io.sapl.demo.axon.query.patients.api.PatientDocument;

public interface PatientsRepository extends CrudRepository<PatientDocument, String>{

}
