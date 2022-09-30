package io.sapl.demo.axon.query.publisher;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import io.sapl.axon.annotation.PreHandleEnforce;
import io.sapl.demo.axon.query.patients.api.PatientDocument;
import io.sapl.demo.axon.query.publisher.api.PublisherAPI.StreamAllPatients;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class PublisherPatientsQueryHandler {
	private final ReactivePatientsRepository repository;

	@QueryHandler
	@PreHandleEnforce(action = "'FetchAll'", resource = "{ 'type':'patient' }")
	Flux<PatientDocument> handle(StreamAllPatients query) {
		return repository.findAll();
	}
}
