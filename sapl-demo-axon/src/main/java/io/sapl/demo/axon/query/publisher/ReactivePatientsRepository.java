package io.sapl.demo.axon.query.publisher;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.sapl.demo.axon.query.patients.api.PatientDocument;

public interface ReactivePatientsRepository extends ReactiveCrudRepository<PatientDocument, String> {
}
