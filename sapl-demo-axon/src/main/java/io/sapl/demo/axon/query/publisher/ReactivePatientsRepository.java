package io.sapl.demo.axon.query.publisher;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import io.sapl.demo.axon.query.patients.api.PatientDocument;

@Repository
public interface ReactivePatientsRepository extends ReactiveCrudRepository<PatientDocument, String>{
}
