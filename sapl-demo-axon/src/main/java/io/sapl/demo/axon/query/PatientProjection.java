package io.sapl.demo.axon.query;

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
import io.sapl.demo.axon.command.PatientCommandAPI.PatientDiagnosed;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientDischarged;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientHospitalised;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchAllPatients;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchPatient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientProjection {

	private final PatientsRepository patientsRepository;
	private final QueryUpdateEmitter updateEmitter;

	@EventHandler
	void on(PatientRegistered evt, @Timestamp Instant timestamp) {
		log.debug("Project: {}", evt);
		var patientDoc = new PatientDocument(evt.id(), evt.name(), null, null, evt.ward(), timestamp);
		saveAndUpdate(patientDoc);
	}

	@EventHandler
	void on(PatientHospitalised evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		updatePatient(evt.id(), PatientDocument.withWard(evt.ward(), timestamp));
	}

	@EventHandler
	void on(PatientDischarged evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		updatePatient(evt.id(), PatientDocument.withWard(evt.ward(), timestamp));
	}

	@EventHandler
	void on(PatientDiagnosed evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		updatePatient(evt.id(), PatientDocument.withIcdAndDisgnosis(evt.icd11Code(), evt.diagnosisText(), timestamp));
	}

	@QueryHandler
	//@EnforceDropUpdatesWhileDenied(action="'Fetch'", resource="{ 'type':'patient', 'id':#payload.patientId() }")
	@PostHandleEnforce(action="'Fetch'", resource="{ 'type':'patient', 'value':#queryResult }")
	Optional<PatientDocument> handle(FetchPatient query) {
		log.trace("Handle: {}", query); 
		return patientsRepository.findById(query.patientId());
	}

	@QueryHandler
	@PreHandleEnforce(action="'FetchAll'", resource="{ 'type':'patient' }")
	Iterable<PatientDocument> handle(FetchAllPatients query) {
		log.trace("Handle: {}", query);
		return patientsRepository.findAll();
	}

	private void updatePatient(String id, Function<PatientDocument, PatientDocument> update) {
		patientsRepository.findById(id).map(update).ifPresentOrElse(this::saveAndUpdate, logNotFound(id));
	}

	private void saveAndUpdate(PatientDocument patientDoc) {
		patientsRepository.save(patientDoc);
		updateEmitter.emit(FetchPatient.class, idMatches(patientDoc.id()), patientDoc);
	}

	private Runnable logNotFound(Object o) {
		return () -> log.error("Not found: {}", o);
	}

	private Predicate<FetchPatient> idMatches(String id) {
		return query -> query.patientId().equals(id);
	}

}
