package io.sapl.demo.axon.iface.rest;

import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.axon.command.MonitorType;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.FetchVitalSignOfPatient;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.MonitorVitalSignOfPatient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class VitalSignsController {

	private final ReactorQueryGateway queryGateway;

	@GetMapping("/api/patients/{id}/vitals/{type}")
	Mono<ResponseEntity<VitalSignMeasurement>> fetchVitals(@PathVariable String id, @PathVariable MonitorType type) {
		return queryGateway
				.query(new FetchVitalSignOfPatient(id, type), ResponseTypes.instanceOf(VitalSignMeasurement.class))
				.map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/api/patients/{id}/vitals/{type}/stream")
	Flux<ServerSentEvent<VitalSignMeasurement>> streamSingleVital(@PathVariable String id,
			@PathVariable MonitorType type) {
		return queryGateway
				.subscriptionQuery(new MonitorVitalSignOfPatient(id, type),
						ResponseTypes.instanceOf(VitalSignMeasurement.class),
						ResponseTypes.instanceOf(VitalSignMeasurement.class))
				.flatMapMany(result -> Flux.concat(result.initialResult(), result.updates()))
				.map(view -> ServerSentEvent.<VitalSignMeasurement>builder().data(view).build());
	}
}
