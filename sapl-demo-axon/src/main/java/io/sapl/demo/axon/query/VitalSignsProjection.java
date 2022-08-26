package io.sapl.demo.axon.query;

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

import io.sapl.axon.annotation.EnforceDropUpdatesWhileDenied;
import io.sapl.demo.axon.command.MonitorAPI.MeasurementTaken;
import io.sapl.demo.axon.command.PatientCommandAPI.MonitorConnectedToPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.MonitorDisconnectedFromPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.PatientRegistered;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchSingleVitalSignOfPatient;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchVitalSignsOfPatient;
import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VitalSignsProjection {

	private final VitalSignsRepository repository;
	private final QueryUpdateEmitter   updateEmitter;

	@EventHandler
	void on(PatientRegistered evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		var vitals = new VitalSignsDocument(evt.id(), Map.of(), Set.of(), timestamp);
		saveAndUpdate(vitals);
	}

	@EventHandler
	void on(MeasurementTaken evt, @Timestamp Instant timestamp) {
		log.trace("Project: {}", evt);
		// monitorDeviceId, String type, String value, String unit, Instant timestamp
		var measurement = new Measurement(evt.monitorId(), evt.monitorType(), evt.value(), evt.unit(), timestamp);
		repository.findByMonitorId(evt.monitorId()).map(VitalSignsDocument.withMeasurement(measurement, timestamp))
				.ifPresentOrElse(v -> {
					saveAndUpdate(v);
					updateEmitter.emit(FetchSingleVitalSignOfPatient.class,
							q -> q.patientId().equals(v.patientId()) && q.type().equals(evt.monitorType()),
							measurement);
				}, () -> log.trace("No patient has monitor {} connected", evt.monitorId()));
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

	@QueryHandler
	@EnforceDropUpdatesWhileDenied(action = "'read'", resource = "{ 'type':'measurement', 'id':#payload.patientId(), 'monitorType':#payload.type() }")
	Optional<Measurement> handle(FetchSingleVitalSignOfPatient query) {
		return repository.findById(query.patientId()).map(v -> v.lastKnownMeasurements().get(query.type()));
	}

	@QueryHandler
	@EnforceRecoverableIfDenied(action = "'read'", resource = "{ 'type':'measurements', 'id':#payload.patientId() }")
	Optional<VitalSignsDocument> handle(FetchVitalSignsOfPatient query) {
		return repository.findById(query.patientId());
	}

	void updateVitals(String id, Function<VitalSignsDocument, VitalSignsDocument> update) {
		repository.findById(id).map(update).ifPresentOrElse(this::saveAndUpdate, logNotFound(id));
	}

	private Runnable logNotFound(Object o) {
		return () -> log.error("Not found: {}", o);
	}

	private void saveAndUpdate(VitalSignsDocument vitals) {
		repository.save(vitals);
		updateEmitter.emit(FetchVitalSignsOfPatient.class, idMatches(vitals.patientId()), vitals);
	}

	private Predicate<FetchVitalSignsOfPatient> idMatches(String id) {
		return query -> query.patientId().equals(id);
	}

}
