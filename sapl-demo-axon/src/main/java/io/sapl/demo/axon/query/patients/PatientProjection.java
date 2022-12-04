package io.sapl.demo.axon.query.patients;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import io.sapl.axon.annotation.PostHandleEnforce;
import io.sapl.axon.annotation.PreHandleEnforce;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientDiagnosed;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientDischarged;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientHospitalised;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.query.patients.api.PatientDocument;
import io.sapl.demo.axon.query.patients.api.PatientQueryAPI.FetchAllPatients;
import io.sapl.demo.axon.query.patients.api.PatientQueryAPI.FetchPatient;
import io.sapl.demo.axon.query.patients.api.PatientQueryAPI.MonitorPatient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientProjection {

	private final PatientsRepository patientsRepository;
	private final QueryUpdateEmitter updateEmitter;

	@EventHandler
	void on(PatientRegistered event, @Timestamp Instant timestamp) {
		log.trace("Project: {}", event);
		var patientDocument = new PatientDocument(event.id(), event.name(), null, null, event.ward(), timestamp);
		saveAndUpdate(patientDocument);
	}

	@EventHandler
	void on(PatientHospitalised event, @Timestamp Instant timestamp) {
		log.trace("Project: {}", event);
		updatePatient(event.id(), PatientDocument.withWard(event.ward(), timestamp));
	}

	@EventHandler
	void on(PatientDischarged event, @Timestamp Instant timestamp) {
		log.trace("Project: {}", event);
		updatePatient(event.id(), PatientDocument.withWard(event.ward(), timestamp));
	}

	@EventHandler
	void on(PatientDiagnosed event, @Timestamp Instant timestamp) {
		log.trace("Project: {}", event);
		updatePatient(event.id(), PatientDocument.withIcdAndDisgnosis(event.icd11Code(), event.diagnosisText(), timestamp));
	}

	@QueryHandler
	@PostHandleEnforce(action = "'Fetch'", resource = "{ 'type':'patient', 'value':#queryResult }")
	Optional<PatientDocument> handle(FetchPatient query) {
		log.trace("Handle: {}", query);
		return patientsRepository.findById(query.patientId());
	}

	@QueryHandler
	@PreHandleEnforce(action = "'FetchAll'", resource = "{ 'type':'patient' }")
	Iterable<PatientDocument> handle(FetchAllPatients query) {
		log.trace("Handle: {}", query);
		return patientsRepository.findAll();
	}

	@QueryHandler
	@PreHandleEnforce(action = "'Monitor'", resource = "{ 'type':'patient', 'id':#query.patientId() }")
	Optional<PatientDocument> handle(MonitorPatient query) {
		log.trace("Handle: {}", query);
		return patientsRepository.findById(query.patientId());
	}

	private void updatePatient(String id, Function<PatientDocument, PatientDocument> update) {
		patientsRepository.findById(id).map(update).ifPresentOrElse(this::saveAndUpdate, logNotFound(id));
	}

	private void saveAndUpdate(PatientDocument patientDocument) {
		patientsRepository.save(patientDocument);
		updateEmitter.emit(MonitorPatient.class, idMatches(patientDocument.id()), patientDocument);
	}

	private Predicate<MonitorPatient> idMatches(String id) {
		return query -> query.patientId().equals(id);
	}

	private Runnable logNotFound(Object o) {
		return () -> log.error("Not found: {}", o);
	}
}
