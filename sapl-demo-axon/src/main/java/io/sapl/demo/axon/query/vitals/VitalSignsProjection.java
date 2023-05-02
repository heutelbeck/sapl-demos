package io.sapl.demo.axon.query.vitals;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

import io.sapl.axon.annotation.EnforceRecoverableUpdatesIfDenied;
import io.sapl.axon.annotation.PreHandleEnforce;
import io.sapl.demo.axon.command.MonitorAPI.MeasurementTaken;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorConnectedToPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorDisconnectedFromPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;
import io.sapl.demo.axon.query.vitals.api.VitalSignsDocument;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.FetchVitalSignOfPatient;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.MonitorVitalSignOfPatient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VitalSignsProjection {

	private final VitalSignsRepository repository;
	private final QueryUpdateEmitter updateEmitter;

	@EventHandler
	void on(PatientRegistered evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		var vitals = new VitalSignsDocument(evt.id(), Map.of(), Set.of(), timestamp);
		saveAndUpdate(vitals);
	}

	@EventHandler
	void on(MonitorConnectedToPatient evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		updateVitals(evt.id(), VitalSignsDocument.withSensor(evt.monitorDeviceId(), timestamp));
	}

	@EventHandler
	void on(MonitorDisconnectedFromPatient evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		updateVitals(evt.id(), VitalSignsDocument.withoutSensor(evt.monitorDeviceId(), timestamp));
	}

	@EventHandler
	void on(MeasurementTaken evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		var measurement = new VitalSignMeasurement(evt.monitorId(), evt.monitorType(), evt.value(), evt.unit(),
				timestamp);
		repository.findByMonitorId(evt.monitorId()).map(VitalSignsDocument.withMeasurement(measurement, timestamp))
				.ifPresentOrElse(v -> {
					saveAndUpdate(v);
					updateEmitter.emit(MonitorVitalSignOfPatient.class,
							q -> q.patientId().equals(v.patientId()) && q.type() == evt.monitorType(), measurement);
				}, () -> log.trace("No patient has monitor {} connected", evt.monitorId()));
	}

	@QueryHandler
	@EnforceRecoverableUpdatesIfDenied(action = "'Monitor'", resource = "{ 'type':'measurement', 'id':#query.patientId(), 'monitorType':#query.type() }")
	Optional<VitalSignMeasurement> handle(MonitorVitalSignOfPatient query) {
		return repository.findById(query.patientId()).map(v -> v.lastKnownMeasurements().get(query.type()));
	}

	@QueryHandler
	@PreHandleEnforce(action = "'Fetch'", resource = "{ 'type':'measurement', 'id':#query.patientId(), 'monitorType':#query.type() }")
	Optional<VitalSignMeasurement> handle(FetchVitalSignOfPatient query) {
		return repository.findById(query.patientId())
				.flatMap(pVitals -> Optional.ofNullable(pVitals.lastKnownMeasurements().get(query.type())));
	}

	void updateVitals(String id, Function<VitalSignsDocument, VitalSignsDocument> update) {
		repository.findById(id).map(update).ifPresentOrElse(this::saveAndUpdate, logNotFound(id));
	}

	private Runnable logNotFound(Object o) {
		return () -> log.error("Not found: {}", o);
	}

	private void saveAndUpdate(VitalSignsDocument vitals) {
		repository.save(vitals);
		updateEmitter.emit(FetchVitalSignOfPatient.class, idMatches(vitals.patientId()), vitals);
	}

	private Predicate<FetchVitalSignOfPatient> idMatches(String id) {
		return query -> query.patientId().equals(id);
	}

}
