package io.sapl.demo.axon.iface.rest;

import java.util.List;

import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.axon.command.PatientCommandAPI.DischargePatient;
import io.sapl.demo.axon.command.PatientCommandAPI.HospitalisePatient;
import io.sapl.demo.axon.command.Ward;
import io.sapl.demo.axon.query.Measurement;
import io.sapl.demo.axon.query.PatientDocument;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchAllPatients;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchPatient;
import io.sapl.demo.axon.query.PatientQueryAPI.FetchVitalSignsOfPatient;
import io.sapl.demo.axon.query.PatientQueryAPI.MonitorSingleVitalSignOfPatient;
import io.sapl.demo.axon.query.VitalSignsDocument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class PatientsController {

	private final ReactorQueryGateway   queryGateway;
	private final ReactorCommandGateway commandGateway;

	@GetMapping("/api/patients")
	Mono<List<PatientDocument>> fetchAllPatients() {
		return queryGateway.query(new FetchAllPatients(), ResponseTypes.multipleInstancesOf(PatientDocument.class));
	}

	@GetMapping("/api/patients/{id}")
	Mono<PatientDocument> fetchPatient(@PathVariable String id) {
		return queryGateway.query(new FetchPatient(id), ResponseTypes.instanceOf(PatientDocument.class));
	}

	@GetMapping("/api/patients/{id}/vitals")
	Mono<VitalSignsDocument> fetchVitals(@PathVariable String id) {
		return queryGateway.query(new FetchVitalSignsOfPatient(id), ResponseTypes.instanceOf(VitalSignsDocument.class));
	}

	@GetMapping("/api/patients/{id}/vitals/stream")
	Flux<ServerSentEvent<VitalSignsDocument>> streamVitals(@PathVariable String id) {
		return queryGateway
				.subscriptionQuery(new FetchVitalSignsOfPatient(id), ResponseTypes.instanceOf(VitalSignsDocument.class),
						ResponseTypes.instanceOf(VitalSignsDocument.class))
				.flatMapMany(result -> Flux.concat(result.initialResult(), result.updates()))
				.map(view -> ServerSentEvent.<VitalSignsDocument>builder().data(view).build());
	}

	@GetMapping("/api/patients/{id}/vitals/{type}/stream")
	Flux<ServerSentEvent<Measurement>> streamSingleVital(@PathVariable String id, @PathVariable String type) {
		return queryGateway
				.subscriptionQuery(new MonitorSingleVitalSignOfPatient(id, type),
						ResponseTypes.instanceOf(Measurement.class), ResponseTypes.instanceOf(Measurement.class))
				.flatMapMany(result -> Flux.concat(result.initialResult(), result.updates()))
				.map(view -> ServerSentEvent.<Measurement>builder().data(view).build());
	}

	@GetMapping(value = "/api/patients/{id}/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
	Flux<ServerSentEvent<PatientDocument>> streamPatient(@PathVariable String id) {
		return queryGateway
				.subscriptionQuery(new FetchPatient(id), ResponseTypes.instanceOf(PatientDocument.class),
						ResponseTypes.instanceOf(PatientDocument.class))
				.flatMapMany(result -> Flux.concat(result.initialResult(), result.updates()))
				.map(view -> ServerSentEvent.<PatientDocument>builder().data(view).build());
	}

	@PostMapping("/api/patients/{id}/hospitalise/{ward}")
	Mono<Object> hospitalizePatient(@PathVariable String id, @PathVariable Ward ward) {
		return commandGateway.send(new HospitalisePatient(id, ward));
	}

	@PostMapping("/api/patients/{id}/discharge")
	Mono<Object> hospitalizePatient(@PathVariable String id) {
		return commandGateway.send(new DischargePatient(id));
	}

}
